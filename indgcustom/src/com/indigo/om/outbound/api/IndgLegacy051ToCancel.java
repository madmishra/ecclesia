package com.indigo.om.outbound.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;

/**
 * 
 * @author BGS168
 *
 * Custom API to consume LegacyOMS051 message and cancel the given lines 
 * The lines will be grouped by ReasonCode and send it to ChangeOrder API.
 * The lines will be grouped by ShipNode to sends SAP051 message to SAP.
 * 
 */
public class IndgLegacy051ToCancel extends AbstractCustomApi{
	 Map<String,List<YFCElement>> orderLineMapGroupByReasonCode = new HashMap<>();
	 Map<String,List<YFCElement>> orderLineMapGroupByShipNode = new HashMap<>();
	 private static final String SUBLINE_VALUE = "1";
	 private static final String ACTION_VALUE = "CANCEL";
	 private static final String EMPTY_STRING = "";
	 private static final String CALL_SAP051_SERVICE = "Indg_SAP051_OnLegacy051";
	 private static final String CALL_LEGACYOMS051_SERVICE = "Indg_LegacyOMS051_ForLegacy052";
	 private String isFullOrderCancelled = "";
	 private static final String CANCELLED = "Cancelled";
	 private static final String NO = "N";
	 private static final String YES = "Y";
	 private static final String REASON_CODE = "03";
	 private String orderNo = "";
	 private String documentType = "";
	 private String enterpriseCode = "";
	 private String orderType = "";
	 YFCDocument docLegacy051Input = null;
	 
