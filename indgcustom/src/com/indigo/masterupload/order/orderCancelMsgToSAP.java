package com.indigo.masterupload.ordercancelMsg;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class orderCancelMsgToSAP extends AbstractCustomApi{
	private static final String EMPTY_STRING = "";
	private static  String IS_FULL_ORDER_CANCELLED="N";
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
		 YFCElement orderLineEle=inXmlEle.getChildElement(XMLLiterals.ORDER_LINES).getChildElement(XMLLiterals.ORDER_LINE);
		 String shipNode=orderLineEle.getAttribute(XMLLiterals.SHIPNODE);
		 System.out.println("---shipNode--"+shipNode);
		 invokeGetOrderLineList(orderNo,enterPriseCode,shipNode,inXml);
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
		 YFCElement extnEle=orderLineListEle.createChild(XMLLiterals.EXTN);
		 extnEle.setAttribute(XMLLiterals.EXTN_SAP_ORDER_NO, EMPTY_STRING);
		 YFCElement orderEle=orderLineListEle.createChild(XMLLiterals.ORDER);
		 orderEle.setAttribute(XMLLiterals.ORDER_NO,EMPTY_STRING);
		 System.out.println("----getOrderLineListTempDoc---"+getOrderLineListTempDoc);
		 return getOrderLineListTempDoc;
		 
	 }
	 
	 private void invokeGetOrderLineList(String orderNo,String enterPriseCode,String shipNode,YFCDocument inXml)
	 {
		YFCDocument getOrderLineListOutputDoc= invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST,inputGetOrderLineList(shipNode,orderNo,enterPriseCode),getOrderLineListTemplate());
		YFCElement getOrderLineListOutputEle=getOrderLineListOutputDoc.getDocumentElement();
		YFCIterable<YFCElement> inputOrderLineEle = getOrderLineListOutputEle.getChildren(XMLLiterals.ORDER_LINE);
		 for(YFCElement orderElement : inputOrderLineEle) {
		String orderLineStatus=orderElement.getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.STATUS);
		System.out.println(orderLineStatus+"------orderLineStatus---");
		if(!orderLineStatus.equals("Cancelled"))
		{
			IS_FULL_ORDER_CANCELLED=YES;
			System.out.println("----IS_FULL_ORDER_CANCELLED-----"+IS_FULL_ORDER_CANCELLED);
		}
			
	 }
		// formMessageSAP051(IS_FULL_ORDER_CANCELLED,inXml);
		 
	 }

 /*private YFCDocument  formMessageSAP051(String IS_FULL_ORDER_CANCELLED,YFCDocument  inXml) {
	 	YFCElement orderEle=inXml.getDocumentElement();
	 	String modifyts= orderEle.getAttribute("Modifyts");
	 
		 YFCDocument messageSAP051Doc=YFCDocument.createDocument("OrderMessage");
		 YFCElement orderMessageEle=messageSAP051Doc.getDocumentElement();
		 orderMessageEle.setAttribute("MessageTypeId", "SAP051");
		 orderMessageEle.setAttribute("Modifyts", value);
		 return doc;
		 
	 }*/
	 
}