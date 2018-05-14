package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class IndgSAP051MsgToSAP extends AbstractCustomApi{
	private static final String EMPTY_STRING = "";
	private static  String isFullOrderCancelled="Y";
	private static final String NO="N"; 
	private static final String CANCELLED="Cancelled";
	private static final String cancellationReasonCode="05";
	
	/**
	   * This is the invoke point of the Service
	 * @throws  
	   * 
	   */
	
	 public YFCDocument invoke(YFCDocument inXml)  {
		 
		 System.out.println("Inside Main");
		 YFCElement inXmlEle=inXml.getDocumentElement();
		 String orderNo=inXmlEle.getAttribute(XMLLiterals.ORDER_NO);
		 System.out.println("---orderNo value----"+orderNo);
		 String enterpriseCode=inXmlEle.getAttribute(XMLLiterals.ENTERPRISE_CODE);
		 System.out.println("----enterPriseCode---"+enterpriseCode);
		 YFCElement orderLinesrootEle=inXmlEle.getChildElement(XMLLiterals.ORDER_LINES);
		 System.out.println("----list of children-----"+orderLinesrootEle);
		 YFCIterable<YFCElement> orderLineListEle =orderLinesrootEle.getChildren();
		 System.out.println(orderLineListEle+"-------------Children check------------------");
		 for(YFCElement orderElement : orderLineListEle) {
		 String shipNode=orderElement.getAttribute(XMLLiterals.SHIPNODE);
		 System.out.println("---shipNode--"+shipNode);
		 isFullOrderCancelled=invokeGetOrderLineList(orderNo,enterpriseCode,shipNode,inXml);
		 }
		YFCDocument messageSAP051Doc=formMessageSAP051(isFullOrderCancelled,inXml);
		System.out.println("final message----"+messageSAP051Doc);
		 return messageSAP051Doc;
}
	 private YFCDocument inputGetOrderLineList(String shipNode,String orderNo,String enterpriseCode) {
		 YFCDocument inputGetOrderLineListDoc=YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
		 YFCElement orderLineEle=inputGetOrderLineListDoc.getDocumentElement();
		 orderLineEle.setAttribute(XMLLiterals.SHIPNODE,shipNode );
		 YFCElement orderEle=orderLineEle.createChild(XMLLiterals.ORDER);
		 orderEle.setAttribute(XMLLiterals.ORDER_NO, orderNo);
		 orderEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCode);
		 System.out.println("----inputGetOrderLineList-----"+inputGetOrderLineListDoc);
		 return inputGetOrderLineListDoc;
	 }
	 private YFCDocument getOrderLineListTemplate() {
		 YFCDocument getOrderLineListTempDoc=YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
		 YFCElement orderLineListEle=getOrderLineListTempDoc.getDocumentElement();
		 YFCElement orderLineEle=orderLineListEle.createChild(XMLLiterals.ORDER_LINE);
		 orderLineEle.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		 orderLineEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
		 orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		 orderLineEle.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		 orderLineEle.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
		 YFCElement extnEle=orderLineEle.createChild(XMLLiterals.EXTN);
		 extnEle.setAttribute(XMLLiterals.EXTN_SAP_ORDER_NO, EMPTY_STRING);
		 YFCElement orderEle=orderLineEle.createChild(XMLLiterals.ORDER);
		 orderEle.setAttribute(XMLLiterals.ORDER_NO,EMPTY_STRING);
		 System.out.println("----getOrderLineListTempDoc---"+getOrderLineListTempDoc);
		 return getOrderLineListTempDoc;
		 
	 }
	 
	 private String invokeGetOrderLineList(String orderNo,String enterpriseCode,String shipNode,YFCDocument inXml)
	 {
		YFCDocument getOrderLineListOutputDoc= invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST,inputGetOrderLineList(shipNode,orderNo,enterpriseCode),getOrderLineListTemplate());
		System.out.println(getOrderLineListOutputDoc+"----output");
		YFCElement getOrderLineListOutputEle=getOrderLineListOutputDoc.getDocumentElement();
		YFCIterable<YFCElement> inputOrderLineEle = getOrderLineListOutputEle.getChildren(XMLLiterals.ORDER_LINE);
		 for(YFCElement orderElement : inputOrderLineEle) {
		String orderLineStatus=orderElement.getAttribute(XMLLiterals.STATUS);
		System.out.println(orderLineStatus+"------orderLineStatus---");
		if(!orderLineStatus.equals(CANCELLED))
		{
			isFullOrderCancelled=NO;
			System.out.println("----IS_FULL_ORDER_CANCELLED-----"+isFullOrderCancelled);
			break;
		}
			
	 }
		 return isFullOrderCancelled;
		
		 
	 }

 private YFCDocument  formMessageSAP051(String isFullOrderCancelled,YFCDocument  inXml) {
	 	YFCElement inXmlEle=inXml.getDocumentElement();
	 	String modifyts= inXmlEle.getAttribute(XMLLiterals.MODIFYTS);
	 	System.out.println("---messageSAP051Doc-----"+modifyts);
	 	String sapOrderNo=inXmlEle.getAttribute(XMLLiterals.EXTN_SAP_ORDER_NO);
	 	System.out.println("------sapOrderNo---"+sapOrderNo);
	 	String enterpriseCode=inXmlEle.getAttribute(XMLLiterals.ENTERPRISE_CODE);
	 	System.out.println("-----enterpriseCode---"+enterpriseCode);
	 	String documentType=inXmlEle.getAttribute(XMLLiterals.DOCUMENT_TYPE);
	 	System.out.println("-----documentType---"+documentType);
	 	String orderType=inXmlEle.getAttribute(XMLLiterals.ORDER_TYPE);
	 	System.out.println("------orderType---"+orderType);
	 	String currentQty=inXmlEle.getAttribute(XMLLiterals.ORDERED_QTY);
	 	System.out.println("---------currentQty----"+currentQty);
	 	String originalQty=inXmlEle.getAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY);
	 	System.out.println("----originalQty---"+originalQty);
	 
		 YFCDocument messageSAP051Doc=YFCDocument.createDocument(XMLLiterals.ORDER_MESSAGE);
		 YFCElement orderMessageEle=messageSAP051Doc.getDocumentElement();
		 orderMessageEle.setAttribute(XMLLiterals.MESSAGE_TYPE_ID, "SAP051");
		 orderMessageEle.setAttribute(XMLLiterals.MODIFYTS, modifyts);
		 YFCElement messageBodyOrderEle=orderMessageEle.createChild(XMLLiterals.MESSAGE_BODY).createChild(XMLLiterals.ORDER);
		 messageBodyOrderEle.setAttribute(XMLLiterals.SAP_ORDER_NO, sapOrderNo);
		 messageBodyOrderEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCode);
		 messageBodyOrderEle.setAttribute(XMLLiterals.DOCUMENT_TYPE, documentType);
		 messageBodyOrderEle.setAttribute(XMLLiterals.ORDER_TYPE, orderType);
		 messageBodyOrderEle.setAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED,isFullOrderCancelled );
		  
		 YFCElement orderLinesrootEle=inXmlEle.getChildElement(XMLLiterals.ORDER_LINES);
		 System.out.println("----list of children-----"+orderLinesrootEle);
		 YFCIterable<YFCElement> orderLinesEle =orderLinesrootEle.getChildren();
		 for(YFCElement msgElement : orderLinesEle) {
		 String primeLineNo=msgElement.getAttribute(XMLLiterals.PRIME_LINE_NO);
		 String shipNode=msgElement.getAttribute(XMLLiterals.SHIPNODE);
		 String itemId=msgElement.getChildElement(XMLLiterals.ITEM).getAttribute(XMLLiterals.ITEM_ID);
		 YFCElement orderLinesmsgEle=messageBodyOrderEle.createChild(XMLLiterals.ORDER_LINES);
		 YFCElement orderLineEle= orderLinesmsgEle.createChild(XMLLiterals.ORDER_LINE);
		 orderLineEle.setAttribute(XMLLiterals.CURRENT_QTY, currentQty);
		 orderLineEle.setAttribute(XMLLiterals.ORIGINAL_QTY, originalQty);
		 orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, primeLineNo);
		 orderLineEle.setAttribute(XMLLiterals.SHIPNODE,shipNode);
		 orderLineEle.setAttribute(XMLLiterals.CANCELLATION_REASON_CODE, cancellationReasonCode);
		 YFCElement itemEle=orderLinesmsgEle.createChild(XMLLiterals.ITEM);
		 itemEle.setAttribute(XMLLiterals.ITEM_ID, itemId);
		 }
		 return messageSAP051Doc;
		 
	 }
	 
}


