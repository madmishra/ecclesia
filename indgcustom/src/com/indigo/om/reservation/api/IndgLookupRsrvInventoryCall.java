package com.indigo.om.reservation.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.bridge.sterling.consts.ValueConstants;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSException;

public class IndgLookupRsrvInventoryCall extends AbstractCustomApi {

	YFCDocument docRsrvInvInXml = null;
	YFCDocument docRsrvInvOutXml = null;
	private static YFCLogCategory log;
	private String enterpriseCode = "";
	private String ignoreSafetyFactor = "";
	private String reserveInventory = "";
	private String allowPartialReservations = "";

	List<String> storeList = new ArrayList<>();

	static {
		log = YFCLogCategory.instance(IndgLookupRsrvInventoryCall.class);
	}

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
	@Override
	public YFCDocument invoke(YFCDocument inXml) throws YFSException {
		populateRequiredData(inXml);
		if (Boolean.parseBoolean(reserveInventory)) {
			/** Input for reserveItemInventory API is prepared */
			prepareInputForReservation(inXml);

			/** API invocation method */
			invokeReservationAPI();

		}

		return docRsrvInvOutXml;
	}

	private void invokeReservationAPI() {
		YFCDocument reserveOut = invokeYantraApi("reserveItemInventoryList",
				docRsrvInvInXml);

		Document docReservOut = reserveOut.getDocument();
		log.verbose("The output of the reserveInventory API is:" + reserveOut);

		Boolean cartAttributesPopulated = false;

		docRsrvInvOutXml = YFCDocument.createDocument("Cart");
		YFCElement eleCart = docRsrvInvOutXml.getDocumentElement();

		YFCElement eleFulfillmentOptions = eleCart
				.createChild("FulfillmentOptions");

		for (String strStore : storeList) {
			log.verbose(strStore);
			ArrayList<Element> alRsrv = SCXmlUtil.getElementsByAttribute(
					docReservOut.getDocumentElement(), "ReserveItemInventory",
					"ShipNode", strStore);

			YFCElement eleFulfillmentOption = eleFulfillmentOptions
					.createChild("FulfillmentOption");
			eleFulfillmentOption.setAttribute(XMLLiterals.DELIVERY_METHOD,
					"PICK");
			eleFulfillmentOption.setAttribute("NodeId", strStore);
			eleFulfillmentOption.setAttribute("EstimatedPickingStartTime", "");
			eleFulfillmentOption.setAttribute("EstimatedPickingEndTime", "");
			eleFulfillmentOption.setAttribute(
					"EstimatedCustomerPickupStartTime", "");
			eleFulfillmentOption.setAttribute("AllocatedShiftId", "DummyShift");

			YFCElement eleLineItems = eleFulfillmentOption
					.createChild("LineItems");

			for (int i = 0; i < alRsrv.size(); i++) {
				Element eleReserveItemInventory = alRsrv.get(i);
				log.verbose("the element is: " + i + " "
						+ SCXmlUtil.getString(eleReserveItemInventory));

				YFCElement eleLineItem = eleLineItems.createChild("LineItem");
				eleLineItem.setAttribute("Id",
						alRsrv.get(i).getAttribute("ItemID"));
				String strQtyToBeReserved = alRsrv.get(i).getAttribute(
						"QtyToBeReserved");
				String strReservedQty = alRsrv.get(i).getAttribute(
						"ReservedQty");
				eleLineItem.setAttribute("RequestedQuantity",
						strQtyToBeReserved);
				eleLineItem.setAttribute("ReservedQuantity", strReservedQty);
				eleLineItem.setAttribute("AvailableQuantity", strReservedQty);

				if (!cartAttributesPopulated) {
					eleCart.setAttribute("ReservationId", alRsrv.get(i)
							.getAttribute("ReservationID"));
					eleCart.setAttribute("ReservationExpiryTime", alRsrv.get(i)
							.getAttribute("ExpirationDate"));
					cartAttributesPopulated=true;
				}

			}

		}

		log.verbose("The XML published to the calling application is: "
				+ docRsrvInvOutXml);
	}

