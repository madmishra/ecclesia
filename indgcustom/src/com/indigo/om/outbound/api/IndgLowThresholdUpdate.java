package com.indigo.om.outbound.api;

import java.util.Iterator;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * @author BSG168
 *
 * Custom API to manage low threshold of items for both Supply and
 * Delay scenarios. When the low threshold is hit, getInventoryAlertList API
 * is called to check if any alerts has been raised or not, then
 * monitorItemAvailability API is called which raises the availableChangeList 
 * event and message is dropped in the queue.
 */

public class IndgLowThresholdUpdate extends AbstractCustomApi {

	private static final String NEGATIVE = "-";
	private static final String NO = "N";
	private static final String YES = "Y";
	private static final String GROUP_DESCRIPTION = "GROUP_DESCRIPTION";
	private static final String GROUP_CODE ="PROD";
	private static final String END_DATE = "2500-01-01 00:00:00.0";
	private static final String START_DATE = "1900-01-01 00:00:00.0";
	private static final String ALL = "ALL";
	private static final String PRIORITY = "1.00";
	private static final String SOURCING = "SOURCING";
	private static final String ORGANIZATION_CODE = "Indigo_CA";
	private static final String LOW_QUANTITY = "LOW_QUANTITY";
	private static final String ASSUME_LOW_INVENTORY = "ASSUME_LOW_INVENTORY";
	private static final String EMPTY_STRING = "";
	
