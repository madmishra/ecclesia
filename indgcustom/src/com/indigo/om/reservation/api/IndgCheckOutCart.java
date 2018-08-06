package com.indigo.om.reservation.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.collections.map.HashedMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.ibm.icu.util.Calendar;
import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSException;

public class IndgCheckOutCart extends AbstractCustomApi {

	YFCDocument docRsrvInvInXml = null;
	YFCDocument docRsrvInvOutXml = null;
	private static YFCLogCategory log;

	private String reserveInventory = "";
	private String allowPartialReservations = "";
	private String skipCapacityChecks = "";
	private String SafetyFactorOverride = "";
	private String LookForwardWindow = "";
	private String ReservationExpiryWindow = "";
	private String LegacyOMSProcessingTime = "";
	private String SAPAcknowledgementTime = "";
	private String StoreProcessingTime = "";
	private String StorePreClosingBufferTime = "";

	private HashMap<String,String> hmShipNodeLocale = new HashMap();
	private HashMap<String,YFCDocument> hmShipNodeTime = new HashMap();
	private boolean AllValidNodes = true;
	
	List<String> storeList = new ArrayList<>();

	static {
		log = YFCLogCategory.instance(IndgCheckOutCart.class);
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
		
		if(!Boolean.parseBoolean(skipCapacityChecks)){
			populateValidShipNodes(inXml);
			String RequestedPickupTime = inXml.getDocumentElement().getAttribute("RequestedPickupTime");
			calculateTargetTime(RequestedPickupTime);
		}
		
		if (Boolean.parseBoolean(reserveInventory)
				|| "1".equalsIgnoreCase(reserveInventory)) {
			
			/** Input for reserveItemInventory API is prepared */
			prepareInputForReservation(inXml);

			/** API invocation method */
			invokeReservationAPI();

		} else {

			/** Input for findInventory API is prepared */
			prepareInputForfindInventory(inXml);

			/** API invocation method */
			invokeFindInventoryAPI();
		}

		return docRsrvInvOutXml;
	}

