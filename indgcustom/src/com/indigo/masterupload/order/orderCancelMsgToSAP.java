package com.indigo.masterupload.order;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class orderCancelMsgToSAP extends AbstractCustomApi{
	private static final String EMPTY_STRING = "";
	private static  String isFullOrderCancelled="N";
	private static final String YES="Y";
	private static final String CANCELLED="Cancelled";
	
	
	/**
	   * This is the invoke point of the Service
	 * @throws  
	   * 
	   */
	
	 public YFCDocument invoke(YFCDocument inXml)  {
		 
		 YFCElement inXmlEle=inXml.getDocumentElement();
		 String orderNo=inXmlEle.getAttribute(XMLLiterals.ORDER_NO);
		 System.out.println("---orderNo value----"+orderNo);
		 String enterPriseCode=inXmlEle.getAttribute(XMLLiterals.ENTERPRISE_CODE);
		 System.out.println("----enterPriseCode---"+enterPriseCode);
		 YFCIterable<YFCElement> orderLinesEle =inXmlEle.getChildren(XMLLiterals.ORDER_LINES);
		 for(YFCElement orderElement : orderLinesEle) {
		 YFCElement orderLineEle=orderElement.getChildElement(XMLLiterals.ORDER_LINE);
		 String shipNode=orderLineEle.getAttribute(XMLLiterals.SHIPNODE);
		 System.out.println("---shipNode--"+shipNode);
		 invokeGetOrderLineList(orderNo,enterPriseCode,shipNode,inXml);
		 }
		 return inXml;
}
	 private YFCDocument inputGetOrderLineList(String shipNode,String orderNo,String enterPriseCode) {
		 YFCDocument inputGetOrderLineListDoc=YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
		 YFCElement orderLineEle=inputGetOrderLineListDoc.getDocumentElement();
		 orderLineEle.setAttribute(XMLLiterals.SHIPNODE,shipNode );
		 YFCElement orderEle=orderLineEle.createChild(XMLLiterals.ORDER);
		 orderEle.setAttribute(XMLLiterals.ORDER_NO, orderNo);
		 orderEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, enterPriseCode);
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
	 
	 private void invokeGetOrderLineList(String orderNo,String enterPriseCode,String shipNode,YFCDocument inXml)
	 {
		YFCDocument getOrderLineListOutputDoc= invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST,inputGetOrderLineList(shipNode,orderNo,enterPriseCode),getOrderLineListTemplate());
		System.out.println(getOrderLineListOutputDoc+"----output");
		YFCElement getOrderLineListOutputEle=getOrderLineListOutputDoc.getDocumentElement();
		YFCIterable<YFCElement> inputOrderLineEle = getOrderLineListOutputEle.getChildren(XMLLiterals.ORDER_LINE);
		 for(YFCElement orderElement : inputOrderLineEle) {
		String orderLineStatus=orderElement.getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.STATUS);
		System.out.println(orderLineStatus+"------orderLineStatus---");
		if(!orderLineStatus.equals("Cancelled"))
		{
			isFullOrderCancelled=YES;
			System.out.println("----IS_FULL_ORDER_CANCELLED-----"+isFullOrderCancelled);
		}
			
	 }
		// formMessageSAP051(IS_FULL_ORDER_CANCELLED,inXml);
		 
	 }

 /*private YFCDocument  formMessageSAP051(String isFullOrderCancelled,YFCDocument  inXml) {
	 	YFCElement orderEle=inXml.getDocumentElement();
	 	String modifyts= orderEle.getAttribute(XMLLiterals.MODIFYTS);
	 	String sapOrderNo=orderEle.getAttribute(XMLLiterals.EXTN_SAP_ORDER_NO);
	 	String enterpriseCode=orderEle.getAttribute(XMLLiterals.ENTERPRISE_CODE);
	 	String documentType=orderEle.getAttribute(XMLLiterals.DOCUMENT_TYPE);
	 	String orderType=orderEle.getAttribute(XMLLiterals.ORDER_TYPE);
	 	String currentQty=orderEle.getAttribute(XMLLiterals.ORDERED_QTY);
	 	String originalQty=orderEle.getAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY);
	 
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
		  
		 YFCIterable<YFCElement> orderLinesEle =inXmlEle.getChildren(XMLLiterals.ORDER_LINES);
		 
		 YFCElement orderLineEle=orderMessageEle.createChild(XMLLiterals.ORDER_LINES).createChild(XMLLiterals.ORDER_LINE);
		 orderLineEle.setAttribute(XMLLiterals.CURRENT_QTY, currentQty);
		 orderLineEle.setAttribute(XMLLiterals.ORIGINAL_QTY, originalQty);
		 orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, value);
		 
		 
		 return doc;
		 
	 }*/
	 
}