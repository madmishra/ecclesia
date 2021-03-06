package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class IndgSAP051MsgToSAP extends AbstractCustomApi {
	private static final String EMPTY_STRING = "";
	private  String isFullOrderCancelled="Y";
	private static final String NO="N"; 
	private static final String CANCELLED="Cancelled";
	private static final String REASON_CODE="05";
	private static final String SAP051="SAP051";
	
	/**
	 * This is the invoke point of the Service
	 * @throws  
	 * 
	 */
	
	public YFCDocument invoke(YFCDocument inXml) {
		YFCElement inXmlEle=inXml.getDocumentElement();
		String orderNo=inXmlEle.getAttribute(XMLLiterals.ORDER_NO);
		String enterpriseCode=inXmlEle.getAttribute(XMLLiterals.ENTERPRISE_CODE);
		YFCElement orderLinesrootEle=inXmlEle.getChildElement(XMLLiterals.ORDER_LINES);
		YFCIterable<YFCElement> orderLineListEle =orderLinesrootEle.getChildren();
		for(YFCElement orderElement : orderLineListEle) {
			String shipNode=orderElement.getAttribute(XMLLiterals.SHIPNODE);
			isFullOrderCancelled=invokeGetOrderLineList(orderNo,enterpriseCode,shipNode);
		}
		return formMessageSAP051(isFullOrderCancelled,inXml);
	}
	
	/** This method forms input for getOrderLineList API
	 * 
	 * @param shipNode
	 * @param orderNo
	 * @param enterpriseCode
	 * @return
	 */
	
	private YFCDocument inputGetOrderLineList(String shipNode,String orderNo,String enterpriseCode) {
		YFCDocument inputGetOrderLineListDoc=YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
		YFCElement orderLineEle=inputGetOrderLineListDoc.getDocumentElement();
		orderLineEle.setAttribute(XMLLiterals.SHIPNODE,shipNode );
		YFCElement orderEle=orderLineEle.createChild(XMLLiterals.ORDER);
		orderEle.setAttribute(XMLLiterals.ORDER_NO, orderNo);
		orderEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCode);
		return inputGetOrderLineListDoc;
	}
	 
	/**
	 * This method forms template for getOrderLineList template
	 * @return
	 */
	
	private YFCDocument getOrderLineListTemplate() {
		YFCDocument getOrderLineListTempDoc=YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
		YFCElement orderLineListEle=getOrderLineListTempDoc.getDocumentElement();
		YFCElement orderLineEle=orderLineListEle.createChild(XMLLiterals.ORDER_LINE);
		orderLineEle.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		orderLineEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
		orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		orderLineEle.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		orderLineEle.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
		orderLineEle.setAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO, EMPTY_STRING);
		YFCElement orderEle=orderLineEle.createChild(XMLLiterals.ORDER);
		orderEle.setAttribute(XMLLiterals.ORDER_NO,EMPTY_STRING);
		return getOrderLineListTempDoc;
	}
	
	/**
	 * This method invokes getOrderLineList API
	 * @param orderNo
	 * @param enterpriseCode
	 * @param shipNode
	 * @param inXml
	 * @return
	 */
	
	private String invokeGetOrderLineList(String orderNo,String enterpriseCode,String shipNode) {
		YFCDocument getOrderLineListOutputDoc= invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST,inputGetOrderLineList(shipNode,orderNo,enterpriseCode),getOrderLineListTemplate());
		YFCElement getOrderLineListOutputEle=getOrderLineListOutputDoc.getDocumentElement();
		YFCIterable<YFCElement> inputOrderLineEle = getOrderLineListOutputEle.getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderElement : inputOrderLineEle) {
			String orderLineStatus=orderElement.getAttribute(XMLLiterals.STATUS);
			if(!orderLineStatus.equals(CANCELLED))
			{
				isFullOrderCancelled=NO;
				break;
			}
		}
		return isFullOrderCancelled;
	}

	/**
	 * This method forms SAP051 message
	 * @param isFullOrderCancelled
	 * @param inXml
	 * @return
	 */
	
	private YFCDocument formMessageSAP051(String isFullOrderCancelled, YFCDocument inXml) {
		YFCElement inXmlEle=inXml.getDocumentElement();
	 	YFCDocument messageSAP051Doc=YFCDocument.createDocument(XMLLiterals.ORDER_MESSAGE);
	 	YFCElement orderMessageEle=messageSAP051Doc.getDocumentElement();
	 	orderMessageEle.setAttribute(XMLLiterals.MESSAGE_TYPE_ID, SAP051);
	 	orderMessageEle.setAttribute(XMLLiterals.MODIFYTS, inXmlEle.getAttribute(XMLLiterals.MODIFYTS));
	 	YFCElement messageBodyOrderEle=orderMessageEle.createChild(XMLLiterals.MESSAGE_BODY).createChild(XMLLiterals.ORDER);
	 	messageBodyOrderEle.setAttribute(XMLLiterals.SAP_ORDER_NO, inXmlEle.getAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO));
	 	messageBodyOrderEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, inXmlEle.getAttribute(XMLLiterals.ENTERPRISE_CODE));
	 	messageBodyOrderEle.setAttribute(XMLLiterals.DOCUMENT_TYPE, inXmlEle.getAttribute(XMLLiterals.DOCUMENT_TYPE));
	 	messageBodyOrderEle.setAttribute(XMLLiterals.ORDER_TYPE, inXmlEle.getAttribute(XMLLiterals.ORDER_TYPE));
	 	messageBodyOrderEle.setAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED,isFullOrderCancelled );
	 	YFCElement orderLinesrootEle=inXmlEle.getChildElement(XMLLiterals.ORDER_LINES);
	 	YFCIterable<YFCElement> orderLinesEle = orderLinesrootEle.getChildren();
	 	for(YFCElement msgElement : orderLinesEle) {
	 		YFCElement orderLinesmsgEle = messageBodyOrderEle.createChild(XMLLiterals.ORDER_LINES);
	 		YFCElement orderLineEle = orderLinesmsgEle.createChild(XMLLiterals.ORDER_LINE);
	 		orderLineEle.setAttribute(XMLLiterals.CURRENT_QTY, inXmlEle.getAttribute(XMLLiterals.ORDERED_QTY));
	 		orderLineEle.setAttribute(XMLLiterals.ORIGINAL_QTY, inXmlEle.getChildElement(XMLLiterals.ORDER_LINES).getChildElement(XMLLiterals.ORDER_LINE).
	 				getAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY));
	 		orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, msgElement.getAttribute(XMLLiterals.PRIME_LINE_NO));
	 		orderLineEle.setAttribute(XMLLiterals.SHIPNODE,msgElement.getAttribute(XMLLiterals.SHIPNODE));
	 		orderLineEle.setAttribute(XMLLiterals.CANCELLATION_REASON_CODE, REASON_CODE);
	 		YFCElement itemEle=orderLinesmsgEle.createChild(XMLLiterals.ITEM);
	 		itemEle.setAttribute(XMLLiterals.ITEM_ID, msgElement.getChildElement(XMLLiterals.ITEM).getAttribute(XMLLiterals.ITEM_ID));
		 	}
	 	return messageSAP051Doc;
	}
}
