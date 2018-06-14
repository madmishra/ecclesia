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
 * 
 */
public class IndgLegacy051ToCancel extends AbstractCustomApi{
	 Map<String,List<YFCElement>> orderLineMapGroupByReasonCode = new HashMap<>();
	 private static final String SUBLINE_VALUE = "1";
	 private static final String ACTION_VALUE = "MODIFY";
	 private static final String EMPTY_STRING = "";
	 private static final String YES = "Y";
	 private static final String NO = "N";
	 private String orderNo = "";
	 private String orderType = "";
	 private String documentType = "";
	 private String enterpriseCode = "";
	 YFCDocument docLegacy051Input = null;
	 YFCDocument docInputXml = null;
	 private static final String CANCELLATION_TYPE = "LEGACY051";
	 private static final String REASON_CODE = "03";
	 private static final String CALL_LEGACYOMS051_SERVICE = "Indg_LegacyOMS051_ForLegacy052";	 
	 /**
	  * This method is the invoke point of the service.
	  * 
	  */
	 
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		orderNo = inXml.getDocumentElement().getAttribute(XMLLiterals.ORDER_NO);
		enterpriseCode = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).
				getAttribute(XMLLiterals.ENTERPRISE_CODE);
	    documentType = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).
	    		getAttribute(XMLLiterals.DOCUMENT_TYPE);
	    orderType = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).
	    		getAttribute(XMLLiterals.ORDER_TYPE);
		String inputDocString = inXml.toString();
	    docLegacy051Input = YFCDocument.getDocumentFor(inputDocString);
	    docInputXml = YFCDocument.getDocumentFor(inputDocString);
	    YFCDocument shipmentListApiOp = getShipmentList();
	    getOrderLinesGroupByReasonCode(shipmentListApiOp);
	    docCancelOrderLines();
		
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
	      docGroupByCodeAndNode(cancellationReasonCode, orderLine);
	      
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
		YFCDocument docLegacy051 = YFCDocument.createDocument(XMLLiterals.ORDER);
		YFCElement orderLines = docLegacy051.getDocumentElement().createChild(XMLLiterals.ORDER_LINES);
		YFCElement orderLinesEle = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
		YFCIterable<YFCElement> yfsItratorShipNode = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderLineEle : yfsItratorShipNode) {
			String shipNode = orderLineEle.getAttribute(XMLLiterals.SHIPNODE);
			String primeLineNo = orderLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO);
			System.out.println(shipNode + "xshgd");
			System.out.println(shipmentListApiOp + "dochdsg");
			YFCElement shipmentEle = XPathUtil.getXPathElement(shipmentListApiOp, "/Shipments/Shipment[@ShipNode=\""+shipNode+"\"]");
			System.out.println(shipmentEle.toString() + "xdshgfg");
			orderLineEle.setAttribute(XMLLiterals.MODIFYTS, shipmentEle.getAttribute(XMLLiterals.MODIFYTS));
			String legacyOmsNo = shipmentEle.getAttribute(XMLLiterals.CUSTOMER_PO_NO);
			orderLineEle.setAttribute(XMLLiterals.LEGACY_OMS_ORDER_NO, legacyOmsNo);
			orderLineEle.setAttribute(XMLLiterals.IS_PROCESSED, NO);
			YFCIterable<YFCElement> yfsItratorPrimeLine = shipmentEle.getChildElement(XMLLiterals.SHIPMENT_LINES)
					.getChildren(XMLLiterals.SHIPMENT_LINE);
			for(YFCElement shipmentLineEle : yfsItratorPrimeLine) {
				if(primeLineNo.equals(shipmentLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO)) && 
						(!XmlUtils.isVoid(shipmentLineEle.getAttribute(XMLLiterals.BACKROOM_PICK_COMPLETE)))) {
					String isPickComplete = shipmentLineEle.getAttribute(XMLLiterals.BACKROOM_PICK_COMPLETE);
					if(isPickComplete.equals(YES)) {
						orderLines.importNode(orderLineEle);
					}
				}				
			}
		}
		docSetAttributesToLeg051(docLegacy051);
	}
	
	private void docSetAttributesToLeg051(YFCDocument docLegacy051) {
		docLegacy051.getDocumentElement().setAttribute(XMLLiterals.ORDER_NO, orderNo);
		docLegacy051.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCode);
		docLegacy051.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, documentType);
		docLegacy051.getDocumentElement().setAttribute(XMLLiterals.ORDER_TYPE, orderType);
		YFCElement orderLinesEle = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
		YFCIterable<YFCElement> yfsItrator = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderLine : yfsItrator) {
			String primeLineNo = orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO);
	    	YFCElement orderLineEle = XPathUtil.getXPathElement(docLegacy051, "/Order/OrderLines/OrderLine[@PrimeLineNo = \""+
	    	primeLineNo+"\"]");
	    	if(!XmlUtils.isVoid(orderLineEle)) {
	    		String requestId = orderLine.getAttribute(XMLLiterals.LEGACY_OMS_CANCELLATION_REQ_ID);
				orderLineEle.setAttribute(XMLLiterals.CONDITION_VARIABLE_1, requestId);
	    	}
		}	
		callLegacyOMS051opQueue(docLegacy051);
	}
	
	/**
	 * This method is used to create groups based on shipNode and 
	 * cancellationCode
	 * 
	 * @param cancellationReasonCode
	 * @param shipNodeValue
	 * @param orderLine
	 */
	
	private void docGroupByCodeAndNode (String cancellationReasonCode, YFCElement orderLine) {
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
	      docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.MODIFICATION_REFRENCE_1, CANCELLATION_TYPE);
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
			YFCElement odrLineQtyEle = XPathUtil.getXPathElement(docInputXml, "/OrderMessage/MessageBody/Order/OrderLines/"
					+ "OrderLine[@PrimeLineNo=\""+primeLineNo+"\"]");
			String orderedQty = odrLineQtyEle.getAttribute(XMLLiterals.QUANTITY_AFTER_CANCELLATION);
			String cancellationReqId = odrLineQtyEle.getAttribute(XMLLiterals.CANCELLATION_REQUEST_ID);
			YFCElement orderLineEle = orderLinesElement.createChild(XMLLiterals.ORDER_LINE);
			orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, primeLineNo);
			orderLineEle.setAttribute(XMLLiterals.SUB_LINE_NO, SUBLINE_VALUE);
			orderLineEle.setAttribute(XMLLiterals.ACTION, ACTION_VALUE);
			orderLineEle.setAttribute(XMLLiterals.CONDITION_VARIABLE_1, cancellationReqId);
			orderLineEle.setAttribute(XMLLiterals.CONDITION_VARIABLE_2, REASON_CODE);
			orderLineEle.setAttribute(XMLLiterals.ORDERED_QTY, orderedQty);
		}
		 invokeYantraApi(XMLLiterals.CHANGE_ORDER_API, docChangeOrderApiInput);    
	}
	
	private void callLegacyOMS051opQueue(YFCDocument doc) {
	     invokeYantraService(CALL_LEGACYOMS051_SERVICE, doc);
		}
}
