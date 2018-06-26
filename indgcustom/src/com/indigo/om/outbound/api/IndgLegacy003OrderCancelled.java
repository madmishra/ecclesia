package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;

public class IndgLegacy003OrderCancelled extends AbstractCustomApi {
	
	private static final String EMPTY_STRING = "";
	private static final String CANCEL_ORDER_STATUS = "Cancelled";
	private static final String CALL_LEGACYOMS003_SERVICE = "CALL_LEGACYOMS003_SERVICE";
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCDocument docGetOrderLineList = getOrderLineListFunc(inXml);
		String modifyTs = docGetOrderLineList.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
				getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.MODIFYTS);
		inXml.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, modifyTs);
		callLegacy003OnScheduleQueue(docGetOrderLineList, inXml);
		callLegacy003OnScheduleQueue(inXml);
		return inXml;
	}
	
	/**
     * this method forms input to getOrderLineList API
     * 
     * @param docInXml
     * @return
     */
   
   public YFCDocument docGetOrderLineListInput(YFCDocument docInXml) {
       YFCElement eleInXmlOrder = docInXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
       		getChildElement(XMLLiterals.ORDER);
       String sOrderNo = eleInXmlOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO);
       String sEnterpriseCode = eleInXmlOrder.getAttribute(XMLLiterals.ENTERPRISE_CODE);
       String sDocumentType = eleInXmlOrder.getAttribute(XMLLiterals.DOCUMENT_TYPE);
       YFCElement eleOrderLine = eleInXmlOrder.getChildElement(XMLLiterals.ORDER_LINES).getChildElement(XMLLiterals.ORDER_LINE);
       String sShipnode = eleOrderLine.getAttribute(XMLLiterals.SHIPNODE);
       YFCDocument docGetOrderLineList = YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
       docGetOrderLineList.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, sShipnode);
       YFCElement eleOrder = docGetOrderLineList.getDocumentElement().createChild(XMLLiterals.ORDER);
       eleOrder.setAttribute(XMLLiterals.ORDER_NO, sOrderNo);
       eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, sEnterpriseCode);
       eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, sDocumentType);
       return docGetOrderLineList;
     }
   
   /**
    * this method forms template for getOrderLineList API
    * @return
    */
   
   public YFCDocument docGetOrderLineListTemplate() {
       YFCDocument docGetOrderListTemp = YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
       YFCElement eleOrderLine = docGetOrderListTemp.getDocumentElement().createChild(XMLLiterals.ORDER_LINE);
       eleOrderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
       eleOrderLine.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
       eleOrderLine.setAttribute(XMLLiterals.SUB_LINE_NO, EMPTY_STRING);
       eleOrderLine.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
       eleOrderLine.setAttribute(XMLLiterals.ORDERED_QTY, EMPTY_STRING);
       eleOrderLine.setAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY, EMPTY_STRING);
       eleOrderLine.setAttribute(XMLLiterals.CONDITION_VARIABLE_1, EMPTY_STRING);
       eleOrderLine.setAttribute(XMLLiterals.CONDITION_VARIABLE_2, EMPTY_STRING);
       YFCElement eleItemEle = eleOrderLine.createChild(XMLLiterals.ITEM);
       eleItemEle.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
       YFCElement eleOrder = eleOrderLine.createChild(XMLLiterals.ORDER);
       eleOrder.setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
       eleOrder.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.ORDER_TYPE, EMPTY_STRING);
       eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
       eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
       YFCElement eleOrderStatus = eleOrderLine.createChild(XMLLiterals.ORDER_STATUSES);
       YFCElement eleStatus = eleOrderStatus.createChild(XMLLiterals.ORDER_STATUS);
       eleStatus.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
       return docGetOrderListTemp;
     }
   
   /**
    * 
    * this method invokes getOrderLineList API
    * @param docInXml
    * @return
    */
   
   public YFCDocument getOrderLineListFunc(YFCDocument inXml){
       return  invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST, docGetOrderLineListInput(inXml), docGetOrderLineListTemplate());
    }
   
   private void callLegacy003OnScheduleQueue(YFCDocument docGetOrderLineList, YFCDocument inXml) {
   	YFCIterable<YFCElement> yfsItratorPrimeLine = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
   			getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES).getChildren(XMLLiterals.ORDER_LINE);
   	for(YFCElement orderLineEle : yfsItratorPrimeLine) {
   		String primeLineNo = orderLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO);
   		YFCElement odrLineEle = XPathUtil.getXPathElement(docGetOrderLineList, "/OrderLineList/OrderLine[@PrimeLineNo=\""+primeLineNo+"\"]");
   		if(!XmlUtils.isVoid(odrLineEle)) {
   			String status = odrLineEle.getAttribute(XMLLiterals.STATUS);
   			if(status.equals(CANCEL_ORDER_STATUS)) {
   				YFCNode parent = orderLineEle.getParentNode();
   	   			parent.removeChild(orderLineEle);
   				}
   			}
   		}
   	invokeYantraService(getProperty(CALL_LEGACYOMS003_SERVICE), inXml);
   	}
   
   private void callLegacy003OnScheduleQueue(YFCDocument doc) {
	     invokeYantraService(getProperty(CALL_LEGACYOMS003_SERVICE), doc);
	}

}
