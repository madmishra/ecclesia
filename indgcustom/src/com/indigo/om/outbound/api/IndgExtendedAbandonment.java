package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;


public class IndgExtendedAbandonment extends AbstractCustomApi {
	
	private static final String EMPTY_STRING = "";

	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCDocument docGetShipmentListOp = getShipmentListAPI(inXml);
		System.out.println("asdfg"+ docGetShipmentListOp);
		return inXml;
	}
	
	public YFCDocument docgetShipmentListInp(YFCDocument inXml) {
		YFCElement orderEle = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
		YFCDocument eleShipment = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		eleShipment.getDocumentElement().setAttribute(XMLLiterals.CUSTOMER_PO_NO, orderEle.getAttribute(XMLLiterals.LEGACY_OMS_ORDER_NO));
		eleShipment.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, orderEle.getAttribute(XMLLiterals.ENTERPRISE_CODE));
		eleShipment.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, orderEle.getAttribute(XMLLiterals.DOCUMENT_TYPE));
		YFCElement eleShipmentLine = eleShipment.createElement(XMLLiterals.SHIPMENT_LINES).createChild(XMLLiterals.SHIPMENT_LINE);
		eleShipmentLine.setAttribute(XMLLiterals.ORDER_NO, orderEle.getAttribute(XMLLiterals.PARENT_LEGACY_OMS_ORDER_NO));
		System.out.println("qwert"+ eleShipment);
		return 	eleShipment;
	}
	
	public YFCDocument docgetShipmentListTemp() {
		YFCDocument docgetShipmentListTemp = YFCDocument.createDocument(XMLLiterals.SHIPMENTS);
		YFCElement eleShipment = docgetShipmentListTemp.getDocumentElement().createChild(XMLLiterals.SHIPMENT);
		eleShipment.setAttribute(XMLLiterals.SHIPMENT_KEY, EMPTY_STRING);
		eleShipment.setAttribute(XMLLiterals.SHIPMENT_NO, EMPTY_STRING);
		eleShipment.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		eleShipment.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
		eleShipment.setAttribute(XMLLiterals.ORDER_TYPE, EMPTY_STRING);
		eleShipment.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
		YFCElement eleShipmentLine = eleShipment.getChildElement(XMLLiterals.SHIPMENT_LINES).createChild(XMLLiterals.SHIPMENT_LINE);
		eleShipmentLine.setAttribute(XMLLiterals.CUSTOMER_PO_NO, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		System.out.println("zxcvbn"+ docgetShipmentListTemp);
		return docgetShipmentListTemp;
	}
	
	private YFCDocument getShipmentListAPI(YFCDocument inXml) {
		return invokeYantraApi(XMLLiterals.GET_SHIPMENT_LIST, docgetShipmentListInp(inXml), docgetShipmentListTemp());
	}
}