	private void invokeFindInventoryAPI() {

		YFCDocument reserveOut = invokeYantraApi("findInventory",
				docRsrvInvInXml);
		// YFCDocument reserveOut = YFCDocument.getDocumentFor(new
		// File("C:/Input/FIOutput.xml"));

		Document docReservOut = reserveOut.getDocument();
		log.verbose("The output of the findInventory API is:" + reserveOut);

		Boolean cartAttributesPopulated = false;

		docRsrvInvOutXml = YFCDocument.createDocument("Cart");
		YFCElement eleCart = docRsrvInvOutXml.getDocumentElement();

		YFCElement eleFulfillmentOptions = eleCart
				.createChild("FulfillmentOptions");

		for (String strStore : storeList) {
			log.verbose(strStore);
			
			ArrayList<Element> alRsrv = new ArrayList<Element>();
			YFCElement eleUnavailableLines = reserveOut.getDocumentElement().getChildElement("SuggestedOption").getChildElement("UnavailableLines");
			
			if(!SCUtil.isVoid(reserveOut.getDocumentElement().getChildElement("SuggestedOption").getChildElement("Option"))){
				alRsrv = SCXmlUtil.getElementsByAttribute(
						docReservOut.getDocumentElement(),
						"SuggestedOption/Option/PromiseLines/PromiseLine",
						"ShipNode", strStore);
			}
			
			YFCElement eleFulfillmentOption = eleFulfillmentOptions
					.createChild("FulfillmentOption");
			eleFulfillmentOption.setAttribute(XMLLiterals.DELIVERY_METHOD,
					"PICK");
			eleFulfillmentOption.setAttribute("NodeId", strStore);
			eleFulfillmentOption.setAttribute("EstimatedPickingStartTime", "");
			eleFulfillmentOption.setAttribute("EstimatedPickingEndTime", "");
			eleFulfillmentOption.setAttribute(
					"EstimatedCustomerPickupStartTime", "");
			eleFulfillmentOption.setAttribute("AllocatedShiftId", "");

			YFCElement eleLineItems = eleFulfillmentOption
					.createChild("LineItems");

			if(SCUtil.isVoid(eleUnavailableLines)){
				for (int i = 0; i < alRsrv.size(); i++) {
					Element elePromiseLine = alRsrv.get(i);
					log.verbose("the element is: " + i + " "
							+ SCXmlUtil.getString(elePromiseLine));

					NodeList nlAssignment = elePromiseLine
							.getElementsByTagName("Assignment");
					if (nlAssignment.getLength() == 1
							&& ((Element) nlAssignment.item(0))
									.hasAttribute("EmptyAssignmentReason")) {
						YFCElement eleLineItem = eleLineItems
								.createChild("LineItem");
						eleLineItem.setAttribute("Id",
								alRsrv.get(i).getAttribute("ItemID"));
						eleLineItem.setAttribute("RequestedQuantity", alRsrv.get(i)
								.getAttribute("RequiredQty"));
						eleLineItem.setAttribute("ReservedQuantity", "0");
						eleLineItem.setAttribute("AvailableQuantity", "0");
					} else {
						for (int j = 0; j < nlAssignment.getLength(); j++) {
							Element eleAssignment = (Element) nlAssignment.item(j);
							if (eleAssignment.hasAttribute("EmptyAssignmentReason")) {
								continue;
							}

							YFCElement eleLineItem = eleLineItems
									.createChild("LineItem");
							eleLineItem.setAttribute("Id", alRsrv.get(i)
									.getAttribute("ItemID"));
							eleLineItem.setAttribute("RequestedQuantity", alRsrv
									.get(i).getAttribute("RequiredQty"));
							eleLineItem.setAttribute("ReservedQuantity",
									eleAssignment.getAttribute("ReservedQty"));
							eleLineItem.setAttribute("AvailableQuantity",
									eleAssignment.getAttribute("Quantity"));

						}
					}

					if (!cartAttributesPopulated) {
						eleCart.setAttribute("ReservationId", "");
						eleCart.setAttribute("ReservationExpiryTime", "");
						cartAttributesPopulated = true;
					}

				}
			} else {
				YFCNodeList<YFCElement> nlUnavailableLine = eleUnavailableLines.getElementsByTagName("UnavailableLine");
				
				for(int i=0; i<nlUnavailableLine.getLength();i++){
					YFCElement eleUnavailableLine = nlUnavailableLine.item(i);
					YFCElement eleLineItem = eleLineItems
							.createChild("LineItem");
					eleLineItem.setAttribute("Id",eleUnavailableLine.getAttribute("ItemID"));
					eleLineItem.setAttribute("RequestedQuantity",eleUnavailableLine.getAttribute("RequiredQty"));
					eleLineItem.setAttribute("ReservedQuantity","0");
					eleLineItem.setAttribute("AvailableQuantity","0");
				}
				
				if (!cartAttributesPopulated) {
					eleCart.setAttribute("ReservationId", "");
					eleCart.setAttribute("ReservationExpiryTime", "");
					cartAttributesPopulated = true;
				}
			}
			

		}

		// System.out.println("The XML published to the calling application is: "
		// + docRsrvInvOutXml);
		log.verbose("The XML published to the calling application is: "
				+ docRsrvInvOutXml);

	}

	private void prepareInputForfindInventory(YFCDocument inXml) {
		docRsrvInvInXml = YFCDocument.createDocument("Promise");
		YFCElement elePromise = docRsrvInvInXml.getDocumentElement();

		elePromise.setAttribute("CheckCapacity", (Boolean
				.parseBoolean(skipCapacityChecks) || "1"
				.equalsIgnoreCase(skipCapacityChecks)) ? "N" : "Y");
		elePromise.setAttribute(XMLLiterals.ORGANIZATION_CODE, "Indigo_CA");
		elePromise.setAttribute("ReqStartDate", inXml.getDocumentElement()
				.getAttribute("RequestedPickupTime"));
		elePromise.setAttribute("ShipDate", inXml.getDocumentElement()
				.getAttribute("RequestedPickupTime"));
		elePromise.setAttribute("ReqEndDate", inXml.getDocumentElement()
				.getAttribute("TargetReservationExpiryTime"));

		YFCElement elePromiseLines = elePromise.createChild("PromiseLines");

		YFCNodeList<YFCElement> nlLineItems = inXml.getDocumentElement()
				.getElementsByTagName("LineItem");

		if (nlLineItems.getLength() > 0) {
			int counter = 0;
			for (YFCElement eleLineItem : nlLineItems) {
				YFCElement elePromiseLine = elePromiseLines
						.createChild("PromiseLine");
				elePromiseLine.setAttribute(XMLLiterals.DELIVERY_METHOD,
						eleLineItem.getAttribute(XMLLiterals.DELIVERY_METHOD));

				elePromiseLine.setAttribute("ItemID",
						eleLineItem.getAttribute("Id"));
				elePromiseLine.setAttribute("RequiredQty",
						eleLineItem.getAttribute("Quantity"));
				elePromiseLine.setAttribute("ShipNode",
						eleLineItem.getAttribute("NodeId"));
				counter++;
				elePromiseLine.setAttribute("LineId", counter);

				if (!storeList.contains(eleLineItem.getAttribute("NodeId"))) {
					storeList.add(eleLineItem.getAttribute("NodeId"));
				}

				elePromiseLine.setAttribute("UnitOfMeasure",
						getProperty("UOM", "EACH"));
			}
			// System.out.println("The reserveAvailableInventory Input:" +
			// docRsrvInvInXml);
			log.verbose("The findInventory Input:" + docRsrvInvInXml);
		}
	}

