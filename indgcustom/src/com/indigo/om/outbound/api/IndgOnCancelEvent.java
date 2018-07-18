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
 * The lines will be grouped by ShipNode to sends SAP051 message to SAP and
 * LegacyOMS052 message after on_Cancel event is called.
 * 
 */
public class IndgOnCancelEvent extends AbstractCustomApi{
	 Map<String,List<YFCElement>> orderLineMapGroupByShipNode = new HashMap<>();
	 private static final String EMPTY_STRING = "";
	 private static final String CALL_SAP051_SERVICE = "CALL_SAP051_SERVICE";
	 private static final String CALL_LEGACYOMS051_SERVICE = "CALL_LEGACYOMS051_SERVICE";
	 private String isFullOrderCancelled = "";
	 private static final String CANCELLED = "Cancelled";
	 private static final String NO = "N";
	 private static final String YES = "Y";
	 private String orderNo = "";
	 private String documentType = "";
	 private String enterpriseCode = "";
	 private String orderType = "";
	 private String customerLinePoNo="";
	 private String reasonCode="";
	 YFCDocument docLegacy051Input = null;
	 private static final String REASON_CODE1 = "03";
	 private static final String REASON_CODE2 = "05";
	 private static final String REASON_CODE3 = "06";
	 
	 /**
	  * This method is the invoke point of the service.
	  * 
	  */
	 
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		System.out.println(inXml + "aaaaaaaaaa");
		orderNo = inXml.getDocumentElement().getAttribute(XMLLiterals.ORDER_NO);
		orderType = inXml.getDocumentElement().getAttribute(XMLLiterals.ORDER_TYPE);
		enterpriseCode = inXml.getDocumentElement().getAttribute(XMLLiterals.ENTERPRISE_CODE);
	    documentType = inXml.getDocumentElement().getAttribute(XMLLiterals.DOCUMENT_TYPE);
	    reasonCode = inXml.getDocumentElement().getChildElement(XMLLiterals.ORDER_AUDIT).getAttribute(XMLLiterals.REASON_CODE);
	    System.out.println(orderNo + orderType + enterpriseCode + documentType + reasonCode + "bbbbbbbbb");
		String inputDocString = inXml.toString();
	    docLegacy051Input = YFCDocument.getDocumentFor(inputDocString);
	    getOrderLinesGroupByReasonCode();
		docSAP051GetAttributes(inXml);
		
