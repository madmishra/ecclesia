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
	private static final String NO = "N";
	private static final String YES = "Y";
	private static final String GROUP_DESCRIPTION = "GROUP_DESCRIPTION";
	private static final String GROUP_CODE ="PROD";
	private static final String END_DATE = "1900-01-01 00:00:00.0";
	private static final String START_DATE = "2500-01-01 00:00:00.0";
	private static final String ALL = "ALL";
	private static final String PRIORITY = "1.00";
	private static final String SOURCING = "SOURCING";
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		System.out.println(inXml + "aaaaaaaa");
		YFCElement eleRoot = inXml.getDocumentElement();
		String supplyType = eleRoot.getAttribute(XMLLiterals.SUPPLY_TYPE);
		String demandType = eleRoot.getAttribute(XMLLiterals.DEMAND_TYPE);
		String quantity = eleRoot.getAttribute(XMLLiterals.QUANTITY);
		if((!YFCObject.isVoid(supplyType)) && (supplyType.equals(XMLLiterals.ON_HAND)) && (quantity.contains(NEGATIVE)) || 
				((!YFCObject.isVoid(demandType)) && (demandType.equals(XMLLiterals.OPEN_ORDER)))) {
			System.out.println(eleRoot + "bbbbbbbbb");
			checkInventoryAlertList(eleRoot);
		}
		return inXml;
	}
	
	private void checkInventoryAlertList(YFCElement eleRoot) {
		manageDistributionRuleForNode(eleRoot);
		YFCDocument docGetInventoryAlertsListApiOp = getInventoryAlertListApi(eleRoot);
		System.out.println(docGetInventoryAlertsListApiOp + "eeeeeeeee");
		if(!YFCObject.isVoid(docGetInventoryAlertsListApiOp)) {
				callMonitorItemAvailability(eleRoot);
		}
		deleteDistributionForNode(eleRoot);
	}
	
	private void manageDistributionRuleForNode(YFCElement eleRoot) {
		YFCDocument docManageDistributionRule = YFCDocument.createDocument(XMLLiterals.DISTRIBUTION_RULE);
		docManageDistributionRule.getDocumentElement().setAttribute(XMLLiterals.DEFAULT_FLAG, NO);
		docManageDistributionRule.getDocumentElement().setAttribute(XMLLiterals.DESCRIPTION, getProperty(GROUP_DESCRIPTION));
		docManageDistributionRule.getDocumentElement().setAttribute(XMLLiterals.DISTRIBUTION_RULE_ID, getProperty(GROUP_DESCRIPTION));
		docManageDistributionRule.getDocumentElement().setAttribute(XMLLiterals.ITEM_GROUP_CODE, GROUP_CODE);
		docManageDistributionRule.getDocumentElement().setAttribute(XMLLiterals.OWNERKEY, eleRoot.getAttribute(XMLLiterals.INVENTORY_ORGANIZATION_CODE));
		docManageDistributionRule.getDocumentElement().setAttribute(XMLLiterals.PURPOSE, SOURCING);
		YFCElement eleItemShipNodes = docManageDistributionRule.getDocumentElement().createChild(XMLLiterals.ITEM_SHIP_NODES);
		YFCElement eleItemShipNode = eleItemShipNodes.createChild(XMLLiterals.ITEM_SHIP_NODE);
		eleItemShipNode.setAttribute(XMLLiterals.ACTIVE_FLAG, YES);
		eleItemShipNode.setAttribute(XMLLiterals.EFFECTIVE_END_DATE, END_DATE);
		eleItemShipNode.setAttribute(XMLLiterals.EFFECTIVE_START_DATE, START_DATE);
		eleItemShipNode.setAttribute(XMLLiterals.ITEMID, ALL);
		eleItemShipNode.setAttribute(XMLLiterals.ITEM_TYPE, ALL);
		eleItemShipNode.setAttribute(XMLLiterals.ITEM_SHIPNODE_KEY, eleRoot.getAttribute(XMLLiterals.SHIPNODE));
		eleItemShipNode.setAttribute(XMLLiterals.PRIORITY, PRIORITY);
		eleItemShipNode.setAttribute(XMLLiterals.SHIPNODE_KEY, eleRoot.getAttribute(XMLLiterals.SHIPNODE));
		System.out.println(docManageDistributionRule + "ccccccccc");
		invokeYantraApi(XMLLiterals.MANAGE_DISTRIBUTION_RULE, docManageDistributionRule);
	}
	
	public YFCDocument getInventoryAlertListApiInp(YFCElement eleRoot) {
		YFCDocument docInputApi = YFCDocument.createDocument(XMLLiterals.INVENTORY_ALERTS);
		docInputApi.getDocumentElement().setAttribute(XMLLiterals.NODE, eleRoot.getAttribute(XMLLiterals.SHIPNODE));
		docInputApi.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, eleRoot.getAttribute(XMLLiterals.INVENTORY_ORGANIZATION_CODE));
		docInputApi.getDocumentElement().setAttribute(XMLLiterals.ALERT_LEVEL, ALERT_LEVEL);
		docInputApi.getDocumentElement().setAttribute(XMLLiterals.ALERT_TYPE, ALERT_TYPE);
		YFCElement inventoryItems = docInputApi.getDocumentElement().createChild(XMLLiterals.INVENTORY_ITEMS);
		YFCElement inventoryItem = inventoryItems.createChild(XMLLiterals.INVENTORY_ITEM);
		inventoryItem.setAttribute(XMLLiterals.ITEM_ID, eleRoot.getAttribute(XMLLiterals.ITEM_ID));
		inventoryItem.setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleRoot.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
		System.out.println(docInputApi + "ddddddddd");
	    return docInputApi;
	}
	
	public YFCDocument getInventoryAlertListApi(YFCElement eleRoot){
		return  invokeYantraApi(XMLLiterals.GET_INVENTORY_ALERTS_LIST, getInventoryAlertListApiInp(eleRoot));
	}
	
	private void callMonitorItemAvailability(YFCElement eleRoot) {
		YFCDocument docMonitorItemAvailability = YFCDocument.createDocument(XMLLiterals.MONITOR_ITEM_AVAILABILITY);
		docMonitorItemAvailability.getDocumentElement().setAttribute(XMLLiterals.ITEM_ID, eleRoot.getAttribute(XMLLiterals.ITEM_ID));
		docMonitorItemAvailability.getDocumentElement().setAttribute(XMLLiterals.DISTRIBUTION_RULE_ID, getProperty(GROUP_DESCRIPTION));
		docMonitorItemAvailability.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, eleRoot.getAttribute(XMLLiterals.INVENTORY_ORGANIZATION_CODE));
		docMonitorItemAvailability.getDocumentElement().setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleRoot.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
		System.out.println(docMonitorItemAvailability + "fffffffffff");
		invokeYantraApi(XMLLiterals.MONITOR_ITEM_AVAILABILITY_API, docMonitorItemAvailability);
	}
	
	private void deleteDistributionForNode(YFCElement eleRoot) {
		YFCDocument docDeleteDistribution = YFCDocument.getDocumentFor(XMLLiterals.ITEM_SHIP_NODE);
		docDeleteDistribution.getDocumentElement().setAttribute(XMLLiterals.DISTRIBUTION_RULE_ID, getProperty(GROUP_DESCRIPTION));
		docDeleteDistribution.getDocumentElement().setAttribute(XMLLiterals.ITEMID, ALL);
		docDeleteDistribution.getDocumentElement().setAttribute(XMLLiterals.ITEM_SHIPNODE_KEY, eleRoot.getAttribute(XMLLiterals.SHIPNODE));
		docDeleteDistribution.getDocumentElement().setAttribute(XMLLiterals.OWNERKEY, eleRoot.getAttribute(XMLLiterals.INVENTORY_ORGANIZATION_CODE));
		docDeleteDistribution.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE_KEY, eleRoot.getAttribute(XMLLiterals.SHIPNODE));
		System.out.println(docDeleteDistribution + "gggggggggg");
		invokeYantraApi(XMLLiterals.DELETE_DISTRIBUTION, docDeleteDistribution);
	}
}
