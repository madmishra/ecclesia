package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
/**
 * 
 * 
 * @author BSG170
 *
 */
public class IndgStoreOrdFullyCancelled extends AbstractCustomApi{
	private static final String EMPTY_STRING = "";
	private String sIsFullOrderCancelled="Y";
	private static final String NO="N"; 
	private static final String CANCELLED="Cancelled";
	private  String sModifyts="";
	
	 /**
	   * This is the invoke point of the Service
	   * @throws  
	   * 
	   */
	
	  @Override
	  public YFCDocument invoke(YFCDocument docInXml)  {
		  YFCElement eleInXml=docInXml.getDocumentElement();
		  String sOrderNo=eleInXml.getAttribute(XMLLiterals.ORDER_NO);
		  String sEnterpriseCode=eleInXml.getAttribute(XMLLiterals.ENTERPRISE_CODE);
		  YFCElement orderLinesrootEle=eleInXml.getChildElement(XMLLiterals.ORDER_LINES);
		  YFCIterable<YFCElement> orderLineListEle =orderLinesrootEle.getChildren();
		  for(YFCElement orderElement : orderLineListEle) {
			  String sShipNode=orderElement.getAttribute(XMLLiterals.SHIPNODE);
			  sIsFullOrderCancelled=invokeGetOrderLineList(sOrderNo,sEnterpriseCode,sShipNode);
			  if((sIsFullOrderCancelled).equals(NO))
				  break;
		  }
		  eleInXml.setAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED, sIsFullOrderCancelled);
		  eleInXml.setAttribute(XMLLiterals.MODIFYTS, sModifyts);
		  return docInXml;
	  }
	  
	 /** This method forms input for getOrderLineList api
	  * 
	  * @param shipNode
	  * @param orderNo
	  * @param enterpriseCode
	  * @return
	  */
	  
	 private YFCDocument inputGetOrderLineList(String sShipNode,String sOrderNo,String sEnterpriseCode) {
		 YFCDocument docInputGetOrderLineList=YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
		 YFCElement eleOrderLineEle=docInputGetOrderLineList.getDocumentElement();
		 eleOrderLineEle.setAttribute(XMLLiterals.SHIPNODE,sShipNode );
		 YFCElement eleOrder=eleOrderLineEle.createChild(XMLLiterals.ORDER);
		 eleOrder.setAttribute(XMLLiterals.ORDER_NO, sOrderNo);
		 eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, sEnterpriseCode);
		 return docInputGetOrderLineList;
	 }
	 
	 /**
	  * This method forms template for getOrderLineList template
	  * @return
	  */
	 
	 private YFCDocument getOrderLineListTemplate() {
		 YFCDocument docGetOrderLineListTemp=YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
		 YFCElement eleOrderLineList=docGetOrderLineListTemp.getDocumentElement();
		 YFCElement eleOrderLine=eleOrderLineList.createChild(XMLLiterals.ORDER_LINE);
		 eleOrderLine.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		 eleOrderLine.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
		 eleOrderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		 eleOrderLine.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		 eleOrderLine.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
		 eleOrderLine.setAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO, EMPTY_STRING);
		 YFCElement eleOrder=eleOrderLine.createChild(XMLLiterals.ORDER);
		 eleOrder.setAttribute(XMLLiterals.ORDER_NO,EMPTY_STRING);
		 return docGetOrderLineListTemp;
		 
	 }
	 
	  /**
	   * This method invokes getOrderLineList API
	   * @param orderNo
	   * @param enterpriseCode
	   * @param shipNode
	   * @param inXml
	   * @return
	   */
	 
	 private String invokeGetOrderLineList(String sOrderNo,String sEnterpriseCode,String sShipNode)
	 {
		YFCDocument docGetOrderLineListOutput= invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST,inputGetOrderLineList(sShipNode,sOrderNo,sEnterpriseCode),getOrderLineListTemplate());
		YFCElement eleGetOrderLineListOutput=docGetOrderLineListOutput.getDocumentElement();
		sModifyts=eleGetOrderLineListOutput.getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.MODIFYTS);
		YFCIterable<YFCElement> eleInputOrderLine = eleGetOrderLineListOutput.getChildren(XMLLiterals.ORDER_LINE);
		 for(YFCElement eleOrder : eleInputOrderLine) {
		String sOrderLineStatus=eleOrder.getAttribute(XMLLiterals.STATUS);
		if(!sOrderLineStatus.equals(CANCELLED))
		{
			sIsFullOrderCancelled=NO;
			break;
		}
	 }
		 return sIsFullOrderCancelled;	 
	 }
}