		return inXml;
	}
	
	/**
	 * This method fetches the shipNode and cancellationCode and passes
	 * it to another method where order lines can be grouped based on it
	 * 
	 * @param shipmentListApiOp
	 */
	
	private void getOrderLinesGroupByReasonCode(){
		System.out.println(docLegacy051Input + "cccccccccc");
	    YFCElement orderLinesEle = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
	    YFCIterable<YFCElement> yfsItrator = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
	    for(YFCElement orderLine: yfsItrator) {
	      String shipNodeValue = orderLine.getAttribute(XMLLiterals.SHIPNODE);
	      System.out.println(orderLine + shipNodeValue + "ddddddddd");
	      docGroupByCodeAndNode(shipNodeValue, orderLine);
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
	
	private void docGroupByCodeAndNode (String shipNodeValue, YFCElement orderLine) {
		List<YFCElement> orderLineList;
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
	      System.out.println(orderLineMapGroupByShipNode + "eeeeeeeeee");
    }
	
	/**
	 * this method calls the getOrderLineListAPI based on shipNode
	 * 
	 * @param inXml
	 */
	
	private void docSAP051GetAttributes(YFCDocument inXml) {
		for (Entry<String, List<YFCElement>> entry : orderLineMapGroupByShipNode.entrySet()) {
			YFCDocument groupByShipNodeDoc = YFCDocument.createDocument(XMLLiterals.ORDER);
			YFCElement orderLinesEle = groupByShipNodeDoc.getDocumentElement().createChild(XMLLiterals.ORDER_LINES);
		    List<YFCElement> orderLineList = orderLineMapGroupByShipNode.get(entry.getKey());
		    for(YFCElement lineEle : orderLineList) {
		      orderLinesEle.importNode(lineEle);
		    }
		    System.out.println(groupByShipNodeDoc + "fffffffffff");
		    YFCDocument getOrderLineListDoc = getOrderLineListFunc(groupByShipNodeDoc);
		    System.out.println(getOrderLineListDoc + "ggggggggggg");
		    docSAP051Input(groupByShipNodeDoc, getOrderLineListDoc);
		    docAddLegacyOMSOdrNo(getOrderLineListDoc);
		}
		docSetIsProcessedAttr(inXml);
		callLegacyOMS052opQueue(docLegacy051Input);
	}
	
	/**
	 * This method appends the legacyOMSOrderNo at OrderLine level for 
	 * each primeLineNo taking the value from getOrderLineList API
	 * 
	 * @param getOrderLineListDoc
	 */
	
	private void docAddLegacyOMSOdrNo(YFCDocument getOrderLineListDoc) {
		System.out.println(docLegacy051Input + "nnnnnnnnnn");
		YFCElement orderLinesEle = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
	    YFCIterable<YFCElement> yfsItrator = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
	    for(YFCElement orderLine : yfsItrator) {
	    	String primeLineNo = orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO);
	    	YFCElement orderLineEle = XPathUtil.getXPathElement(getOrderLineListDoc, "/OrderLineList/OrderLine[@PrimeLineNo = \""+
	    	primeLineNo+"\"]");
	    	orderLine.setAttribute(XMLLiterals.LEGACY_OMS_ORDER_NO, orderLineEle.getAttribute(XMLLiterals.CUSTOMER_PO_NO));
	    	orderLine.setAttribute(XMLLiterals.CONDITION_VARIABLE_1, orderLineEle.getAttribute(XMLLiterals.CONDITION_VARIABLE_1));
	    	orderLine.setAttribute(XMLLiterals.MODIFYTS, orderLineEle.getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.MODIFYTS));
	    }
	    System.out.println(docLegacy051Input + "oooooooooo");
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
			orderLine.setAttribute(XMLLiterals.ORDERED_QTY, orderLineEle.getAttribute(XMLLiterals.ORDERED_QTY));
			orderLine.setAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY, orderLineEle.getAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY));
			orderLine.setAttribute(XMLLiterals.CANCELLATION_REASON_CODE, reasonCode);
	    }
	    System.out.println(groupByShipNodeDoc + "hhhhhhhhhh");
	    String sapOrderNo = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO);
	    String modifyTs = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
	    		getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.MODIFYTS);
	    System.out.println(sapOrderNo + modifyTs + "iiiiiiiii");
	    sendShipNodeDocToService(groupByShipNodeDoc, getOrderLineListDoc);
	    System.out.println(groupByShipNodeDoc + "jjjjjjjjjj");
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
		groupByShipNodeDoc.getDocumentElement().setAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO, sapOrderNo);
	    groupByShipNodeDoc.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, modifyTs);
	    groupByShipNodeDoc.getDocumentElement().setAttribute(XMLLiterals.STERLING_ORDER_NO, orderNo);
	    System.out.println(groupByShipNodeDoc + "kkkkkkkkkk");
	    docCheckForSAPOrderNo(groupByShipNodeDoc);
	}
	
	/**
	 * This method verifies that the SAPOrderNo value must not be void
	 * and drops the SAP051 and LegacyOMS051 message in the queue
	 * 
	 * @param groupByShipNodeDoc
	 */
	
	private void docCheckForSAPOrderNo(YFCDocument groupByShipNodeDoc) {
		String sapOrderNo = groupByShipNodeDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES).
				getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO);
		if(!XmlUtils.isVoid(sapOrderNo)) {
			customerLinePoNo = groupByShipNodeDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES).
					getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO);
			System.out.println(groupByShipNodeDoc + "llllllllllll" + customerLinePoNo);
			callSAP051opQueue(groupByShipNodeDoc);
			YFCElement orderLinesEle = groupByShipNodeDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
			YFCIterable<YFCElement> inputOrderLineEle = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
			for(YFCElement orderLineEle : inputOrderLineEle) {
				YFCElement orderLines = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
				orderLines.importNode(orderLineEle);
			}
		}
		System.out.println(docLegacy051Input + "mmmmmmmmmm");
	}
	
	/**
	 * This method adds the attribute isProcessed based on the order
	 * lines. If all the orderLines have been cancelled the value will 
	 * be Y or else N.
	 * 
	 * @param inXml
	 */
	
	private void docSetIsProcessedAttr(YFCDocument inXml) {
		YFCElement docOrderLinesEle = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
		YFCElement orderLinesEle = inXml.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
		YFCIterable<YFCElement> yfsItrator = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderLineEle : yfsItrator) {
			String primeLineNo = orderLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO);
			orderLineEle.setAttribute(XMLLiterals.IS_PROCESSED, NO);
			YFCElement docOrderLineEle = XPathUtil.getXPathElement(docLegacy051Input, "/Order/OrderLines/OrderLine"
					+ "[@PrimeLineNo=\""+primeLineNo+"\"]");
			if(!XmlUtils.isVoid(docOrderLineEle)) {
				docOrderLineEle.setAttribute(XMLLiterals.IS_PROCESSED, YES);
			}
			else
				docOrderLinesEle.importNode(orderLineEle);
		}
		System.out.println(docLegacy051Input + "pppppppppp");
	}
	
	/**
	 * This method calls the service where SAP051 message will be dropped
	 * @param doc
	 */
	
	private void callSAP051opQueue(YFCDocument doc) {
		 if(!XmlUtils.isVoid(customerLinePoNo))
			 System.out.println(doc + "qqqqqqqqqq");
	     invokeYantraService(getProperty(CALL_SAP051_SERVICE), doc);
	}
	
	/**
	 * This method calls the service where LegacyOMS052 message will be dropped
	 * @param doc
	 */
	
	private void callLegacyOMS052opQueue(YFCDocument doc) {
		 if((!XmlUtils.isVoid(customerLinePoNo)) && ((reasonCode.equals(REASON_CODE1)) || (reasonCode.equals(REASON_CODE2)) || 
				 (reasonCode.equals(REASON_CODE3) || (XmlUtils.isVoid(reasonCode))))) {
			 System.out.println(doc + "rrrrrrrrrrr");
			 invokeYantraService(getProperty(CALL_LEGACYOMS051_SERVICE), doc);
		 }
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
	    orderLineEle.setAttribute(XMLLiterals.CONDITION_VARIABLE_1, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.CONDITION_VARIABLE_2, EMPTY_STRING);
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
}