	/**
	 * This method is the invoke point of the service.
	 * 
	 */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCElement eleRoot = inXml.getDocumentElement();
		String supplyType = eleRoot.getAttribute(XMLLiterals.SUPPLY_TYPE);
		String demandType = eleRoot.getAttribute(XMLLiterals.DEMAND_TYPE);
		String quantity = eleRoot.getAttribute(XMLLiterals.QUANTITY);
		if((!YFCObject.isVoid(supplyType)) && (supplyType.equals(XMLLiterals.ON_HAND)) && (quantity.contains(NEGATIVE)) || 
				((!YFCObject.isVoid(demandType)) && (demandType.equals(XMLLiterals.OPEN_ORDER)))) {
			manageDistributionRuleForNode(eleRoot);
			checkInventoryAlertList(eleRoot, quantity);
			deleteDistributionForNode(eleRoot);
			
		}
		return inXml;
	}
	
	/**
	 * This method calls getInventoryAlertList API and sorts the event in
	 * descending order (if alerts exists) or it calls getMonitorAvailability API
	 * if assumeLowQuantity property is enabled.
	 * @param eleRoot
	 * @param inpQuantity
	 */
	
	private void checkInventoryAlertList(YFCElement eleRoot, String inpQuantity) {
		YFCDocument docGetInvAlertsListApiOp = getInventoryAlertListApi(eleRoot);
		if(!YFCObject.isVoid(docGetInvAlertsListApiOp) && (docGetInvAlertsListApiOp.getDocumentElement().hasChildNodes())) {
			sortAlertsByDescOrder(docGetInvAlertsListApiOp);
			String availableQty = docGetInvAlertsListApiOp.getDocumentElement().getChildElement(XMLLiterals.INVENTORY_ITEM).
					getChildElement(XMLLiterals.INVENTORY_ALERTS_LIST).getChildElement(XMLLiterals.INVENTORY_ALERTS).getChildElement(XMLLiterals.AVAILABILITY_INFORMATION).
					getChildElement(XMLLiterals.AVAILABLE_INVENTORY).getAttribute(XMLLiterals.AVAILABLE_QUANTITY);
			if(!YFCObject.isVoid(availableQty)) {
				int eventQty = (int) Double.parseDouble(inpQuantity);
				int apiQty = (int) Double.parseDouble(availableQty);
				int diff = eventQty - apiQty;
				String lowQuantity = getProperty(LOW_QUANTITY);
				int lowQtyInp = (int) Double.parseDouble(lowQuantity);
				if(diff < lowQtyInp) {
					callMonitorItemAvailability(eleRoot);
				}
			}
		}
		else {
			String assumeLowInventory = getProperty(ASSUME_LOW_INVENTORY);
			if(assumeLowInventory.equals(YES)) {
				callMonitorItemAvailability(eleRoot);
			}
		}
	}
	
	/**
	 * This method forms the input for manageDistributionRule API and
	 * calls the API.
	 * @param eleRoot
	 */
	
	private void manageDistributionRuleForNode(YFCElement eleRoot) {
		YFCDocument docManageDistributionRule = YFCDocument.createDocument(XMLLiterals.DISTRIBUTION_RULE);
		docManageDistributionRule.getDocumentElement().setAttribute(XMLLiterals.DEFAULT_FLAG, NO);
		docManageDistributionRule.getDocumentElement().setAttribute(XMLLiterals.DESCRIPTION, getProperty(GROUP_DESCRIPTION));
		docManageDistributionRule.getDocumentElement().setAttribute(XMLLiterals.DISTRIBUTION_RULE_ID, getProperty(GROUP_DESCRIPTION));
		docManageDistributionRule.getDocumentElement().setAttribute(XMLLiterals.ITEM_GROUP_CODE, GROUP_CODE);
		docManageDistributionRule.getDocumentElement().setAttribute(XMLLiterals.OWNERKEY, ORGANIZATION_CODE);
		docManageDistributionRule.getDocumentElement().setAttribute(XMLLiterals.PURPOSE, SOURCING);
		YFCElement eleItemShipNodes = docManageDistributionRule.getDocumentElement().createChild(XMLLiterals.ITEM_SHIP_NODES);
		YFCElement eleItemShipNode = eleItemShipNodes.createChild(XMLLiterals.ITEM_SHIP_NODE);
		eleItemShipNode.setAttribute(XMLLiterals.ACTIVE_FLAG, YES);
		eleItemShipNode.setAttribute(XMLLiterals.EFFECTIVE_END_DATE, END_DATE);
		eleItemShipNode.setAttribute(XMLLiterals.EFFECTIVE_START_DATE, START_DATE);
		eleItemShipNode.setAttribute(XMLLiterals.ITEMID, ALL);
		eleItemShipNode.setAttribute(XMLLiterals.ITEM_TYPE, ALL);
		eleItemShipNode.setAttribute(XMLLiterals.PRIORITY, PRIORITY);
		eleItemShipNode.setAttribute(XMLLiterals.SHIPNODE_KEY, eleRoot.getAttribute(XMLLiterals.SHIPNODE));
		invokeYantraApi(XMLLiterals.MANAGE_DISTRIBUTION_RULE, docManageDistributionRule);
	}
	
	/**
	 * This method forms the input document for getInventoryAlertList API
	 * @param eleRoot
	 * @return
	 */
	
	public YFCDocument getInventoryAlertListApiInp(YFCElement eleRoot) {
		YFCDocument docInputApi = YFCDocument.createDocument(XMLLiterals.INVENTORY_ALERTS);
		docInputApi.getDocumentElement().setAttribute(XMLLiterals.NODE, eleRoot.getAttribute(XMLLiterals.SHIPNODE));
		docInputApi.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, ORGANIZATION_CODE);
		YFCElement inventoryItems = docInputApi.getDocumentElement().createChild(XMLLiterals.INVENTORY_ITEMS);
		YFCElement inventoryItem = inventoryItems.createChild(XMLLiterals.INVENTORY_ITEM);
		inventoryItem.setAttribute(XMLLiterals.ITEM_ID, eleRoot.getAttribute(XMLLiterals.ITEM_ID));
		inventoryItem.setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleRoot.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
	    return docInputApi;
	}
	
	/**
	 * This method forms the output template for getInventoryAlertList API.
	 * @return
	 */
	
	public YFCDocument getInventoryAlertListApiTemplate() {
		YFCDocument docTemplateApi = YFCDocument.createDocument(XMLLiterals.INVENTORY_ITEM_LIST);
		docTemplateApi.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, EMPTY_STRING);
		YFCElement eleInventoryItem = docTemplateApi.getDocumentElement().createChild(XMLLiterals.INVENTORY_ITEM);
		eleInventoryItem.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
		YFCElement eleInventoryAlertLists = eleInventoryItem.createChild(XMLLiterals.INVENTORY_ALERTS_LIST);
		YFCElement eleInventoryAlerts = eleInventoryAlertLists.createChild(XMLLiterals.INVENTORY_ALERTS);
		eleInventoryAlerts.setAttribute(XMLLiterals.ALERT_LEVEL, EMPTY_STRING);
		eleInventoryAlerts.setAttribute(XMLLiterals.ALERT_TYPE, EMPTY_STRING);
		eleInventoryAlerts.setAttribute(XMLLiterals.ALERT_QUANTITY, EMPTY_STRING);
		eleInventoryAlerts.setAttribute(XMLLiterals.ALERT_RAISED_ON, EMPTY_STRING);
		eleInventoryAlerts.setAttribute(XMLLiterals.NODE, EMPTY_STRING);
		YFCElement eleAvailabilityInfo = eleInventoryAlerts.createChild(XMLLiterals.AVAILABILITY_INFORMATION);
		YFCElement eleAvailableInventory = eleAvailabilityInfo.createChild(XMLLiterals.AVAILABLE_INVENTORY);
		eleAvailableInventory.setAttribute(XMLLiterals.AVAILABLE_QUANTITY, EMPTY_STRING);
		eleAvailableInventory.setAttribute(XMLLiterals.IS_ONHAND, EMPTY_STRING);
	    return docTemplateApi;
	}
	
	/**
	 * This method invokes the getInventoryAlertList API.
	 * @param eleRoot
	 * @return
	 */
	
	public YFCDocument getInventoryAlertListApi(YFCElement eleRoot){
		return  invokeYantraApi(XMLLiterals.GET_INVENTORY_ALERTS_LIST, getInventoryAlertListApiInp(eleRoot), getInventoryAlertListApiTemplate());
	}
	
	/**
	 * This method sorts the alerts in descending order by AlertRaisedOn attribute
	 * value.
	 * @param docGetInventoryAlertsListApiOp
	 */
	
	private void sortAlertsByDescOrder(YFCDocument docGetInventoryAlertsListApiOp) {
		YFCElement inventoryItemList = docGetInventoryAlertsListApiOp.getDocumentElement();
		for(Iterator<YFCElement> itr = inventoryItemList.getChildren().iterator();itr.hasNext();) {
			YFCElement inventoryItem = itr.next();
			YFCElement inventoryAlertLists = inventoryItem.getChildElement(XMLLiterals.INVENTORY_ALERTS_LIST,true);
			YFCElement inventoryAlert = inventoryAlertLists.getChildElement(XMLLiterals.INVENTORY_ALERTS);
			if(inventoryAlert.hasAttribute(XMLLiterals.ALERT_RAISED_ON)) {
				inventoryItem.setAttribute(XMLLiterals.ALERT_RAISED_ON, inventoryAlert.getAttribute(XMLLiterals.ALERT_RAISED_ON));
			}
		}
		String [] attrNames = new String[]{XMLLiterals.ALERT_RAISED_ON};
		inventoryItemList.sortChildren(attrNames, false);
	}
	
	/**
	 * This method invokes the monitorItemAvailability API.
	 * @param eleRoot
	 */
	
	private void callMonitorItemAvailability(YFCElement eleRoot) {
		YFCDocument docMonitorItemAvailability = YFCDocument.createDocument(XMLLiterals.MONITOR_ITEM_AVAILABILITY);
		docMonitorItemAvailability.getDocumentElement().setAttribute(XMLLiterals.ITEM_ID, eleRoot.getAttribute(XMLLiterals.ITEM_ID));
		docMonitorItemAvailability.getDocumentElement().setAttribute(XMLLiterals.DISTRIBUTION_RULE_ID, getProperty(GROUP_DESCRIPTION));
		docMonitorItemAvailability.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, ORGANIZATION_CODE);
		docMonitorItemAvailability.getDocumentElement().setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleRoot.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
		invokeYantraApi(XMLLiterals.MONITOR_ITEM_AVAILABILITY_API, docMonitorItemAvailability);
	}
	
	/**
	 * This method invokes the deleteDistribution API for the shipNode
	 * to remove from the distribution group.
	 * @param eleRoot
	 */
	
	private void deleteDistributionForNode(YFCElement eleRoot) {
		YFCDocument docDeleteDistribution = YFCDocument.createDocument(XMLLiterals.ITEM_SHIP_NODE);
		docDeleteDistribution.getDocumentElement().setAttribute(XMLLiterals.DISTRIBUTION_RULE_ID, getProperty(GROUP_DESCRIPTION));
		docDeleteDistribution.getDocumentElement().setAttribute(XMLLiterals.ITEMID, ALL);
		docDeleteDistribution.getDocumentElement().setAttribute(XMLLiterals.OWNERKEY, ORGANIZATION_CODE);
		docDeleteDistribution.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE_KEY, eleRoot.getAttribute(XMLLiterals.SHIPNODE));
		invokeYantraApi(XMLLiterals.DELETE_DISTRIBUTION, docDeleteDistribution);
	}
}
