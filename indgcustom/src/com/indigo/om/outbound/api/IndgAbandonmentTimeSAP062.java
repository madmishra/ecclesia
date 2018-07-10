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
 */

public class IndgAbandonmentTimeSAP062 extends AbstractCustomApi {

	private static final String EMPTY_STRING = "";
	private static final String ADJUSTMENT_TYPE = "ADJUSTMENT";
	private static final String SUPPLY_TYPE = "ONHAND";
	private static final String UOM = "EACH";
	private static final String ORGANIZATION_CODE_VAL = "Indigo_CA";
	
	/**
	 * This is the invoke point of program
	 */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCDocument shipmentListApiOp = getShipmentList(inXml);
		YFCDocument docGetShipmentDetails = getShipmentDetailsAPI(shipmentListApiOp);
		String legacyOmsOrderNo = docGetShipmentDetails.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO);
		String modifyTs = docGetShipmentDetails.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.MODIFYTS);
		inXml.getDocumentElement().setAttribute(XMLLiterals.LEGACY_OMS_ORDER_NO, legacyOmsOrderNo);
		inXml.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, modifyTs);
		quantityDifferenceInInpApiOp(docGetShipmentDetails, inXml);
		return inXml;
	}
	
	/**
	 * This method forms the input document for getShipmentList API
	 * @param orderNo
	 * @return
	 */
	
	public YFCDocument inputXmlForGetShipmentList(String orderNo) {
	    YFCDocument getShipmentListDoc = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
	    YFCElement shipmentLinesEle = getShipmentListDoc.getDocumentElement().createChild(XMLLiterals.SHIPMENT_LINES);
	    YFCElement shipmentLineEle = shipmentLinesEle.createChild(XMLLiterals.SHIPMENT_LINE);
	    shipmentLineEle.setAttribute(XMLLiterals.ORDER_NO, orderNo);
	    return getShipmentListDoc;
	  }
	
	/**
	 * This method forms the template for getShipmentList API
	 * @return
	 */
	
	public YFCDocument inputTemplateForGetShipmentList() {
	    YFCDocument getShipmentListTemp = YFCDocument.createDocument(XMLLiterals.SHIPMENTS);
	    YFCElement shipmentEle = getShipmentListTemp.getDocumentElement().createChild(XMLLiterals.SHIPMENT);
	    shipmentEle.setAttribute(XMLLiterals.SHIPMENT_KEY, EMPTY_STRING);
	    shipmentEle.setAttribute(XMLLiterals.SHIPMENT_NO, EMPTY_STRING);
	    shipmentEle.setAttribute(XMLLiterals.SELLER_ORGANIZATION_CODE, EMPTY_STRING);
	    shipmentEle.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
	    return getShipmentListTemp;
	  }
	
	/**
	 * This method invokes getShipmentList API
	 * @param inXml
	 * @return
	 */
	
	public YFCDocument getShipmentList(YFCDocument inXml){
		String orderNo = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).
				getAttribute(XMLLiterals.STERLING_ORDER_NO);
	    return invokeYantraApi(XMLLiterals.GET_SHIPMENT_LIST, inputXmlForGetShipmentList(orderNo), inputTemplateForGetShipmentList());
	 }
	
	/**
	 * This method forms the input document for getShipmentDetails API
	 * @param shipmentListApiOp
	 * @return
	 */
	
	public YFCDocument inputXmlForGetShipmentDetails(YFCDocument shipmentListApiOp) {
		YFCDocument docShipmentInp = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		docShipmentInp.getDocumentElement().setAttribute(XMLLiterals.SELLER_ORGANIZATION_CODE, shipmentListApiOp.getDocumentElement().
	    		getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.SELLER_ORGANIZATION_CODE));
		docShipmentInp.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, shipmentListApiOp.getDocumentElement().
	    		getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.SHIPNODE));
		docShipmentInp.getDocumentElement().setAttribute(XMLLiterals.SHIPMENT_KEY, shipmentListApiOp.getDocumentElement().
	    		getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.SHIPMENT_KEY));
		docShipmentInp.getDocumentElement().setAttribute(XMLLiterals.SHIPMENT_NO, shipmentListApiOp.getDocumentElement().
	    		getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.SHIPMENT_NO));
	    return docShipmentInp;
	}
	
	/**
	 * This method forms the template for getShipmentDetails API
	 * @return
	 */
	
	public YFCDocument templateXmlForGetShipmentDetails() {
		YFCDocument docShipmentTemp = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		YFCElement eleShipmentLines = docShipmentTemp.getDocumentElement().createChild(XMLLiterals.SHIPMENT_LINES);
		YFCElement eleShipmentLine = eleShipmentLines.createChild(XMLLiterals.SHIPMENT_LINE);
		eleShipmentLine.setAttribute(XMLLiterals.ACTUAL_QUANTITY, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.QUANTITY, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.SHIPMENT_KEY, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.SHIPMENT_LINE_NO, EMPTY_STRING);
		return docShipmentTemp;
	}
	
	/**
	 * This method invokes getShipmentDetails API
	 * @param shipmentListApiOp
	 * @return
	 */
	
	public YFCDocument getShipmentDetailsAPI(YFCDocument shipmentListApiOp){
	    return invokeYantraApi(XMLLiterals.GET_SHIPMENT_LIST, inputXmlForGetShipmentDetails(shipmentListApiOp), 
	    		templateXmlForGetShipmentDetails());
	 }
	
	 /**
	  * This method fetches the quantity attribute from inXML and getShipmentDetails
	  * output document and compares the quantity and finds the difference.
	  * @param docGetShipmentDetails
	  * @param inXml
	  */
	
	private void quantityDifferenceInInpApiOp(YFCDocument docGetShipmentDetails, YFCDocument inXml) {
		YFCIterable<YFCElement> yfsItrator = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES).getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderLineEle : yfsItrator) {
			String primeLineNo = orderLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO);
			String quantity1 = orderLineEle.getAttribute(XMLLiterals.QTY);
			int inpQuantity = Integer.parseInt(quantity1);
			YFCElement shipmentLineEle = XPathUtil.getXPathElement(docGetShipmentDetails, "/Shipments/Shipment/ShipmentLines/"
					+ "ShipmentLine[@PrimeLineNo=\""+primeLineNo+"\"]");
			String quantity2 = shipmentLineEle.getAttribute(XMLLiterals.ACTUAL_QUANTITY);
			int apiQuantity = (int) Double.parseDouble(quantity2);
			if(apiQuantity > inpQuantity) {
				int quantityDiff = inpQuantity-apiQuantity;
				orderLineEle.setAttribute(XMLLiterals.QTY, apiQuantity);
				adjustQuantityofInventory(docGetShipmentDetails, shipmentLineEle, quantityDiff);
			}
		}
	}
	
	/**
	 * This method forms the adjustInventory API input and increases the inventory
	 * by the difference of two quantities.
	 * @param docGetShipmentDetails
	 * @param shipmentLineEle
	 * @param quantityDiff
	 */
	
	private void adjustQuantityofInventory(YFCDocument docGetShipmentDetails, YFCElement shipmentLineEle, int quantityDiff) {
		YFCDocument docAdjustInv = YFCDocument.createDocument(XMLLiterals.ITEMS);
		YFCElement eleItem = docAdjustInv.getDocumentElement().createChild(XMLLiterals.ITEM);
		eleItem.setAttribute(XMLLiterals.ADJUSTMENT_TYPE, ADJUSTMENT_TYPE);
		eleItem.setAttribute(XMLLiterals.ITEM_ID, shipmentLineEle.getAttribute(XMLLiterals.ITEM_ID));
		eleItem.setAttribute(XMLLiterals.ORGANIZATION_CODE, ORGANIZATION_CODE_VAL);
		eleItem.setAttribute(XMLLiterals.QUANTITY, quantityDiff);
		eleItem.setAttribute(XMLLiterals.SHIPNODE, docGetShipmentDetails.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).
				getAttribute(XMLLiterals.SHIPNODE));
		eleItem.setAttribute(XMLLiterals.SUPPLY_TYPE, SUPPLY_TYPE);
		eleItem.setAttribute(XMLLiterals.UNIT_OF_MEASURE, UOM);
		invokeYantraApi(XMLLiterals.ADJUST_INVENTORY_API, docAdjustInv);
	}
}
