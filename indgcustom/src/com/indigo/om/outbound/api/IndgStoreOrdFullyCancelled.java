package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class IndgStoreOrdFullyCancelled extends AbstractCustomApi{
	private static final String EMPTY_STRING = "";
	private static  String isFullOrderCancelled="Y";
	private static final String NO="N"; 
	private static final String CANCELLED="Cancelled";
	private static final String INDG_SEND_CANCELLED_LINES="Indg_SendCancelledLines";
	
	/**
	   * This is the invoke point of the Service
	 * @throws  
	   * 
	   */
	
	 public YFCDocument invoke(YFCDocument inXml)  {
		 YFCElement inXmlEle=inXml.getDocumentElement();
		 String orderNo=inXmlEle.getAttribute(XMLLiterals.ORDER_NO);
		 String enterpriseCode=inXmlEle.getAttribute(XMLLiterals.ENTERPRISE_CODE);
		 YFCElement orderLinesrootEle=inXmlEle.getChildElement(XMLLiterals.ORDER_LINES);
		 YFCIterable<YFCElement> orderLineListEle =orderLinesrootEle.getChildren();
		 for(YFCElement orderElement : orderLineListEle) {
		 String shipNode=orderElement.getAttribute(XMLLiterals.SHIPNODE);
		if((isFullOrderCancelled=invokeGetOrderLineList(orderNo,enterpriseCode,shipNode,inXml)).equals(NO))
		break;
		 }
		 inXmlEle.setAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED, isFullOrderCancelled);
		
		 //invokeService(inXml);
		 return inXml;
}
	 /** This method forms input for getOrderLineList api
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
		 YFCElement extnEle=orderLineEle.createChild(XMLLiterals.EXTN);
		 extnEle.setAttribute(XMLLiterals.EXTN_SAP_ORDER_NO, EMPTY_STRING);
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
	 private String invokeGetOrderLineList(String orderNo,String enterpriseCode,String shipNode,YFCDocument inXml)
	 {
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
	 /**This method invokes service Indg_SendCancelledLines
	  * 
	  * @param inXml
	  */

/* private void invokeService(YFCDocument inXml) {
	 invokeYantraService(INDG_SEND_CANCELLED_LINES,inXml);
 }
	 */
}