	private void invokeReservationAPI() {
		YFCDocument reserveOut = invokeYantraApi("reserveAvailableInventory",
				docRsrvInvInXml);
		// YFCDocument reserveOut = YFCDocument.getDocumentFor(new
		// File("C:/Input/RAIOutput1.xml"));

		Document docReservOut = reserveOut.getDocument();
		log.verbose("The output of the reserveInventory API is:" + reserveOut);

		Boolean cartAttributesPopulated = false;

		docRsrvInvOutXml = YFCDocument.createDocument("Cart");
		YFCElement eleCart = docRsrvInvOutXml.getDocumentElement();

		YFCElement eleFulfillmentOptions = eleCart
				.createChild("FulfillmentOptions");

		for (String strStore : storeList) {
			System.out.println(strStore);
			log.verbose(strStore);
			ArrayList<Element> alRsrv = SCXmlUtil.getElementsByAttribute(
					docReservOut.getDocumentElement(),
					"PromiseLines/PromiseLine", "ShipNode", strStore);

			YFCElement eleFulfillmentOption = eleFulfillmentOptions
					.createChild("FulfillmentOption");
			eleFulfillmentOption.setAttribute(XMLLiterals.DELIVERY_METHOD,
					"PICK");
			eleFulfillmentOption.setAttribute("NodeId", strStore);
			eleFulfillmentOption.setAttribute("EstimatedPickingStartTime", "");
			eleFulfillmentOption.setAttribute("EstimatedPickingEndTime", "");
			eleFulfillmentOption.setAttribute(
					"EstimatedCustomerPickupStartTime", "");
			eleFulfillmentOption.setAttribute("AllocatedShiftId", "");

			YFCElement eleLineItems = eleFulfillmentOption
					.createChild("LineItems");

			for (int i = 0; i < alRsrv.size(); i++) {
				Element elePromiseLine = alRsrv.get(i);
				log.verbose("the element is: " + i + " "
						+ SCXmlUtil.getString(elePromiseLine));
				Element eleReservations = (Element) elePromiseLine
						.getElementsByTagName("Reservations").item(0);
				
				Element eleReservation = (Element) eleReservations
						.getElementsByTagName("Reservation").item(0);

				YFCElement eleLineItem = eleLineItems.createChild("LineItem");
				eleLineItem.setAttribute("Id",
						alRsrv.get(i).getAttribute("ItemID"));
				String strQtyToBeReserved = eleReservations
						.getAttribute("QtyToBeReserved");
				String strReservedQty = eleReservations
						.getAttribute("TotalReservedQty");
				eleLineItem.setAttribute("RequestedQuantity",
						strQtyToBeReserved);
				eleLineItem.setAttribute("ReservedQuantity", strReservedQty);
				eleLineItem.setAttribute("AvailableQuantity",
						eleReservations.getAttribute("AvailableQty"));
				
				if (!cartAttributesPopulated) {
					if(SCUtil.isVoid(eleReservation)){
						eleCart.setAttribute("ReservationId","");
						eleCart.setAttribute("ReservationExpiryTime","");
					} else {
						eleCart.setAttribute("ReservationId",
								eleReservation.getAttribute("ReservationID"));
						eleCart.setAttribute("ReservationExpiryTime",
								eleReservation.getAttribute("ExpirationDate"));
					}
					cartAttributesPopulated = true;
				}

			}

		}

		// System.out.println("The XML published to the calling application is: "
		// + docRsrvInvOutXml);
		log.verbose("The XML published to the calling application is: "
				+ docRsrvInvOutXml);
	}

