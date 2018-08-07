package com.indigo.om.reservation.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfs.japi.YFSException;

public class IndgReadInventoryFromCache extends AbstractCustomApi {
	/**
	 * Below XML Format will be used to invoke this method <Cart
	 * EnterpriseCode="Indigo_CA" SkipCapacityChecks="true"
	 * SkipCacheOnInventoryLookups="" IgnoreSafetyFactor="" ReserveInventory=""
	 * AllowPartialReservations="" TargetReservationExpiryTime=""
	 * RequestedPickupTime=""> <LineItems> <LineItem Id="" Quantity=""
	 * DeliveryMethod="PICK" NodeId="" AllocatedShiftId="" /> </LineItems>
	 * <ConfigurationOverrides SafetyFactorOverride=""> <Capacity
	 * LookForwardWindow="" ReservationExpiryWindow=""
	 * LegacyOMSProcessingTime="" SAPAcknowledgementTime=""
	 * StoreProcessingTime="" StorePreClosingBufferTime="" />
	 * </ConfigurationOverrides> </Cart>
	 * */

	/*
	 * Output will populate those LineItem element with attribute
	 * CachedInventory="Y" which have inventory in the cache
	 */
	@Override
	public YFCDocument invoke(YFCDocument iDoc2Service) throws YFSException {
		YFCElement iRoot2Service = iDoc2Service.getDocumentElement();
		YFCDocument iDoc2GetAvailabilityCache = createInputForGetAvailabilityCache(iRoot2Service);
		if (iDoc2GetAvailabilityCache != null) {
			Map<String, Map<String, Double>> mpItmQty = createCacheinvMap(iDoc2GetAvailabilityCache);
			if (!mpItmQty.isEmpty()) {
				YFCNodeList<YFCElement> eLineItems = iRoot2Service
						.getElementsByTagName("LineItem");
				if (eLineItems != null && eLineItems.getLength() > 0) {
					Iterator<YFCElement> lineIter = eLineItems.iterator();
					while (lineIter.hasNext()) {
						YFCElement eLineItem = lineIter.next();
						Map<String, Double> mpStoreQty = mpItmQty.get(eLineItem
								.getAttribute("Id"));
						if (mpStoreQty != null) {
							double d = mpStoreQty.get(eLineItem
									.getAttribute("NodeId"));
							if (d >= eLineItem
									.getDoubleAttribute("Quantity", 0)) {
								eLineItem.setAttribute("CachedInventory", "Y");
							}
						}

					}
				}

			}
		}
		// System.out.println(iDoc2Service);
		return iDoc2Service;
	}

	private Map<String, Map<String, Double>> createCacheinvMap(
			YFCDocument iDoc2GetAvailabilityCache) {
		Map<String, Map<String, Double>> mpItmQty = new HashMap<String, Map<String, Double>>();
		// System.out.println("MultiApi-->"+iDoc2GetAvailabilityCache);
		YFCDocument oDoc4mGetAvailabilityCache = invokeYantraApi("multiApi",
				iDoc2GetAvailabilityCache);
		YFCNodeList<YFCElement> oInvItm4mGetInventoryAlertList = oDoc4mGetAvailabilityCache
				.getElementsByTagName("InventoryItem");
		for (YFCElement e : oInvItm4mGetInventoryAlertList) {
			String sItemId = e.getAttribute("ItemID");
			YFCNodeList<YFCElement> oInvAlerts4mGetInventoryAlertList = e
					.getElementsByTagName("InventoryAlerts");
			if (oInvAlerts4mGetInventoryAlertList != null
					&& oInvAlerts4mGetInventoryAlertList.getLength() > 0) {
				Map<String, Double> mpStoreQty = new HashMap<String, Double>();
				for (YFCElement eAlert : oInvAlerts4mGetInventoryAlertList) {
					String sStore = eAlert.getAttribute("Node");
					double dCacheQuantity = eAlert
							.getDoubleAttribute("OnhandAvailableQuantity");
					mpStoreQty.put(sStore, Double.valueOf(dCacheQuantity));
				}
				mpItmQty.put(sItemId, mpStoreQty);

			}

		}
		return mpItmQty;
	}

