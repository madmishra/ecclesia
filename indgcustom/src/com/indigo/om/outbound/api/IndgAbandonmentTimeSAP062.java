package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
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
	
	/**
	 * This is the invoke point of program
	 */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		quantityDifferenceInInpApiOp(inXml);
		return inXml;
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
			String primeLineNo = orderLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO);
			if(inpQuantity!=0) {
				adjustQuantityofInventory(inpQuantity, primeLineNo, docAdjustInv, inXml);
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
	
	private void adjustQuantityofInventory(int inpQuantity, String primeLineNo, YFCDocument docAdjustInv, YFCDocument inXml) {
		YFCElement eleItem = docAdjustInv.getDocumentElement().createChild(XMLLiterals.ITEM);
		YFCElement odrLineEle = XPathUtil.getXPathElement(inXml, "/OrderMessage/MessageBody/Order/OrderLines/OrderLine"
				+ "[@PrimeLineNo=\""+primeLineNo+"\"]");
		eleItem.setAttribute(XMLLiterals.ADJUSTMENT_TYPE, ADJUSTMENT_TYPE);
		eleItem.setAttribute(XMLLiterals.ITEM_ID, odrLineEle.getChildElement(XMLLiterals.ITEM).getAttribute(XMLLiterals.ITEM_ID));
		eleItem.setAttribute(XMLLiterals.ORGANIZATION_CODE, inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.ENTERPRISE_CODE));
		eleItem.setAttribute(XMLLiterals.QUANTITY, inpQuantity);
		eleItem.setAttribute(XMLLiterals.SHIPNODE, odrLineEle.getAttribute(XMLLiterals.SHIPNODE));
		eleItem.setAttribute(XMLLiterals.SUPPLY_TYPE, SUPPLY_TYPE);
		eleItem.setAttribute(XMLLiterals.UNIT_OF_MEASURE, UOM);
	}
}