	private void prepareInputForReservation(YFCDocument inXml) {
		docRsrvInvInXml = YFCDocument.createDocument("Promise");
		YFCElement elePromise = docRsrvInvInXml.getDocumentElement();

		elePromise.setAttribute("CheckCapacity", (Boolean
				.parseBoolean(skipCapacityChecks) || "1"
				.equalsIgnoreCase(skipCapacityChecks)) ? "N" : "Y");
		elePromise.setAttribute(XMLLiterals.ORGANIZATION_CODE, "Indigo_CA");
		if(!Boolean
				.parseBoolean(skipCapacityChecks)){
			elePromise.setAttribute("ShipDate", inXml.getDocumentElement()
					.getAttribute("RequestedPickupTime"));
		}
		

		YFCElement eleReservationParameters = elePromise
				.createChild("ReservationParameters");

		eleReservationParameters.setAttribute("AllowPartialReservation",
				(Boolean.parseBoolean(allowPartialReservations) || "1"
						.equalsIgnoreCase(allowPartialReservations)) ? "Y"
						: "N");
		
		if(!SCUtil.isVoid(inXml.getDocumentElement().getAttribute(
						"TargetReservationExpiryTime"))){
			eleReservationParameters.setAttribute(
					"ExpirationDate",
					inXml.getDocumentElement().getAttribute(
							"TargetReservationExpiryTime"));
		} else {
			Calendar calExpirationTime = Calendar.getInstance();
			calExpirationTime.add(Calendar.MINUTE, Integer.parseInt(ReservationExpiryWindow));
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			eleReservationParameters.setAttribute(
					"ExpirationDate", df.format(calExpirationTime.getTime()));
			
		}
		eleReservationParameters.setAttribute("ReservationID", UUID
				.randomUUID().toString());

		YFCElement elePromiseLines = elePromise.createChild("PromiseLines");

		YFCNodeList<YFCElement> nlLineItems = inXml.getDocumentElement()
				.getElementsByTagName("LineItem");

		if (nlLineItems.getLength() > 0) {
			int counter = 0;
			for (YFCElement eleLineItem : nlLineItems) {
				String shipNode = eleLineItem.getAttribute("NodeId");
				if(!Boolean.parseBoolean(skipCapacityChecks) && !hmShipNodeLocale.containsKey(shipNode)){
					continue;
				}
				
				YFCElement elePromiseLine = elePromiseLines
						.createChild("PromiseLine");
				
				elePromiseLine.setAttribute(XMLLiterals.DELIVERY_METHOD,
						eleLineItem.getAttribute(XMLLiterals.DELIVERY_METHOD));

				elePromiseLine.setAttribute("ItemID",
						eleLineItem.getAttribute("Id"));
				elePromiseLine.setAttribute("RequiredQty",
						eleLineItem.getAttribute("Quantity"));
				elePromiseLine.setAttribute("ShipNode",
						eleLineItem.getAttribute("NodeId"));
				counter++;
				elePromiseLine.setAttribute("LineId", counter);
				
				YFCDocument docTime = hmShipNodeTime.get(shipNode);
				
				if(!Boolean.parseBoolean(skipCapacityChecks)){
					elePromiseLine.setAttribute("ReqStartDate", docTime.getDocumentElement().getAttribute("ReqStartTime"));
					elePromiseLine.setAttribute("ReqEndDate", docTime.getDocumentElement().getAttribute("LookForwardTime"));
				}
				
				if (!storeList.contains(shipNode)) {
					storeList.add(shipNode);
				}

				elePromiseLine.setAttribute("UnitOfMeasure",
						getProperty("UOM", "EACH"));
			}
			
			log.verbose("The reserveAvailableInventory Input:"
					+ docRsrvInvInXml);
		}
	}

