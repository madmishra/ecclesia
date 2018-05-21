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
public class Legacy051ToCancel extends AbstractCustomApi{
	 Map<String,List<YFCElement>> orderLineMapGroupByReasonCode = new HashMap<>();
	 Map<String,List<YFCElement>> orderLineMapGroupByShipNode = new HashMap<>();
	 private static final String SUBLINE_VALUE = "1";
	 private static final String ACTION_VALUE = "CANCEL";
	 private static final String EMPTY_STRING = "";
	 private static final String CALL_SAP051_SERVICE = "Indg_SAP051_OnLegacy051";
	 private String FULL_ORDER_CANCELLED = "IsFullOrderCancelled";
	 private static final String CANCELLED = "Cancelled";
	 private static final String NO = "N";
	 private static final String YES = "Y";
	 
	 /**
	  * This method is the invoke point of the service.
	  * 
	  */
	 
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		String inputDocString = inXml.toString();
		
	    YFCDocument docLegacy051Input = YFCDocument.getDocumentFor(inputDocString);
	    getOrderLinesGroupByReasonCode(docLegacy051Input);
	    docCancelOrderLines(docLegacy051Input, inXml);
		getOrderLinesGroupByShipNode(inXml);
		docSAP051GetAttributes(inXml);
		return inXml;
	}
	
	/**
	 * This method takes the input file and group the OrderLines
	 * based on ReasonCode.
	 * 
	 * @param docLegacy051Input
	 */
	
	private void getOrderLinesGroupByReasonCode(YFCDocument docLegacy051Input){
	    YFCElement orderLinesEle = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY)
	    		.getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
	    YFCIterable<YFCElement> yfsItrator = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
	    for(YFCElement orderLine: yfsItrator) {
	      List<YFCElement> orderLineList;
	      String cancellationReasonCode = orderLine.getAttribute(XMLLiterals.CANCELLATION_REASON_CODE);
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
	  }
	
	/**
	 * This method forms the input for ChangeOrder API (lines grouped by ReasonCode).
	 * and returns the input message back to service from where
	 * LegacyOMS052 message will be formed.
	 * 
	 * @param docLegacy051Input
	 * @param inXml
	 * @return
	 */
	
	private YFCDocument docCancelOrderLines(YFCDocument docLegacy051Input, YFCDocument inXml){
		System.out.println(docLegacy051Input + "InsideGroupbyCode");
		for (Entry<String, List<YFCElement>> entry : orderLineMapGroupByReasonCode.entrySet()) {
			YFCDocument docChangeOrderInputLines = YFCDocument.createDocument(XMLLiterals.ORDER_LINES);
		      List<YFCElement> orderLineList = orderLineMapGroupByReasonCode.get(entry.getKey());
		      YFCElement orderLinesEle = docChangeOrderInputLines.getDocumentElement();
		      for(YFCElement lineEle : orderLineList) {
		        orderLinesEle.importNode(lineEle);
		      }  
		      YFCDocument docChangeOrderApiInput = YFCDocument.createDocument(XMLLiterals.ORDER);
		      String reasonCode = docChangeOrderInputLines.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
		    		  getAttribute(XMLLiterals.CANCELLATION_REASON_CODE);
		      String reasonText = docChangeOrderInputLines.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
		    		  getAttribute(XMLLiterals.CANCELLATION_TEXT);
		      String orderNo = docLegacy051Input.getDocumentElement().getAttribute(XMLLiterals.ORDER_NO);
		      String enterpriseCode = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
		    		  getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.ENTERPRISE_CODE);
		      String documentType = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
		    		  getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.DOCUMENT_TYPE);
		      docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.MODIFICATION_REASON_CODE, reasonCode);
		      docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.MODIFICATION_REASON_TEXT, reasonText);
		      docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.ORDER_NO, orderNo);
		      docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCode);
		      docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, documentType);
		    
		      YFCElement orderLinesElement = docChangeOrderApiInput.getDocumentElement().createChild(XMLLiterals.ORDER_LINES);
		      YFCElement inputEle = docChangeOrderInputLines.getDocumentElement();
		      
			  YFCIterable<YFCElement> yfsItrator = inputEle.getChildren(XMLLiterals.ORDER_LINE);
				for(YFCElement orderLine : yfsItrator) {
					String primeLineNo = orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO);
					YFCElement orderLineEle = orderLinesElement.createChild(XMLLiterals.ORDER_LINE);
					orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, primeLineNo);
					orderLineEle.setAttribute(XMLLiterals.SUB_LINE_NO, SUBLINE_VALUE);
					orderLineEle.setAttribute(XMLLiterals.ACTION, ACTION_VALUE);
				}
				System.out.println(docChangeOrderApiInput + "changeorderinput");
		    YFCDocument changeOrderOutput = invokeYantraApi(XMLLiterals.CHANGE_ORDER_API, docChangeOrderApiInput,
		    		getChangeOrderTemplateDoc());    
		    String modifyTS = changeOrderOutput.getDocumentElement().getAttribute(XMLLiterals.MODIFYTS);
		    inXml.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, modifyTS);
		}
		System.out.println(inXml + "returned doc");
		return inXml;
		
	}
	
	/**
	 * This method will group the OrderLines based on the ShipNode.
	 * 
	 * @param inXml
	 */
	
	public void getOrderLinesGroupByShipNode(YFCDocument inXml) {
		YFCElement orderLinesEle = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY)
	    		.getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
	    YFCIterable<YFCElement> yfsItrator = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
	    for(YFCElement orderLine: yfsItrator) {
	      List<YFCElement> orderLineList;
	      String shipNodeValue = orderLine.getAttribute(XMLLiterals.SHIPNODE);
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
	}
	
	/**
	 * This message now forms the LegacyOMS051 message grouped by
	 * 
	 * @param inXml
	 */
	
	private void docSAP051GetAttributes(YFCDocument inXml) {
		for (Entry<String, List<YFCElement>> entry : orderLineMapGroupByShipNode.entrySet()) {
			String shipNodeDoc = inXml.toString();
		    YFCDocument groupByShipNodeDoc = YFCDocument.getDocumentFor(shipNodeDoc);
			YFCElement orderLinesEle = groupByShipNodeDoc.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
					getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
		    List<YFCElement> orderLineList = orderLineMapGroupByShipNode.get(entry.getKey());
		    for(YFCElement lineEle : orderLineList) {
		      orderLinesEle.importNode(lineEle);
		    }
		    YFCDocument getOrderLineListDoc = getOrderLineListFunc(groupByShipNodeDoc);
		    System.out.println(groupByShipNodeDoc + "shipnodeinput");
		    System.out.println(getOrderLineListDoc + "API O/P");
		    
		    docSAP052Input(groupByShipNodeDoc, getOrderLineListDoc);
		}
	}
	
	private void docSAP052Input(YFCDocument groupByShipNodeDoc, YFCDocument getOrderLineListDoc) {
		YFCElement rootEle = groupByShipNodeDoc.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
	    YFCIterable<YFCElement> yfsItrator = rootEle.getChildren(XMLLiterals.ORDER_LINE);
	    for(YFCElement orderLine : yfsItrator) {
	    	String primeLineNo = orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO);
	    	System.out.println(primeLineNo + "thisNO");
	    	YFCElement orderLineEle = XPathUtil.getXPathElement(getOrderLineListDoc, "/OrderLineList/OrderLine/[@PrimeLineNo = \""+
	    	primeLineNo+"\"]");
	    	System.out.println(orderLineEle.toString() + "inELE");
	    	String currentQty = orderLineEle.getAttribute(XMLLiterals.ORDERED_QTY);
			String originalQty = orderLineEle.getAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY);
			orderLine.setAttribute(XMLLiterals.CURRENT_QTY, currentQty);
			System.out.println(orderLine.toString() + "lkajd");
			orderLine.setAttribute(XMLLiterals.ORIGINAL_QTY, originalQty);
			
	    }
	    System.out.println(groupByShipNodeDoc + "what got appended");
	    sendShipNodeDocToService(groupByShipNodeDoc, getOrderLineListDoc);
	}
	
	private void sendShipNodeDocToService(YFCDocument groupByShipNodeDoc, YFCDocument getOrderLineListDoc) {
	    String sapOrderNo = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
	    		getChildElement(XMLLiterals.EXTN).getAttribute(XMLLiterals.EXTN_SAP_ORDER_NO);
	    groupByShipNodeDoc.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER)
	    .setAttribute(XMLLiterals.SAP_ORDER_NO, sapOrderNo);
	    YFCElement getOrderLineListOutputEle=getOrderLineListDoc.getDocumentElement();
		YFCIterable<YFCElement> inputOrderLineEle = getOrderLineListOutputEle.getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderElement : inputOrderLineEle) {
			String orderLineStatus=orderElement.getAttribute(XMLLiterals.STATUS);
			if(!orderLineStatus.equals(CANCELLED))
			{ FULL_ORDER_CANCELLED = NO;
				break; }
			else
			{ FULL_ORDER_CANCELLED = YES; }
		}	
		if(FULL_ORDER_CANCELLED.equals(NO)) {
			groupByShipNodeDoc.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER)
		    .setAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED, FULL_ORDER_CANCELLED);
		}
		else
			groupByShipNodeDoc.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER)
		    .setAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED, FULL_ORDER_CANCELLED);
	   System.out.println(groupByShipNodeDoc + "sap051 doc");
		callUserUpdateQueue(groupByShipNodeDoc);
	}
	
	private void callUserUpdateQueue(YFCDocument doc) {
	     invokeYantraService(CALL_SAP051_SERVICE, doc);
	}
	
	public YFCDocument getOrderLineListInDoc(YFCDocument groupByShipNodeDoc) {
		YFCElement inEle = groupByShipNodeDoc.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER);
		String enterpriseCode = inEle.getAttribute(XMLLiterals.ENTERPRISE_CODE);
		String documentType = inEle.getAttribute(XMLLiterals.DOCUMENT_TYPE);
		String orderNo = groupByShipNodeDoc.getDocumentElement().getAttribute(XMLLiterals.ORDER_NO);
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
	
	public YFCDocument getOrderLineListTemplateDoc() {
	    YFCDocument getOrderListTemp = YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
	    YFCElement orderLineEle = getOrderListTemp.getDocumentElement().createChild(XMLLiterals.ORDER_LINE);
	    orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.SUB_LINE_NO, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.ORDERED_QTY, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY, EMPTY_STRING);
	    YFCElement itemEle = orderLineEle.createChild(XMLLiterals.ITEM);
	    itemEle.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
	    YFCElement extnEle = orderLineEle.createChild(XMLLiterals.EXTN);
	    extnEle.setAttribute(XMLLiterals.EXTN_LEGACY_OMS_CHILD_ORDERNO, EMPTY_STRING);
	    extnEle.setAttribute(XMLLiterals.EXTN_SAP_ORDER_NO, EMPTY_STRING);
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
	
	public YFCDocument getOrderLineListFunc(YFCDocument groupByShipNodeDoc){
	    return  invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST, getOrderLineListInDoc(groupByShipNodeDoc), 
	    		getOrderLineListTemplateDoc());
	 }
	
	public YFCDocument getChangeOrderTemplateDoc() {
		 YFCDocument getChangeOrderTemp = YFCDocument.createDocument(XMLLiterals.ORDER);
		 getChangeOrderTemp.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
		 return getChangeOrderTemp;
	}
}

