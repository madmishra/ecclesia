package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class IndgAbandonmentTimeSAP062 extends AbstractCustomApi {

	private static final String EMPTY_STRING = "";
	private static final String ADJUSTMENT_TYPE = "ADJUSTMENT";
	private static final String SUPPLY_TYPE = "ONHAND";
	private static final String UOM = "EACH";
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		System.out.println(inXml + "aaaaaaaaaa");
		YFCDocument shipmentListApiOp = getShipmentList(inXml);
		System.out.println(shipmentListApiOp + "bbbbbbbbbb");
		YFCDocument docGetShipmentDetails = getShipmentDetailsAPI(shipmentListApiOp);
		System.out.println(docGetShipmentDetails + "ccccccccccc");
		quantityDifferenceInInpApiOp(docGetShipmentDetails, inXml);
		return null;
	}
	
	public YFCDocument inputXmlForGetShipmentList(String orderNo) {
	    YFCDocument getShipmentListDoc = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
	    YFCElement shipmentLinesEle = getShipmentListDoc.getDocumentElement().createChild(XMLLiterals.SHIPMENT_LINES);
	    YFCElement shipmentLineEle = shipmentLinesEle.createChild(XMLLiterals.SHIPMENT_LINE);
	    shipmentLineEle.setAttribute(XMLLiterals.ORDER_NO, orderNo);
	    return getShipmentListDoc;
	  }
	
	public YFCDocument inputTemplateForGetShipmentList() {
	    YFCDocument getShipmentListTemp = YFCDocument.createDocument(XMLLiterals.SHIPMENTS);
	    YFCElement shipmentEle = getShipmentListTemp.getDocumentElement().createChild(XMLLiterals.SHIPMENT);
	    shipmentEle.setAttribute(XMLLiterals.SHIPMENT_KEY, EMPTY_STRING);
	    shipmentEle.setAttribute(XMLLiterals.SHIPMENT_NO, EMPTY_STRING);
	    shipmentEle.setAttribute(XMLLiterals.SELLER_ORGANIZATION_CODE, EMPTY_STRING);
	    shipmentEle.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
	    return getShipmentListTemp;
	  }
	
	public YFCDocument getShipmentList(YFCDocument inXml){
		String orderNo = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).
				getAttribute(XMLLiterals.STERLING_ORDER_NO);
	    return invokeYantraApi(XMLLiterals.GET_SHIPMENT_LIST, inputXmlForGetShipmentList(orderNo), inputTemplateForGetShipmentList());
	 }
	
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
	
	public YFCDocument getShipmentDetailsAPI(YFCDocument shipmentListApiOp){
	    return invokeYantraApi(XMLLiterals.GET_SHIPMENT_LIST, inputXmlForGetShipmentDetails(shipmentListApiOp), 
	    		templateXmlForGetShipmentDetails());
	 }

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
				int quantityDiff = apiQuantity-inpQuantity;
				adjustQuantityofInventory(docGetShipmentDetails, shipmentLineEle, quantityDiff);
			}
		}
	}
	
	private void adjustQuantityofInventory(YFCDocument docGetShipmentDetails, YFCElement shipmentLineEle, int quantityDiff) {
		YFCDocument docAdjustInv = YFCDocument.createDocument(XMLLiterals.ITEMS);
		YFCElement eleItem = docAdjustInv.getDocumentElement().createChild(XMLLiterals.ITEM);
		eleItem.setAttribute(XMLLiterals.ADJUSTMENT_TYPE, ADJUSTMENT_TYPE);
		eleItem.setAttribute(XMLLiterals.ITEM_ID, shipmentLineEle.getAttribute(XMLLiterals.ITEM_ID));
		eleItem.setAttribute(XMLLiterals.ORGANIZATION_CODE, docGetShipmentDetails.getDocumentElement().
				getAttribute(XMLLiterals.SELLER_ORGANIZATION_CODE));
		eleItem.setAttribute(XMLLiterals.QUANTITY, quantityDiff);
		eleItem.setAttribute(XMLLiterals.SHIPNODE, docGetShipmentDetails.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).
				getAttribute(XMLLiterals.SHIPNODE));
		eleItem.setAttribute(XMLLiterals.SUPPLY_TYPE, SUPPLY_TYPE);
		eleItem.setAttribute(XMLLiterals.UNIT_OF_MEASURE, UOM);
		System.out.println(docAdjustInv + "zzzzzzzzzzzz");
		invokeYantraApi(XMLLiterals.ADJUST_INVENTORY_API, docAdjustInv);
	}
}