	 /**
	  * This method is the invoke point of the service.
	  * 
	  */
	 
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		orderNo = inXml.getDocumentElement().getAttribute(XMLLiterals.ORDER_NO);
		orderType = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).
				getAttribute(XMLLiterals.ORDER_TYPE);
		enterpriseCode = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).
				getAttribute(XMLLiterals.ENTERPRISE_CODE);
	    documentType = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).
	    		getAttribute(XMLLiterals.DOCUMENT_TYPE);
		String inputDocString = inXml.toString();
	    docLegacy051Input = YFCDocument.getDocumentFor(inputDocString);
	    
	    YFCDocument shipmentListApiOp = getShipmentList();
	    getOrderLinesGroupByReasonCode(shipmentListApiOp);
	    docCancelOrderLines();
	    
	    YFCDocument.getDocumentFor(inputDocString);
		docSAP051GetAttributes(inXml);
		
		return inXml;
	}
	
	/**
	 * This method forms the Input XML for getShipmentListAPI
	 * 
	 * @return
	 */
	
	public YFCDocument inputXmlForGetShipmentList() {
	    YFCDocument getShipmentListDoc = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
	    YFCElement shipmentLinesEle = getShipmentListDoc.getDocumentElement().createChild(XMLLiterals.SHIPMENT_LINES);
	    YFCElement shipmentLineEle = shipmentLinesEle.createChild(XMLLiterals.SHIPMENT_LINE);
	    shipmentLineEle.setAttribute(XMLLiterals.ORDER_NO, orderNo);
	    return getShipmentListDoc;
	  }
	
	/**
	  * This method forms template for the getShipmentListAPI
	  * 
	  * @return
	  */
	
	public YFCDocument inputTemplateForGetShipmentList() {
	    YFCDocument getShipmentListTemp = YFCDocument.createDocument(XMLLiterals.SHIPMENTS);
	    YFCElement shipmentEle = getShipmentListTemp.getDocumentElement().createChild(XMLLiterals.SHIPMENT);
	    YFCElement shipmentLinesEle = shipmentEle.createChild(XMLLiterals.SHIPMENT_LINES);
	    YFCElement shipmentLineEle = shipmentLinesEle.createChild(XMLLiterals.SHIPMENT_LINE);
	    shipmentLineEle.setAttribute(XMLLiterals.BACKROOM_PICK_COMPLETE, EMPTY_STRING);
	    shipmentLineEle.setAttribute(XMLLiterals.BACKROOM_PICK_QUANTITY, EMPTY_STRING);
	    shipmentLineEle.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
	    shipmentLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
	    YFCElement orderLineEle = shipmentLineEle.createChild(XMLLiterals.ORDER_LINE);
	    orderLineEle.setAttribute(XMLLiterals.DELIVERY_METHOD, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.DEPARTMENT_CODE, EMPTY_STRING);
	    return getShipmentListTemp;
	  }
	
	/**
	 * This method calls getShipmentList API
	 * 
	 * @return
	 */
	
	public YFCDocument getShipmentList(){
	    return  invokeYantraApi(XMLLiterals.GET_SHIPMENT_LIST, inputXmlForGetShipmentList(), inputTemplateForGetShipmentList());
	 }
	
	/**
	 * This method fetches the shipNode and cancellationCode and passes
	 * it to another method where order lines can be grouped based on it
	 * 
	 * @param shipmentListApiOp
	 */
	
	private void getOrderLinesGroupByReasonCode(YFCDocument shipmentListApiOp){
		if(!XmlUtils.isVoid(shipmentListApiOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT))) {
			orderLineIsPickedStatus(shipmentListApiOp);
		}
	    YFCElement orderLinesEle = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY)
	    		.getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
	    YFCIterable<YFCElement> yfsItrator = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
	    for(YFCElement orderLine: yfsItrator) {
	      String cancellationReasonCode = orderLine.getAttribute(XMLLiterals.CANCELLATION_REASON_CODE);
	      String shipNodeValue = orderLine.getAttribute(XMLLiterals.SHIPNODE);
	      docGroupByCodeAndNode(cancellationReasonCode, shipNodeValue, orderLine);
	      
	    }
	  }
	
	/**
	 * this method is used to check if the order Lines are picked 
	 * or not. If it is already picked it will not be sent to get
	 * cancelled.
	 * 
	 * @param shipmentListApiOp
	 */
	
	private void orderLineIsPickedStatus(YFCDocument shipmentListApiOp) {
		YFCElement orderLinesEle = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
		YFCIterable<YFCElement> yfsItratorShipNode = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderLineEle : yfsItratorShipNode) {
			String shipNode = orderLineEle.getAttribute(XMLLiterals.SHIPNODE);
			String primeLineNo = orderLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO);
			YFCElement shipmentEle = XPathUtil.getXPathElement(shipmentListApiOp, "/Shipments/Shipment[@ShipNode=\""+shipNode+"\"]");
			YFCIterable<YFCElement> yfsItratorPrimeLine = shipmentEle.getChildElement(XMLLiterals.SHIPMENT_LINES)
					.getChildren(XMLLiterals.SHIPMENT_LINE);
			for(YFCElement shipmentLineEle : yfsItratorPrimeLine) {
				if(primeLineNo.equals(shipmentLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO)) && 
						(!XmlUtils.isVoid(shipmentLineEle.getAttribute(XMLLiterals.BACKROOM_PICK_COMPLETE)))) {
					String isPickComplete = shipmentLineEle.getAttribute(XMLLiterals.BACKROOM_PICK_COMPLETE);
					if(isPickComplete.equals(YES)) {
						YFCNode parent = orderLineEle.getParentNode();
				    	parent.removeChild(orderLineEle);
					}
				}				
			}
		}
	}
	
	/**
	 * This method is used to create groups based on shipNode and 
	 * cancellationCode
	 * 
	 * @param cancellationReasonCode
	 * @param shipNodeValue
	 * @param orderLine
	 */
	
	private void docGroupByCodeAndNode (String cancellationReasonCode, String shipNodeValue, YFCElement orderLine) {
		List<YFCElement> orderLineList;
		if(XmlUtils.isVoid(orderLineMapGroupByReasonCode.get(cancellationReasonCode))) {
	        orderLineList = new ArrayList<>();	
	        orderLineList.add(orderLine);
	        orderLineMapGroupByReasonCode.put(cancellationReasonCode,orderLineList);
	      }
	      else {
	        orderLineList = orderLineMapGroupByReasonCode.get(cancellationReasonCode);
	        orderLineList.add(orderLine);
	        orderLineMapGroupByReasonCode.put(cancellationReasonCode,orderLineList);
	      }
	      if(XmlUtils.isVoid(orderLineMapGroupByShipNode.get(shipNodeValue))) {
		        orderLineList = new ArrayList<>();	
		        orderLineList.add(orderLine);
		        orderLineMapGroupByShipNode.put(shipNodeValue,orderLineList);
		   }
		   else {
		        orderLineList = orderLineMapGroupByShipNode.get(shipNodeValue);
		        orderLineList.add(orderLine);
		        orderLineMapGroupByShipNode.put(shipNodeValue,orderLineList);
		  }
	      YFCNode parent = orderLine.getParentNode();
	      parent.removeChild(orderLine);
    }
	
	
	/**
	 * This method fetches the required order lines for cancellation (lines grouped by ReasonCode).
	 */
	
	private void docCancelOrderLines(){
		for (Entry<String, List<YFCElement>> entry : orderLineMapGroupByReasonCode.entrySet()) {
			YFCDocument docChangeOrderInputLines = YFCDocument.createDocument(XMLLiterals.ORDER_LINES);
		      List<YFCElement> orderLineList = orderLineMapGroupByReasonCode.get(entry.getKey());
		      YFCElement orderLinesEle = docChangeOrderInputLines.getDocumentElement();
		      for(YFCElement lineEle : orderLineList) {
			        orderLinesEle.importNode(lineEle);
			  } 
		      YFCDocument docChangeOrderApiInput = YFCDocument.createDocument(XMLLiterals.ORDER);
		      
		      docSetAttributesForCancel(docChangeOrderApiInput, docChangeOrderInputLines);
		}
	}
	
	/**
	 * This method appends the attributes to for the input for
	 * changeOrder API
	 * 
	 * @param docChangeOrderApiInput
	 * @param docChangeOrderInputLines
	 */
	
	private void docSetAttributesForCancel(YFCDocument docChangeOrderApiInput, YFCDocument docChangeOrderInputLines) {
		  String reasonCode = docChangeOrderInputLines.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
	    		  getAttribute(XMLLiterals.CANCELLATION_REASON_CODE);
	      String reasonText = docChangeOrderInputLines.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
	    		  getAttribute(XMLLiterals.CANCELLATION_TEXT);
		  docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.MODIFICATION_REASON_CODE, reasonCode);
	      docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.MODIFICATION_REASON_TEXT, reasonText);
	      docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.ORDER_NO, orderNo);
	      docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCode);
	      docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, documentType);
	      YFCElement orderLinesElement = docChangeOrderApiInput.getDocumentElement().createChild(XMLLiterals.ORDER_LINES);
	      YFCElement inputEle = docChangeOrderInputLines.getDocumentElement();
		  
	      docInputChangeOrderApi(docChangeOrderApiInput, inputEle, orderLinesElement);
	}
	
	/**
	 * This method appends the attributes for ChangeOrder API and
	 * calls the API.
	 * 
	 * @param docChangeOrderApiInput
	 * @param inputEle
	 * @param orderLinesElement
	 */
	
	private void docInputChangeOrderApi(YFCDocument docChangeOrderApiInput, YFCElement inputEle, 
			YFCElement orderLinesElement) {
		 YFCIterable<YFCElement> yfsItrator = inputEle.getChildren(XMLLiterals.ORDER_LINE);
		 for(YFCElement orderLine : yfsItrator) {
			String primeLineNo = orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO);
			YFCElement orderLineEle = orderLinesElement.createChild(XMLLiterals.ORDER_LINE);
			orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, primeLineNo);
			orderLineEle.setAttribute(XMLLiterals.SUB_LINE_NO, SUBLINE_VALUE);
			orderLineEle.setAttribute(XMLLiterals.ACTION, ACTION_VALUE);
		}
		 YFCDocument changeOrderOutput = invokeYantraApi(XMLLiterals.CHANGE_ORDER_API, docChangeOrderApiInput,
		    		getChangeOrderTemplateDoc());    
		 String modifyTS = changeOrderOutput.getDocumentElement().getAttribute(XMLLiterals.MODIFYTS);
		 docLegacy051Input.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, modifyTS);   
	}
	
	/**
	 * this method calls the getOrderLineListAPI based on shipNode
	 * 
	 * @param inXml
	 */
	
	private void docSAP051GetAttributes(YFCDocument inXml) {
		YFCDocument getOrderLineListDoc = null;
		for (Entry<String, List<YFCElement>> entry : orderLineMapGroupByShipNode.entrySet()) {
			YFCDocument groupByShipNodeDoc = YFCDocument.createDocument(XMLLiterals.ORDER);
			YFCElement orderLinesEle = groupByShipNodeDoc.getDocumentElement().createChild(XMLLiterals.ORDER_LINES);
		    List<YFCElement> orderLineList = orderLineMapGroupByShipNode.get(entry.getKey());
		    for(YFCElement lineEle : orderLineList) {
		      orderLinesEle.importNode(lineEle);
		    }
		    getOrderLineListDoc = getOrderLineListFunc(groupByShipNodeDoc);
		    docSAP051Input(groupByShipNodeDoc, getOrderLineListDoc);
		}
		docSetIsProcessedAttr(inXml);
		docAddLegacyOMSOdrNo(getOrderLineListDoc);
		callLegacyOMS051opQueue(docLegacy051Input);
	}
	
	/**
	 * This method appends the legacyOMSOrderNo at OrderLine level for 
	 * each primeLineNo taking the value from getOrderLineList API
	 * 
	 * @param getOrderLineListDoc
	 */
	
	private void docAddLegacyOMSOdrNo(YFCDocument getOrderLineListDoc) {
		YFCElement orderLinesEle = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
	    YFCIterable<YFCElement> yfsItrator = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
	    for(YFCElement orderLine : yfsItrator) {
	    	String primeLineNo = orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO);
	    	YFCElement orderLineEle = XPathUtil.getXPathElement(getOrderLineListDoc, "/OrderLineList/OrderLine[@PrimeLineNo = \""+
	    	primeLineNo+"\"]");
	    	String legacyOMSOrderNo = orderLineEle.getAttribute(XMLLiterals.CUSTOMER_PO_NO);
	    	orderLine.setAttribute(XMLLiterals.LEGACY_OMS_ORDER_NO, legacyOMSOrderNo);
	    }
	}
	
	/**
	 * This method appends required attributes and their values in
	 * SAP051 message
	 * 
	 * @param groupByShipNodeDoc
	 * @param getOrderLineListDoc
	 */
	
	private void docSAP051Input(YFCDocument groupByShipNodeDoc, YFCDocument getOrderLineListDoc) {
		YFCElement rootEle = groupByShipNodeDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
	    YFCIterable<YFCElement> yfsItrator = rootEle.getChildren(XMLLiterals.ORDER_LINE);
	    for(YFCElement orderLine : yfsItrator) {
	    	String primeLineNo = orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO);
	    	YFCElement orderLineEle = XPathUtil.getXPathElement(getOrderLineListDoc, "/OrderLineList/OrderLine[@PrimeLineNo = \""+
	    	primeLineNo+"\"]");
	    	String currentQty = orderLineEle.getAttribute(XMLLiterals.ORDERED_QTY);
			String originalQty = orderLineEle.getAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY);
			orderLine.setAttribute(XMLLiterals.ORDERED_QTY, currentQty);
			orderLine.setAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY, originalQty);
			orderLine.setAttribute(XMLLiterals.CANCELLATION_REASON_CODE, REASON_CODE);
	    }
	    String sapOrderNo = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
	    		getAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO);
	    String modifyTs = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
	    		getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.MODIFYTS);
	    sendShipNodeDocToService(groupByShipNodeDoc, getOrderLineListDoc);
	    docAddOrderLevelAttr(groupByShipNodeDoc, sapOrderNo, modifyTs);
	}
	
	/**
	 * This method checks if the full order is cancelled or not
	 * and appends the value in SAP051 doc.
	 * 
	 * @param groupByShipNodeDoc
	 * @param getOrderLineListDoc
	 */
	
	private void sendShipNodeDocToService(YFCDocument groupByShipNodeDoc, YFCDocument getOrderLineListDoc) {
	    YFCElement getOrderLineListOutputEle = getOrderLineListDoc.getDocumentElement();
		YFCIterable<YFCElement> inputOrderLineEle = getOrderLineListOutputEle.getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderElement : inputOrderLineEle) {
			String orderLineStatus=orderElement.getAttribute(XMLLiterals.STATUS);
			if(!orderLineStatus.equals(CANCELLED))
			{ isFullOrderCancelled = NO;
				break; }
			else
			{ isFullOrderCancelled = YES; }
		}	
		if(isFullOrderCancelled.equals(NO)) {
			groupByShipNodeDoc.getDocumentElement().setAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED, isFullOrderCancelled);
		}
		else
			groupByShipNodeDoc.getDocumentElement().setAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED, isFullOrderCancelled);
	}
	
	/**
	 * This code adds the necessary attributes to the SAP051 doc
	 * 
	 * @param groupByShipNodeDoc
	 */
	
	private void docAddOrderLevelAttr(YFCDocument groupByShipNodeDoc, String sapOrderNo, String modifyTs) {
		groupByShipNodeDoc.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, documentType);
		groupByShipNodeDoc.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCode);
		groupByShipNodeDoc.getDocumentElement().setAttribute(XMLLiterals.ORDER_TYPE, orderType);
		groupByShipNodeDoc.getDocumentElement().setAttribute(XMLLiterals.EXTN_SAP_ORDER_NO, sapOrderNo);
	    groupByShipNodeDoc.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, modifyTs);
	    groupByShipNodeDoc.getDocumentElement().setAttribute(XMLLiterals.STERLING_ORDER_NO, orderNo);
		
	    docCheckForSAPOrderNo(groupByShipNodeDoc);
	}
	
	/**
	 * This method verifies that the SAPOrderNo value must not be void
	 * and drops the SAP051 and LegacyOMS051 message in the queue
	 * 
	 * @param groupByShipNodeDoc
	 */
	
	private void docCheckForSAPOrderNo(YFCDocument groupByShipNodeDoc) {
		String sapOrderNo = groupByShipNodeDoc.getDocumentElement().getAttribute(XMLLiterals.EXTN_SAP_ORDER_NO);
		if(!XmlUtils.isVoid(sapOrderNo)) {
			callSAP051opQueue(groupByShipNodeDoc);
			YFCElement orderLinesEle = groupByShipNodeDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
			YFCIterable<YFCElement> inputOrderLineEle = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
			for(YFCElement orderLineEle : inputOrderLineEle) {
				YFCElement orderLines = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
				orderLines.importNode(orderLineEle);
			}
		}
	}
	
	/**
	 * This method adds the attribute isProcessed based on the order
	 * lines. If all the orderLines have been cancelled the value will 
	 * be Y or else N.
	 * 
	 * @param inXml
	 */
	
	private void docSetIsProcessedAttr(YFCDocument inXml) {
		YFCElement orderLinesEle = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
		YFCElement docOrderLinesEle = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
		YFCIterable<YFCElement> yfsItrator = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderLineEle : yfsItrator) {
			String primeLineNo = orderLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO);
			orderLineEle.setAttribute(XMLLiterals.IS_PROCESSED, NO);
			YFCElement docOrderLineEle = XPathUtil.getXPathElement(docLegacy051Input, "/OrderMessage/MessageBody/Order/"
					+ "OrderLines/OrderLine[@PrimeLineNo=\""+primeLineNo+"\"]");
			if(!XmlUtils.isVoid(docOrderLineEle)) {
				docOrderLineEle.setAttribute(XMLLiterals.IS_PROCESSED, YES);
			}
			else
				docOrderLinesEle.importNode(orderLineEle);
		}
	}
	
	/**
	 * This method calls the service where SAP051 message will be dropped
	 * @param doc
	 */
	
	private void callSAP051opQueue(YFCDocument doc) {
	     invokeYantraService(CALL_SAP051_SERVICE, doc);
	}
	
	/**
	 * This method calls the service where LegacyOMS051 message will be dropped
	 * @param doc
	 */
	
	private void callLegacyOMS051opQueue(YFCDocument doc) {
	     invokeYantraService(CALL_LEGACYOMS051_SERVICE, doc);
	}
	
	/**
	 * This method forms the input for getOrderLineList API doc.
	 * 
	 * @param groupByShipNodeDoc
	 * @return
	 */
	
	public YFCDocument getOrderLineListInDoc(YFCDocument groupByShipNodeDoc) {
		YFCElement inEle = groupByShipNodeDoc.getDocumentElement();
		String shipnode = inEle.getChildElement(XMLLiterals.ORDER_LINES).getChildElement(XMLLiterals.ORDER_LINE).
				getAttribute(XMLLiterals.SHIPNODE);
	    YFCDocument getOrderDoc = YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
	    getOrderDoc.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, shipnode);
	    YFCElement orderEle = getOrderDoc.getDocumentElement().createChild(XMLLiterals.ORDER);
	    orderEle.setAttribute(XMLLiterals.ORDER_NO, orderNo);
	    orderEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCode);
	    orderEle.setAttribute(XMLLiterals.DOCUMENT_TYPE, documentType);
	    return getOrderDoc;
	  }
	
	/**
	 * This method forms the template for getOrderLineList API
	 * 
	 * @return
	 */
	
	public YFCDocument getOrderLineListTemplateDoc() {
	    YFCDocument getOrderListTemp = YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
	    YFCElement orderLineEle = getOrderListTemp.getDocumentElement().createChild(XMLLiterals.ORDER_LINE);
	    orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.SUB_LINE_NO, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.ORDERED_QTY, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.CUSTOMER_PO_NO, EMPTY_STRING);
	    YFCElement itemEle = orderLineEle.createChild(XMLLiterals.ITEM);
	    itemEle.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
	    YFCElement orderEle = orderLineEle.createChild(XMLLiterals.ORDER);
	    orderEle.setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
	    orderEle.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
	    orderEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
	    orderEle.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
	    YFCElement orderStatusEle = orderLineEle.createChild(XMLLiterals.ORDER_STATUSES);
	    YFCElement statusEle = orderStatusEle.createChild(XMLLiterals.ORDER_STATUS);
	    statusEle.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
	    return getOrderListTemp;
	  }
	
	/**
	 * This method call the getOrderLineList API
	 * 
	 * @param groupByShipNodeDoc
	 * @return
	 */
	
	public YFCDocument getOrderLineListFunc(YFCDocument groupByShipNodeDoc){
	    return  invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST, getOrderLineListInDoc(groupByShipNodeDoc), 
	    		getOrderLineListTemplateDoc());
	 }
	
	/**
	 * This method forms the template for changeOrde API
	 * 
	 * @return
	 */
	
	public YFCDocument getChangeOrderTemplateDoc() {
		 YFCDocument getChangeOrderTemp = YFCDocument.createDocument(XMLLiterals.ORDER);
		 getChangeOrderTemp.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
		 return getChangeOrderTemp;
	}
}