	private YFCDocument createInputForGetAvailabilityCache(
			YFCElement iRoot2Service) {

		YFCNodeList<YFCElement> eLineItems = iRoot2Service
				.getElementsByTagName("LineItem");
		if (eLineItems.getLength() > 0) {
			Map<String, YFCElement> hmInputByStore = new HashMap<String, YFCElement>();
			YFCElement iRoot2GetAvailabilityCache = YFCDocument.createDocument(
					"MultiApi").getDocumentElement();
			for (YFCElement eLineItem : eLineItems) {
				// String sItemId = eLineItem.getAttribute("Id");
				String sStore = eLineItem.getAttribute("NodeId");
				/*
				 * if(mpItmQty.containsKey(sItemId)){ Map<String,Double>
				 * mpStoreQty = mpItmQty.get(sItemId);
				 * if(mpStoreQty.containsKey(sStore)){ double dQty =
				 * mpStoreQty.get
				 * (sStore).doubleValue()+eLineItem.getDoubleAttribute
				 * ("Quantity",0.0); mpStoreQty.put(sStore,
				 * Double.valueOf(dQty)); }else{ mpStoreQty.put(sStore,
				 * eLineItem.getDoubleAttribute("Quantity",0.0)); } }else{
				 * Map<String,Double> mpStoreQty = new HashMap<String,Double>();
				 * mpStoreQty.put(sStore,
				 * eLineItem.getDoubleAttribute("Quantity",0.0));
				 * mpItmQty.put(sItemId, mpStoreQty); }
				 */
				YFCElement eInvItems = null;
				if (hmInputByStore.containsKey(sStore)) {
					eInvItems = hmInputByStore.get(sStore).getChildElement(
							"InventoryItems");
					extractInvItemFromInput(eLineItem, eInvItems);
				} else {
					YFCElement eAPI = iRoot2GetAvailabilityCache
							.createChild("API");
					eAPI.setAttribute("Name", "getInventoryAlertsList");
					YFCElement eLine = eAPI.createChild("Input").createChild(
							"InventoryAlerts");
					eLine.setAttribute("OrganizationCode", iRoot2Service
							.getAttribute("EnterpriseCode", "Indigo_CA"));
					eLine.setAttribute("InventoryOrganizationCode",
							iRoot2Service.getAttribute("EnterpriseCode",
									"Indigo_CA"));
					eLine.setAttribute("Node", sStore);
					eLine.setAttribute("AlertType", "REALTIME_ONHAND");
					eInvItems = eLine.createChild("InventoryItems");
					extractInvItemFromInput(eLineItem, eInvItems);
					hmInputByStore.put(sStore, eLine);
				}

			}
			return iRoot2GetAvailabilityCache.getOwnerDocument();
		}

		return null;
	}

	// Map<String,Double> hmItemQuantity = new HashMap<String,Double>();
	private void extractInvItemFromInput(YFCElement eLineItem,
			YFCElement eInvItems) {
		YFCElement eInvItem = eInvItems.createChild("InventoryItem");
		eInvItem.setAttribute("ProductClass", "");
		eInvItem.setAttribute("UnitOfMeasure", "EACH");
		String sItemId = eLineItem.getAttribute("Id");
		eInvItem.setAttribute("ItemID", sItemId);
		/*
		 * double dQuantity = eLineItem.getDoubleAttribute("Quantity",0.0);
		 * hmItemQuantity.put(sItemId, Double.valueOf(dQuantity));
		 */
	}