	private void prepareInputForReservation(YFCDocument inXml) {
		docRsrvInvInXml = YFCDocument
				.createDocument("ReserveItemInventoryList");
		YFCElement eleReserveItemInventoryList = docRsrvInvInXml
				.getDocumentElement();

		eleReserveItemInventoryList.setAttribute("ApplyOnhandSafetyFactor",
				(Boolean.parseBoolean(ignoreSafetyFactor)) ? "N" : "Y");
		eleReserveItemInventoryList.setAttribute("ReserveIfPartial",
				(Boolean.parseBoolean(allowPartialReservations)) ? "Y" : "N");

		YFCNodeList<YFCElement> nlLineItems = inXml.getDocumentElement()
				.getElementsByTagName("LineItem");

		String uniqueID = UUID.randomUUID().toString();

		if (nlLineItems.getLength() > 0) {
			for (YFCElement eleLineItem : nlLineItems) {
				YFCElement eleReserveItemInventory = eleReserveItemInventoryList
						.createChild("ReserveItemInventory");
				eleReserveItemInventory.setAttribute(
						XMLLiterals.DELIVERY_METHOD,
						eleLineItem.getAttribute(XMLLiterals.DELIVERY_METHOD));
				eleReserveItemInventory.setAttribute(
						"ExpirationDate",
						inXml.getDocumentElement().getAttribute(
								"TargetReservationExpiryTime"));
				eleReserveItemInventory.setAttribute("ItemID",
						eleLineItem.getAttribute("Id"));
				eleReserveItemInventory.setAttribute("QtyToBeReserved",
						eleLineItem.getAttribute("Quantity"));
				eleReserveItemInventory.setAttribute("ShipNode",
						eleLineItem.getAttribute("NodeId"));
				eleReserveItemInventory.setAttribute(
						"ShipDate",
						inXml.getDocumentElement().getAttribute(
								"RequestedPickupTime"));
				if (!storeList.contains(eleLineItem.getAttribute("NodeId"))) {
					storeList.add(eleLineItem.getAttribute("NodeId"));
				}
				eleReserveItemInventory.setAttribute("OrganizationCode",
						enterpriseCode);
				eleReserveItemInventory.setAttribute("ItemOrganizationCode",
						enterpriseCode);
				eleReserveItemInventory.setAttribute("UnitOfMeasure", "EACH");
				eleReserveItemInventory.setAttribute("ProductClass", "GOOD");
				eleReserveItemInventory.setAttribute("ReservationID", uniqueID);
			}
			log.verbose("The reserveItemInventory Input:" + docRsrvInvInXml);
		}
	}

	private void populateRequiredData(YFCDocument inXml) {
		enterpriseCode = inXml.getDocumentElement().getAttribute(
				ValueConstants.ENTERPRISE_CODE, "");
		ignoreSafetyFactor = inXml.getDocumentElement().getAttribute(
				"IgnoreSafetyFactor", "");
		reserveInventory = inXml.getDocumentElement().getAttribute(
				"ReserveInventory", "");
		allowPartialReservations = inXml.getDocumentElement().getAttribute(
				"AllowPartialReservations", "");

		if (SCUtil.isVoid(enterpriseCode)) {
			inXml.getDocumentElement().setAttribute(
					ValueConstants.ENTERPRISE_CODE,
					getProperty(ValueConstants.ENTERPRISE_CODE, true));
			enterpriseCode = getProperty(ValueConstants.ENTERPRISE_CODE, true);
		}

		if (SCUtil.isVoid(ignoreSafetyFactor)) {
			inXml.getDocumentElement().setAttribute("IgnoreSafetyFactor",
					getProperty("IgnoreSafetyFactor", true));
			ignoreSafetyFactor = getProperty("ignoreSafetyFactor", true);
		}
		if (SCUtil.isVoid(reserveInventory)) {
			inXml.getDocumentElement().setAttribute("ReserveInventory",
					getProperty("ReserveInventory", true));
			reserveInventory = getProperty("ReserveInventory", true);
		}
		if (SCUtil.isVoid(allowPartialReservations)) {
			inXml.getDocumentElement().setAttribute("AllowPartialReservations",
					getProperty("AllowPartialReservations", true));
			allowPartialReservations = getProperty("AllowPartialReservations",
					true);
		}

	}
}
