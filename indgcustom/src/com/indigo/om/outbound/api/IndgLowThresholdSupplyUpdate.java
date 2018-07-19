package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class IndgLowThresholdSupplyUpdate extends AbstractCustomApi {

	private static final String NEGATIVE = "-";
	private static final String ALERT_TYPE = "REALTIME_FUTURE_MAX";
	private static final String ALERT_LEVEL = "3";
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCElement eleSupply = inXml.getDocumentElement().getChildElement(XMLLiterals.SUPPLY);
		String supplyType = eleSupply.getAttribute(XMLLiterals.SUPPLY_TYPE);
		String quantity = eleSupply.getAttribute(XMLLiterals.QUANTITY);
		if((supplyType.equals(XMLLiterals.ON_HAND)) && (quantity.contains(NEGATIVE))) {
			System.out.println(supplyType + "bbbbbbbbbb" + quantity);
			checkInventoryAlertList(eleSupply);
		}
		return inXml;
	}
	
	private void checkInventoryAlertList(YFCElement eleSupply) {
		YFCDocument docGetInventoryAlertsListApiOp = getInventoryAlertListApi(eleSupply);
		System.out.println(docGetInventoryAlertsListApiOp + "aaaaaaaaaa");
		if(!YFCObject.isVoid(docGetInventoryAlertsListApiOp)) {
				callMonitorItemAvailability(eleSupply);
		}
	}
	
	public YFCDocument getInventoryAlertListApiInp(YFCElement eleSupply) {
		YFCDocument docInputApi = YFCDocument.createDocument(XMLLiterals.INVENTORY_ALERTS);
		docInputApi.getDocumentElement().setAttribute(XMLLiterals.NODE, eleSupply.getAttribute(XMLLiterals.SHIPNODE));
		docInputApi.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, eleSupply.getAttribute(XMLLiterals.INVENTORY_ORGANIZATION_CODE));
		docInputApi.getDocumentElement().setAttribute(XMLLiterals.ALERT_LEVEL, ALERT_LEVEL);
		docInputApi.getDocumentElement().setAttribute(XMLLiterals.ALERT_TYPE, ALERT_TYPE);
		YFCElement inventoryItems = docInputApi.getDocumentElement().createChild(XMLLiterals.INVENTORY_ITEMS);
		YFCElement inventoryItem = inventoryItems.createChild(XMLLiterals.INVENTORY_ITEM);
		inventoryItem.setAttribute(XMLLiterals.ITEM_ID, eleSupply.getAttribute(XMLLiterals.ITEM_ID));
		inventoryItem.setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleSupply.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
		System.out.println(docInputApi + "cccccccccc");
	    return docInputApi;
	}
	
	public YFCDocument getInventoryAlertListApi(YFCElement eleSupply){
		return  invokeYantraApi(XMLLiterals.GET_INVENTORY_ALERTS_LIST, getInventoryAlertListApiInp(eleSupply));
	}
	
	private void callMonitorItemAvailability(YFCElement eleSupply) {
		YFCDocument docMonitorItemAvailability = YFCDocument.createDocument(XMLLiterals.MONITOR_ITEM_AVAILABILITY);
		docMonitorItemAvailability.getDocumentElement().setAttribute(XMLLiterals.ITEM_ID, eleSupply.getAttribute(XMLLiterals.ITEM_ID));
		docMonitorItemAvailability.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, eleSupply.getAttribute(XMLLiterals.INVENTORY_ORGANIZATION_CODE));
		docMonitorItemAvailability.getDocumentElement().setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleSupply.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
		System.out.println(docMonitorItemAvailability + "dddddddddd");
		invokeYantraApi(XMLLiterals.MONITOR_ITEM_AVAILABILITY_API, docMonitorItemAvailability);
	}
}