	private void populateRequiredData(YFCDocument inXml) {

		reserveInventory = inXml.getDocumentElement().getAttribute(
				"ReserveInventory", "");
		allowPartialReservations = inXml.getDocumentElement().getAttribute(
				"AllowPartialReservations", "");
		skipCapacityChecks = inXml.getDocumentElement().getAttribute(
				"SkipCapacityChecks", "");
		
		YFCElement eleConfigOverride = inXml.getDocumentElement().getElementsByTagName("ConfigurationOverrides").item(0);
		SafetyFactorOverride = eleConfigOverride.getAttribute("SafetyFactorOverride");
		if (SCUtil.isVoid(SafetyFactorOverride)) {
			reserveInventory = getProperty("SafetyFactorOverride", true);
		}
		
		YFCElement eleCapacity =  eleConfigOverride.getChildElement("Capacity");
		LookForwardWindow = eleCapacity.getAttribute("LookForwardWindow");
		if (SCUtil.isVoid(LookForwardWindow)) {
			LookForwardWindow = getProperty("LookForwardWindow", true);
		}
		
		ReservationExpiryWindow = eleCapacity.getAttribute("ReservationExpiryWindow");
		if (SCUtil.isVoid(ReservationExpiryWindow)) {
			ReservationExpiryWindow = getProperty("ReservationExpiryWindow", true);
		}
		
		LegacyOMSProcessingTime = eleCapacity.getAttribute("LegacyOMSProcessingTime");
		if (SCUtil.isVoid(LegacyOMSProcessingTime)) {
			LegacyOMSProcessingTime = getProperty("LegacyOMSProcessingTime", true);
		}
		
		SAPAcknowledgementTime = eleCapacity.getAttribute("SAPAcknowledgementTime");
		if (SCUtil.isVoid(SAPAcknowledgementTime)) {
			SAPAcknowledgementTime = getProperty("SAPAcknowledgementTime", true);
		}
		
		StoreProcessingTime = eleCapacity.getAttribute("StoreProcessingTime");
		if (SCUtil.isVoid(StoreProcessingTime)) {
			StoreProcessingTime = getProperty("StoreProcessingTime", true);
		}
		
		StorePreClosingBufferTime = eleCapacity.getAttribute("StorePreClosingBufferTime");
		if (SCUtil.isVoid(StorePreClosingBufferTime)) {
			StorePreClosingBufferTime = getProperty("StorePreClosingBufferTime", true);
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
		if (SCUtil.isVoid(skipCapacityChecks)) {
			inXml.getDocumentElement().setAttribute("SkipCapacityChecks",
					getProperty("SkipCapacityChecks", true));
			skipCapacityChecks = getProperty("SkipCapacityChecks", true);
		}
	}
	
	private void populateValidShipNodes(YFCDocument inXml) {
		 YFCNodeList<YFCElement> nlLineItem = inXml.getDocumentElement().getElementsByTagName("LineItem");
		 for(YFCElement eleLineItem: nlLineItem){
			 String strShipNode = eleLineItem.getAttribute("NodeId");
			 YFCDocument shipNodeInputDoc = YFCDocument.createDocument("ShipNode");
			 shipNodeInputDoc.getDocumentElement().setAttribute("ShipNode", strShipNode);
			 YFCDocument shipNodeOutputDoc = invokeYantraService("GetShipNodeList", shipNodeInputDoc);
			 
			 YFCElement eleShipNode = shipNodeOutputDoc.getDocumentElement().getElementsByTagName("ShipNode").item(0);
			 if(SCUtil.isVoid(eleShipNode)){
				 throw new YFSException("ShipNode not present", "HTTP Code 400", "Shipnode not present");
			 }
			 
			 if("Store_PickUpOnly".equalsIgnoreCase(eleShipNode.getAttribute("ShipNodeClass"))){
				 String strLocale = eleLineItem.getAttribute("Localecode"); 
				 hmShipNodeLocale.put(strShipNode, strLocale);				 
			 } else {
				 AllValidNodes=false;
				 break;
			 }
		 }
	}

	private void calculateTargetTime(String RequestedPickupTime){
		try {
			
			for(Map.Entry<String, String> entry: hmShipNodeLocale.entrySet()){
				
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				
				Calendar calStartTime = Calendar.getInstance();
				Calendar calTargetTime = Calendar.getInstance();

				if(!SCUtil.isVoid(RequestedPickupTime)){
					Date RPT = df.parse(RequestedPickupTime);
					calTargetTime.setTime(RPT);
					calTargetTime.add(Calendar.MINUTE, -Integer.parseInt(StoreProcessingTime));
				} else {
					calTargetTime.add(Calendar.MINUTE, Integer.parseInt(ReservationExpiryWindow));
					calTargetTime.add(Calendar.MINUTE, Integer.parseInt(LegacyOMSProcessingTime));
					calTargetTime.add(Calendar.MINUTE, Integer.parseInt(SAPAcknowledgementTime));
				}
				
				Calendar LookForward = Calendar.getInstance();
				LookForward.add(Calendar.DATE, Integer.parseInt(LookForwardWindow));
				LookForward.add(Calendar.MINUTE,-Integer.parseInt(StoreProcessingTime));
				
				df.setTimeZone(TimeZone.getTimeZone("UTC"));
				
				/** ShipNode Locale TimeZone : START */
				
				YFCDocument localeListApiOp = getLocaleList(entry.getValue());
				String sTimeZone = localeListApiOp.getDocumentElement().getChildElement(XMLLiterals.LOCALE).getAttribute(XMLLiterals.TIME_ZONE);
				
				/** ShipNode Locale TimeZone : END */
				
				// Updating the time to ShipNode's Timezone
				df.setTimeZone(TimeZone.getTimeZone(sTimeZone));
				
				
				String StartTime = df.format(calStartTime.getTime());
				
				String TargetTime = df.format(calTargetTime.getTime());
				
				String LookForwardTime = df.format(LookForward.getTime());
				
				calTargetTime.add(Calendar.MINUTE, Integer.parseInt(StoreProcessingTime));
				String ReqStartTime = df.format(calTargetTime.getTime()); 
				
								
				YFCDocument docShipNodeTime = YFCDocument.createDocument("Time");
				docShipNodeTime.getDocumentElement().setAttribute("StartTime", StartTime);
				docShipNodeTime.getDocumentElement().setAttribute("TargetTime", TargetTime);
				docShipNodeTime.getDocumentElement().setAttribute("LookForwardTime", LookForwardTime);
				docShipNodeTime.getDocumentElement().setAttribute("ReqStartTime", ReqStartTime);

				hmShipNodeTime.put(entry.getKey(),docShipNodeTime);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public YFCDocument getLocaleList(String localeCode){
	    return  invokeYantraApi(XMLLiterals.GET_LOCALE_LIST, formInputXmlForGetLocaleList(localeCode),formTemplateXmlForgetLocaleList());
	 }
	
	public YFCDocument getShipNodeList(String shipNode){
	    return  invokeYantraApi(XMLLiterals.GET_SHIP_NODE_LIST, formInputXmlForGetStoreList(shipNode),formTemplateXmlForgetShipNodeList());
	 }
	
	public YFCDocument formInputXmlForGetStoreList(String shipNode) {
	    YFCDocument getStoreListDoc = YFCDocument.createDocument(XMLLiterals.SHIPNODE);
	    getStoreListDoc.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, shipNode);
	    return getStoreListDoc;
	 }
	 
		
	public YFCDocument formTemplateXmlForgetShipNodeList() {
	    YFCDocument getShipNodeListTemp = YFCDocument.createDocument(XMLLiterals.SHIPNODE_LIST);
	    YFCElement shipNodeEle = getShipNodeListTemp.getDocumentElement().createChild(XMLLiterals.SHIPNODE);
	    shipNodeEle.setAttribute(XMLLiterals.SHIP_NODE_CODE, "");
	    shipNodeEle.setAttribute(XMLLiterals.LOCALE_CODE, "");
	    
	    return getShipNodeListTemp;
	 }
	
	public YFCDocument formInputXmlForGetLocaleList(String localeCode) {
	    YFCDocument getLocaleDoc = YFCDocument.createDocument(XMLLiterals.LOCALE);
	    getLocaleDoc.getDocumentElement().setAttribute(XMLLiterals.LOCALE_CODE, localeCode);
	    return getLocaleDoc;
	 }
	 
	 /**
	  * This method forms template for the getLocaleListTemp
	  * 
	  * @return
	  */
		
	public YFCDocument formTemplateXmlForgetLocaleList() {
	    YFCDocument getLocaleListTemp = YFCDocument.createDocument(XMLLiterals.LOCALE_LIST);
	    YFCElement localeEle = getLocaleListTemp.getDocumentElement().createChild(XMLLiterals.LOCALE);
	    localeEle.setAttribute(XMLLiterals.TIME_ZONE, "");
	    return getLocaleListTemp;
	 }

	 public static void main(String[] args) {
	
	 IndgCheckOutCart obj = new IndgCheckOutCart();
//	 File f = new File("C:/Input/ReserveInv.xml");
//	 YFCDocument doc = YFCDocument.getDocumentFor(f);
	
	 obj.calculateTargetTime("2018-07-28T13:22:29-04:00");
	 }
}

