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
	
	
	/**
	   * This is the invoke point of the Service
	 * @throws  
	   * 
	   */
	
	 public YFCDocument invoke(YFCDocument inXml)  {
		 
		 YFCElement inXmlEle=inXml.getDocumentElement();
		 YFCElement orderEle=inXmlEle.getChildElement("Order");
		 String orderNo=orderEle.getAttribute("OrderNo");
		 System.out.println("---orderNo value----"+orderNo);
		 String enterPriseCode=orderEle.getAttribute("EnterPriseCode");
		 System.out.println("----enterPriseCode---"+enterPriseCode);
		 YFCElement orderLineEle=orderEle.getChildElement("OrderLines").getChildElement("OrderLine");
		 String shipNode=orderLineEle.getAttribute("ShipNode");
		 System.out.println("---shipNode--"+shipNode);
		 invokeGetOrderLineList(orderNo,enterPriseCode,shipNode);
		 return inXml;
}
	 private YFCDocument inputGetOrderLineList(String shipNode,String orderNo,String enterPriseCode) {
		 YFCDocument inputGetOrderLineListDoc=YFCDocument.createDocument();
		 YFCElement orderLineEle=inputGetOrderLineListDoc.createElement("OrdeLine");
		 orderLineEle.setAttribute("ShipNode",shipNode );
		 YFCElement orderEle=orderLineEle.createChild("Order");
		 orderEle.setAttribute("OrderNo", orderNo);
		 orderEle.setAttribute("EnterpriseCode", enterPriseCode);
		 System.out.println("----inputGetOrderLineList-----"+inputGetOrderLineListDoc);
		 return inputGetOrderLineListDoc;
	 }
	 private YFCDocument getOrderLineListTemplate() {
		 YFCDocument getOrderLineListTempDoc=YFCDocument.createDocument("OrderLineList");
		 YFCElement orderLineListEle=getOrderLineListTempDoc.getDocumentElement();
		 YFCElement orderLineEle=orderLineListEle.createChild("OrderLine");
		 orderLineEle.setAttribute("DocumentType", EMPTY_STRING);
		 orderLineEle.setAttribute("EnterpriseCode", EMPTY_STRING);
		 orderLineEle.setAttribute("PrimeLineNo", EMPTY_STRING);
		 orderLineEle.setAttribute("ShipNode", EMPTY_STRING);
		 orderLineEle.setAttribute("Status", EMPTY_STRING);
		 YFCElement extnEle=orderLineListEle.createChild("Extn");
		 extnEle.setAttribute("ExtnSAPOrderNo", EMPTY_STRING);
		 YFCElement orderEle=orderLineListEle.createChild("Order");
		 orderEle.setAttribute("OrderNo",EMPTY_STRING);
		 System.out.println("----getOrderLineListTempDoc---"+getOrderLineListTempDoc);
		 return getOrderLineListTempDoc;
		 
	 }
	 
	 private void invokeGetOrderLineList(String orderNo,String enterPriseCode,String shipNode)
	 {
		YFCDocument getOrderLineListOutputDoc= invokeYantraApi("getOrderLineList",inputGetOrderLineList(shipNode,orderNo,enterPriseCode),getOrderLineListTemplate());
		YFCElement getOrderLineListOutputEle=getOrderLineListOutputDoc.getDocumentElement();
		YFCIterable<YFCElement> inputOrderLineEle = getOrderLineListOutputEle.getChildren("OrderLine");
		 for(YFCElement orderElement : inputOrderLineEle) {
		String orderLineStatus=orderElement.getChildElement("OrderLine").getAttribute("Status");
		System.out.println(orderLineStatus);
		if(!orderLineStatus.equals("Cancelled"))
		{
			IS_FULL_ORDER_CANCELLED=YES;
			System.out.println("----IS_FULL_ORDER_CANCELLED-----"+IS_FULL_ORDER_CANCELLED);
		}
			
	 }
		 
	 }
	 
}