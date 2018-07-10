package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * @author BSG168
 * 
 * Custom API to consume SAP062 message and adjust the inventory
 * based on the quantity and for the Legacy062 message.
 *
 */

public class IndgAbandonmentTimeSAP062 extends AbstractCustomApi {

	private static final String ADJUSTMENT_TYPE = "ADJUSTMENT";
	private static final String SUPPLY_TYPE = "ONHAND";
	private static final String UOM = "EACH";
	private static final String EMPTY_STRING = "";
	
	/**
	 * This is the invoke point of program
	 */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCDocument docGetOrderLineListOp = getOrderLineListApi(inXml);
		quantityDifferenceInInpApiOp(inXml);
		String legacyOmsOrderNo = docGetOrderLineListOp.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
				getAttribute(XMLLiterals.CUSTOMER_PO_NO);
		String modifyTs = docGetOrderLineListOp.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
				getAttribute(XMLLiterals.MODIFYTS);
		inXml.getDocumentElement().setAttribute(XMLLiterals.LEGACY_OMS_ORDER_NO, legacyOmsOrderNo);
		inXml.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, modifyTs);
		return inXml;
	}
	
	/**
	 * This method forms the input document for getOrderLineList API.
	 * @param orderEle
	 * @return
	 */
	
	public YFCDocument docForGetOrderLineListInp(YFCElement orderEle) {
	    YFCDocument getOrderLineListDoc = YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
	    getOrderLineListDoc.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, orderEle.getChildElement(XMLLiterals.ORDER_LINES).
	    		getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.SHIPNODE));
	    YFCElement eleOrder = getOrderLineListDoc.getDocumentElement().createChild(XMLLiterals.ORDER);
	    eleOrder.setAttribute(XMLLiterals.ORDER_NO, orderEle.getAttribute(XMLLiterals.STERLING_ORDER_NO));
	    eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, orderEle.getAttribute(XMLLiterals.ENTERPRISE_CODE));
	    eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, orderEle.getAttribute(XMLLiterals.DOCUMENT_TYPE));
	    return getOrderLineListDoc;
	}
	
	/**
	 * This method forms the template for getOrderLineList API.
	 * @return
	 */
	
	public YFCDocument docForGetOrderLineListTemplate() {
	    YFCDocument getOrderLineListTemp = YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
	    YFCElement orderLineEle = getOrderLineListTemp.getDocumentElement().createChild(XMLLiterals.ORDER_LINE);
	    orderLineEle.setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.CUSTOMER_PO_NO, EMPTY_STRING);
	    return getOrderLineListTemp;
	}
	
	/**
	 * This method invokes the getOrderLineList API.
	 * @param inXml
	 * @return
	 */
	
	public YFCDocument getOrderLineListApi(YFCDocument inXml){
		YFCElement orderEle = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
	    return  invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST, docForGetOrderLineListInp(orderEle), docForGetOrderLineListTemplate());
	}
	
	 /**
	  * This method fetches the quantity attribute from inXML and getShipmentDetails
	  * output document and compares the quantity and finds the difference.
	  * @param docGetShipmentDetails
	  * @param inXml
	  */
	
	private void quantityDifferenceInInpApiOp(YFCDocument inXml) {
		YFCDocument docAdjustInv = YFCDocument.createDocument(XMLLiterals.ITEMS);
		YFCIterable<YFCElement> yfsItrator = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES).getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderLineEle : yfsItrator) {
			String quantity1 = orderLineEle.getAttribute(XMLLiterals.QUANTITY_SHORT);
			int inpQuantity = -(int) Double.parseDouble(quantity1);
			if(inpQuantity!=0) {
				adjustQuantityofInventory(inpQuantity, orderLineEle, docAdjustInv, inXml);
			}
		}
		invokeYantraApi(XMLLiterals.ADJUST_INVENTORY_API, docAdjustInv);
	}
	
	/**
	 * This method forms the adjustInventory API input and increases the inventory
	 * by the difference of two quantities.
	 * @param docGetShipmentDetails
	 * @param shipmentLineEle
	 * @param quantityDiff
	 */
	
	private void adjustQuantityofInventory(int inpQuantity, YFCElement orderLineEle, YFCDocument docAdjustInv, YFCDocument inXml) {
		YFCElement eleItem = docAdjustInv.getDocumentElement().createChild(XMLLiterals.ITEM);
		eleItem.setAttribute(XMLLiterals.ADJUSTMENT_TYPE, ADJUSTMENT_TYPE);
		eleItem.setAttribute(XMLLiterals.ITEM_ID, orderLineEle.getChildElement(XMLLiterals.ITEM).getAttribute(XMLLiterals.ITEM_ID));
		eleItem.setAttribute(XMLLiterals.ORGANIZATION_CODE, inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.ENTERPRISE_CODE));
		eleItem.setAttribute(XMLLiterals.QUANTITY, inpQuantity);
		eleItem.setAttribute(XMLLiterals.SHIPNODE, orderLineEle.getAttribute(XMLLiterals.SHIPNODE));
		eleItem.setAttribute(XMLLiterals.SUPPLY_TYPE, SUPPLY_TYPE);
		eleItem.setAttribute(XMLLiterals.UNIT_OF_MEASURE, UOM);
	}
}