	/**
	 * @param args
	 */
	/*public static void main(String[] args) {

		YFSContext oEnv = null;
		try {
			oEnv = new YCPContext("admin", "password");
			oEnv.setUserTokenIgnored(true);
			oEnv.setSystemCall(true);
			YFSInitializer.initialize();
			ServiceInvoker invoker = ServiceInvokerManager.getInstance()
					.getServiceInvoker();
			invoker.setYFSEnvironment(oEnv);
			String inrules1 = "<Rules "
					+ "Action='Modify' OrganizationCode='Matrix' "
					+ " RuleSetFieldName='COMPUTE_AVAILABILITY_FOR_RTAM' RuleSetValue='Y'/>";
			YFCDocument docrules = YFCDocument.getDocumentFor(inrules1);
			invoker.invokeYantraApi("manageRule", docrules);

			String inrules2 = "<Rules "
					+ " OrganizationCode='Matrix' "
					+ " RuleSetFieldName='DEFAULT_NODE_LEVEL_INV_MON_RULE' RuleSetValue='Matrix_RTAM'/>";
			YFCDocument docrules2 = YFCDocument.getDocumentFor(inrules2);
			invoker.invokeYantraApi("manageRule", docrules2);

			String inrules3 = "<Rules "
					+ " OrganizationCode='Matrix' "
					+ " RuleSetFieldName='DEFAULT_INV_MON_RULE' RuleSetValue='Matrix_RTAM'/>";
			YFCDocument docrules3 = YFCDocument.getDocumentFor(inrules3);
			invoker.invokeYantraApi("manageRule", docrules3);

			String in = "<InventoryMonitorRules DisableFlag='N' InventoryMonitorRule='Matrix_RTAM' "
					+ "InventoryMonitorRuleName='Matrix node level monitor Rule' "
					+ "InventoryMonitorRuleType='EVENT' LeadTimeLevel1Qty='100' LeadTimeLevel2Qty='10' "
					+ "LeadTimeLevel3Qty='0' "
					+ "MaxMonitorDays='30' OrganizationCode='Matrix'" + "/>";
			YFCDocument doc = YFCDocument.getDocumentFor(in);
			invoker.invokeYantraApi("manageInventoryMonitorRule", doc);

			String in1 = "<MonitorItemAvailability RaiseEventOnAllAvailabilityChanges='Y' ProductClass='' ItemID='100001' "
					+ "OrganizationCode='Matrix' UnitOfMeasure='EACH'/>";
			YFCDocument doc1 = YFCDocument.getDocumentFor(in1);
			 System.out.println("1-->\n"+invoker.invokeYantraApi("monitorItemAvailability",doc1));

			String in3 = "<Items><Item AccountNo='' AdjustmentType='ADJUSTMENT'  ItemID='100001' OrganizationCode='Matrix' ProductClass='' "
					+ "Quantity='10' ShipNode='Mtrx_Store_1' SupplyType='ONHAND' UnitOfMeasure='EACH' />"
					+ "<Item AccountNo='' AdjustmentType='ADJUSTMENT'  ItemID='100002' OrganizationCode='Matrix' ProductClass='' "
					+ "Quantity='15' ShipNode='Mtrx_Store_2' SupplyType='ONHAND' UnitOfMeasure='EACH' /></Items>";
			YFCDocument doc3 = YFCDocument.getDocumentFor(in3);
			invoker.invokeYantraApi("adjustInventory", doc3);

			String in4 = "<MonitorItemAvailability ProductClass='' RaiseEventOnAllAvailabilityChanges='Y' ItemID='100001' "
					+ "OrganizationCode='Matrix' UnitOfMeasure='EACH'/>";

			YFCDocument doc4 = YFCDocument.getDocumentFor(in4);
			System.out.println("2-->\n"+invoker.invokeYantraApi("monitorItemAvailability",doc4));

			String in2 = "<InventoryAlerts OrganizationCode='Matrix' "
					+ "InventoryOrganizationCode='Matrix'/>";
			YFCDocument doc2 = YFCDocument.getDocumentFor(in2);
			YFCDocument outdoc = invoker.invokeYantraApi(
					"getInventoryAlertsList", doc2);

			System.out.println(outdoc);

			String sInput = "<Cart EnterpriseCode='Matrix' >"
					+ "<LineItems> <LineItem Id='100001' Quantity='2'"
					+ " NodeId='Mtrx_Store_1' AllocatedShiftId='' /><LineItem Id='100002' Quantity='2'"
					+ " NodeId='Mtrx_Store_2' AllocatedShiftId='' />"
					+ " </LineItems></Cart>";
			YFCDocument doc5 = YFCDocument.getDocumentFor(sInput);
			invoker.invokeYantraService("INDG_ReadCacheInventory", doc5);
			oEnv.commit();
			invoker.releaseYFSEnvironment();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

	}*/

}
