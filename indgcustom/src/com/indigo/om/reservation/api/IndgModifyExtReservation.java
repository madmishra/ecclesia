package com.indigo.om.reservation.api;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSException;


/*
 * 
 * This class will be invoked as part of the service INDG_ModifyReservation.
 * This service will be invoked as the REST webservice for modifying the existing reservations.
 * 
 * <Cart ReservationId="RES2705" EnterpriseCode="TV" AllowPartialReservations="true" TargetReservationExpiryTime="2018-07-28T11:15:00Z" IgnoreSafetyFactor="true">
   <LineItems>
      <LineItem Id="502957" Quantity="0" DeliveryMethod="PICK" NodeId="TE" />
   </LineItems>

   <ConfigurationOverrides SafetyFactorOverride="2">
      <Capacity LookForwardWindow="2" ReservationExpiryWindow="15" LegacyOMSProcessingTime="60" SAPAcknowledgementTime="5" StoreProcessingTime="120"
       StorePreClosingBufferTime="60" />
   </ConfigurationOverrides>
</Cart>
 * 
 * */
public class IndgModifyExtReservation extends AbstractCustomApi {

	private static YFCLogCategory log;
	private String reservationID = "";
	private String strReservationExpireDate = "";
	
	/*
	 * Key is ItemID:DeliveryMethod:ShipNode and value is map of different quantity
	 * 
	QUANTITY: Will be populated only for existing lines. Will be blank for new line requested in modification call
	REQ_QUANTITY: Will be populated only for modified lines. Used for distinguishing lines not updated 
	AVAL_QUANTITY: Available Quantity updated after reservation call 
	RES_QUANTITY : Reserved Quantity updated after reservation call 
	*/
	Map<String, Map<String, String>> existResrMap = null;
	
	//Used for framing response. Key DeliveryMethod:ShipNode and value is List of ItemID 
	Map<String, HashSet<String>> mapForOutput = null;
	YFCDocument docReserveItem = null;
	YFCElement elePromiseLines = null;

	

	static {
		log = YFCLogCategory.instance(IndgCheckOutCart.class);
	}

	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCDocument docModifyCartOut = null;
		try {
			
			log.verbose("Input to the class ::"+inXml.toString());
			
			YFCElement inELe = inXml.getDocumentElement();
			reservationID = inELe.getAttribute("ReservationId", "");
			
			//Check ReservationID, EnterpriseCode and TargetReservationExpiryTime
			basicValidation(inELe);

			YFCDocument reservationOutDoc = callGetInventoryReservationList();
			
			//Prepare input reserveAvailableInventory
			prepareTempReserveInvDoc(inXml, reservationOutDoc);
			
			//Go through the modification input. cancelReservation is invoked if quantity is reduced 
			//or lines are appended reserveAvailableInventory doc which will be called at last
			checkModificationInp(inXml);
			
			//Call reserveAvailableInventory if there is any line appended to doc.
			callResrAvalInvApi();
			
			//Frame output of the modifyCart call
			docModifyCartOut = frameCartOutDoc();
			
			log.debug("Output of IndgModifyExtReservation:: " + docModifyCartOut.getString());
			
			return docModifyCartOut;

		} 
		catch (YFSException e) 
		{
			if ("INDG10002".equalsIgnoreCase(e.getErrorCode()))
			{
				throw new YFSException("Reservation is invalid or doesn't exist"+e, "INDG10002", "Reservation is invalid or doesn't exist");
			}else if("YFS10513".equalsIgnoreCase(e.getErrorCode()))
			{
				throw new YFSException("Organization code is mandatory for this operation"+e, "YFS10513", "Organization code is mandatory for this operation");
			}else if("YFS10395".equalsIgnoreCase(e.getErrorCode()))
			{
				throw new YFSException("Invalid Organization"+e, "YFS10395", "Invalid Organization");
			}else if("YFC004".equalsIgnoreCase(e.getErrorCode()))
			{
				throw new YFSException("Invalid Date Format"+e, "YFC004", "Invalid Date Format");
			}
			else
			{
				log.verbose("Error in invoke::" + e);
				throw new YFCException(XMLLiterals.ERROR, "Exception occured in invoke()" + e);
			}
		}
		catch (Exception e) {

			log.verbose("Error in invoke::" + e);
			throw new YFCException(XMLLiterals.ERROR, "Exception occured in invoke()" + e);

		}

		
	}

	private void basicValidation(YFCElement inELe) 
	{
		
			String orgCodeStr = inELe.getAttribute("EnterpriseCode");
			String expDateStr = inELe.getAttribute("TargetReservationExpiryTime");
			String dateFormat = "([0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2})";
			
			//EnterpriseCode Check
			if(SCUtil.isVoid(orgCodeStr))
			{
				throw new YFSException("Organization code is mandatory for this operation", "YFS10513", "");
				
			}else if(!orgCodeStr.equalsIgnoreCase(getProperty("EnterpriseCode", false)))
			{
				throw new YFSException("Invalid Organization", "YFS10395", "");
			}
			
			//ReservationID check
			if(SCUtil.isVoid(reservationID))
			{
				throw new YFSException("Reservation is invalid or doesn't exist", "INDG10002", "");
			}
			
			//ExpiryDate Check
			if(!SCUtil.isVoid(expDateStr) 
					&& !((expDateStr.matches(dateFormat+"Z")) || (expDateStr.matches(dateFormat))))
			{
				throw new YFSException("Invalid Date Format", "YFC004", "");
			}
			
	}

	private YFCDocument callGetInventoryReservationList() {
		try {

			YFCDocument docGetResInvInXml = YFCDocument.createDocument(XMLLiterals.INV_RES);
			YFCElement eleGetRes = docGetResInvInXml.getDocumentElement();
			eleGetRes.setAttribute(XMLLiterals.RESERVATION_ID, reservationID);
			
			YFCDocument docGetResInvOutXml = invokeYantraApi("getInventoryReservationList", docGetResInvInXml, getInventoryReservationsTemplate());
			
			String totalRecordsStr = docGetResInvOutXml.getDocumentElement().getAttribute("TotalNumberOfRecords", "0");
			Double totalRecordsDou = Double.parseDouble(totalRecordsStr);
			//Reservation doesn't exist in system
			if(0 == Double.compare(0.0, totalRecordsDou))
			{
				throw new YFSException("Reservation is invalid or doesn't exist", "INDG10002", "");
			}else
			{	
				prepareMapOfExisitngReservation(docGetResInvOutXml);
			}
			return docGetResInvOutXml;

		} catch (YFSException e) 
		{
			if ("INDG10002".equalsIgnoreCase(e.getErrorCode()))
			{
				throw new YFSException("Reservation is invalid or doesn't exist"+e, "INDG10002", "Reservation is invalid or doesn't exist");
			}
			else{
				log.verbose("Error in callGetInventoryReservationList::" + e);
				throw new YFCException(XMLLiterals.ERROR, "Exception occured in callGetInventoryReservationList()" + e);
			}
		}
	}
	//Create existResrMap and mapForOutput and populate basic attributes
	private void prepareMapOfExisitngReservation(YFCDocument docGetResInvOutXml) {
		try {
			existResrMap = new HashMap<>();
			mapForOutput = new HashMap<>();

			YFCNodeList<YFCElement> inventoryList = docGetResInvOutXml.getDocumentElement()
					.getElementsByTagName(XMLLiterals.INV_RES);

			if (inventoryList.getLength() > 0) {

				for (YFCElement eleLineInv : inventoryList) {
					YFCElement eleItem = eleLineInv.getChildElement("Item");
					String itemIDStr = eleItem.getAttribute(XMLLiterals.ITEM_ID);

					String deliveryMethodStr = eleLineInv.getAttribute(XMLLiterals.DELIVERY_METHOD, "NA");
					String shipNodeStr = eleLineInv.getAttribute(XMLLiterals.SHIP_NODE, "NA");
					String quantity = eleLineInv.getAttribute(XMLLiterals.QUANTITY, "0.00");

					eleLineInv.setAttribute(XMLLiterals.UOM, eleItem.getAttribute(XMLLiterals.UOM));
					eleLineInv.setAttribute(XMLLiterals.PRODUCT_CLASS, eleItem.getAttribute(XMLLiterals.PRODUCT_CLASS));

					String mapKeyStr = itemIDStr + ":" + deliveryMethodStr + ":" + shipNodeStr;

					//Map will just have QUANITY populated for the respective key
					if (!existResrMap.containsKey(mapKeyStr)) {
						Map<String, String> quantityMap =  createQuantityMap(quantity);
						existResrMap.put(mapKeyStr, quantityMap);
					}

					String outMapKeyStr = deliveryMethodStr + ":" + shipNodeStr;
					if (mapForOutput.containsKey(outMapKeyStr)) {
						HashSet<String> itemList = mapForOutput.get(outMapKeyStr);
						itemList.add(itemIDStr);
						mapForOutput.put(outMapKeyStr, itemList);

					} else {
						HashSet<String> itemList = new HashSet<>();
						itemList.add(itemIDStr);
						mapForOutput.put(outMapKeyStr, itemList);
					}

				}

				log.verbose("Map  ::" + existResrMap.toString());

			}

		} catch (Exception e) {
			log.verbose("Error in prepareMapOfExisitngReservation::" + e);
			throw new YFCException(XMLLiterals.ERROR, "Exception occured in prepareMapOfExisitngReservation()" + e);
		}

	}

	//Populate existResrMap
	private Map<String, String> createQuantityMap(String quantity) {

		Map<String, String> quantityMap = new HashMap<>();

		quantityMap.put(XMLLiterals.QUANTITY, quantity);
		quantityMap.put(XMLLiterals.REQ_QUANTITY, null);
		quantityMap.put(XMLLiterals.AVAL_QUANTITY, null);
		quantityMap.put(XMLLiterals.RES_QUANTITY, null);

		return quantityMap;
	}

	//Create template input doc for reserveAvailableInventory. Lines will be appended in appendItemInReserveDoc().
	private void prepareTempReserveInvDoc(YFCDocument inXml, YFCDocument reservationOutDoc) {
		String shipDate = null;
		YFCElement resEle = reservationOutDoc.getDocumentElement();
		YFCElement invEle = resEle.getChildElement(XMLLiterals.INV_RES);
		if (!SCUtil.isVoid(invEle)) {
			shipDate = invEle.getAttribute("ShipDate", " ");
		}

		docReserveItem = YFCDocument.createDocument("Promise");
		YFCElement elePromise = docReserveItem.getDocumentElement();

		elePromise.setAttribute("CheckCapacity", "N");
		elePromise.setAttribute(XMLLiterals.ORGANIZATION_CODE, "Indigo_CA");
		if (!SCUtil.isVoid(shipDate)) {
			elePromise.setAttribute("ReqStartDate", shipDate);
			elePromise.setAttribute("ShipDate", shipDate);
		}

		strReservationExpireDate = inXml.getDocumentElement().getAttribute("TargetReservationExpiryTime");
		elePromise.setAttribute("ReqEndDate", strReservationExpireDate);

		YFCElement eleReservationParameters = elePromise.createChild("ReservationParameters");

		String allowPartialReservations = inXml.getDocumentElement().getAttribute("AllowPartialReservations", "");

		eleReservationParameters.setAttribute("AllowPartialReservation",
				(Boolean.parseBoolean(allowPartialReservations) || "1".equalsIgnoreCase(allowPartialReservations)) ? "Y"
						: "N");

		eleReservationParameters.setAttribute("ExpirationDate",
				inXml.getDocumentElement().getAttribute("TargetReservationExpiryTime"));

		eleReservationParameters.setAttribute(XMLLiterals.RESERVATION_ID, reservationID);
		elePromiseLines = elePromise.createChild("PromiseLines");

		log.debug("Doc prepared in prepareTempReserveInvDoc ::" + docReserveItem.getString());

	}

	/*Check the requested modification input. Possible modifications are
	 * 
	 * CASE 1 - Increase in the reserved quantity: This line will be appended in reserveAvailableInventory doc with delta quantity
	 * CASE 2 - Decrease in the reserved quantity: cancelReservation will be invoked with the delta quantity.
	 * CASE 3 - Adding new line: This line will be appended in reserveAvailableInventory doc with requested quantity
	 * CASE 4 - Deleting existing line: cancelReservation will be invoked with the reserved quantity.
	 * 
	 * */
	private void checkModificationInp(YFCDocument inXml) {
		try {

			YFCNodeList<YFCElement> nlLineItems = inXml.getDocumentElement().getElementsByTagName("LineItem");

			if (nlLineItems.getLength() > 0) {
				int counter = 0;
				for (YFCElement eleLineItem : nlLineItems) {
					String itemIDStr = eleLineItem.getAttribute("Id");
					String deliveryMethodStr = eleLineItem.getAttribute(XMLLiterals.DELIVERY_METHOD, "NA");
					String shipNodeStr = eleLineItem.getAttribute(XMLLiterals.NODE_ID, "NA");

					String quantityStr = eleLineItem.getAttribute(XMLLiterals.QUANTITY);
					Double modQuantityDou = Double.parseDouble(quantityStr);

					String mapKeyStr = itemIDStr + ":" + deliveryMethodStr + ":" + shipNodeStr;

					if (!SCUtil.isVoid(existResrMap) && existResrMap.containsKey(mapKeyStr)) 
					{
						Map<String, String> quantityMap = existResrMap.get(mapKeyStr);
						String orgQuantityStr = quantityMap.get(XMLLiterals.QUANTITY);
						Double orgQuantityDou = Double.parseDouble(orgQuantityStr);

						// Populate Requested Quantity in the Original Map
						quantityMap.put(XMLLiterals.REQ_QUANTITY, quantityStr);

						//Executing CASE 4
						if (0 == Double.compare(0.0, modQuantityDou)) {
							quantityMap.put(XMLLiterals.REQ_QUANTITY, "0");
							// Cancel all quantity in existing reservation for
							// this set
							callCancelReservation(eleLineItem, orgQuantityStr);

						//Executing CASE 2
						} else if (0 < Double.compare(orgQuantityDou, modQuantityDou))
						{
							Double d = orgQuantityDou - modQuantityDou;
							String cancelledQtyStr = callCancelReservation(eleLineItem, d.toString());

							Double avalQtyDoc = orgQuantityDou - Double.parseDouble(cancelledQtyStr);
							quantityMap.put(XMLLiterals.AVAL_QUANTITY, avalQtyDoc.toString());
							quantityMap.put(XMLLiterals.RES_QUANTITY, avalQtyDoc.toString());

						//Executing CASE 1
						} else if (0 > Double.compare(orgQuantityDou, modQuantityDou))
						{
							Double d = modQuantityDou - orgQuantityDou;
							counter++;
							appendItemInReserveDoc(eleLineItem, d.toString(), counter);
						}

						existResrMap.put(mapKeyStr, quantityMap);
					} 
					//Executing CASE 3
					else {
						// This is the new line which was not added in Original
						// Map
						Map<String, String> quantityMap = createQuantityMap(null);
						quantityMap.put(XMLLiterals.REQ_QUANTITY, quantityStr);
						existResrMap.put(mapKeyStr, quantityMap);
						
						String outMapKeyStr = deliveryMethodStr + ":" + shipNodeStr;
						if (mapForOutput.containsKey(outMapKeyStr)) {
							HashSet<String> itemList = mapForOutput.get(outMapKeyStr);
							itemList.add(itemIDStr);
							mapForOutput.put(outMapKeyStr, itemList);

						} else {
							HashSet<String> itemList = new HashSet<>();
							itemList.add(itemIDStr);
							mapForOutput.put(outMapKeyStr, itemList);
						}
						// Add this line in existing reservation
						counter++;
						appendItemInReserveDoc(eleLineItem, quantityStr, counter);
					}
				}
			}

		} catch (Exception e) {

			log.verbose("Error in checkModificationInp::" + e);
			throw new YFCException(XMLLiterals.ERROR, "Exception occured in checkModificationInp()" + e);
		}

	}

	//Invoked for adding lines in template input of reserveAvailableInventory.
	private void appendItemInReserveDoc(YFCElement eleLineItem, String addQuantity, int counter) {
		try {

			YFCElement elePromiseLine = elePromiseLines.createChild(XMLLiterals.PROMISE_LINE);
			elePromiseLine.setAttribute(XMLLiterals.DELIVERY_METHOD,
					eleLineItem.getAttribute(XMLLiterals.DELIVERY_METHOD));

			elePromiseLine.setAttribute(XMLLiterals.ITEM_ID, eleLineItem.getAttribute("Id"));
			elePromiseLine.setAttribute("RequiredQty", addQuantity);
			elePromiseLine.setAttribute(XMLLiterals.SHIP_NODE, eleLineItem.getAttribute(XMLLiterals.NODE_ID));

			elePromiseLine.setAttribute("LineId", counter);

			 elePromiseLine.setAttribute("UnitOfMeasure", getProperty("UOM","EACH"));
			//elePromiseLine.setAttribute(XMLLiterals.UOM, "EACH");
			
		} catch (Exception e) {
			log.verbose("Error in appendItemInReserveDoc::" + e);
			throw new YFCException(XMLLiterals.ERROR, "Exception occured in appendItemInReserveDoc()" + e);
		}

	}

	//Invoked for canceling lines/ reducing quantities.
	private String callCancelReservation(YFCElement eleLineItem, String cancelQuantity) {
		try {
			String cancelledQty = "0.0";
			YFCDocument cancelInDoc = YFCDocument.createDocument("CancelReservation");
			YFCElement eleCancelRes = cancelInDoc.getDocumentElement();

			eleCancelRes.setAttribute(XMLLiterals.ORGANIZATION_CODE, "Indigo_CA");
			eleCancelRes.setAttribute(XMLLiterals.RESERVATION_ID, reservationID);
			eleCancelRes.setAttribute(XMLLiterals.SHIP_NODE, eleLineItem.getAttribute("NodeId"));
			eleCancelRes.setAttribute(XMLLiterals.ITEM_ID, eleLineItem.getAttribute("Id"));
			eleCancelRes.setAttribute("QtyToBeCancelled", cancelQuantity);

			 eleCancelRes.setAttribute("UnitOfMeasure", getProperty("UOM","EACH"));
			//eleCancelRes.setAttribute(XMLLiterals.UOM, "EACH");
			
			YFCDocument docGetResInvOutXml = invokeYantraApi("cancelReservation", cancelInDoc);

			if (!SCUtil.isVoid(docGetResInvOutXml)) {
				cancelledQty = docGetResInvOutXml.getDocumentElement().getAttribute("CancelledQty", "0.0");
			}

			return cancelledQty;
		} catch (Exception e) {
			log.verbose("Error in callCancelReservation::" + e);
			throw new YFCException(XMLLiterals.ERROR, "Exception occured in callCancelReservation()" + e);
		}

	}

	private void callResrAvalInvApi() {
		try {

			YFCNodeList<YFCElement> promiseLineList = docReserveItem.getDocumentElement()
					.getElementsByTagName("PromiseLine");

			if (promiseLineList.getLength() > 0) {
				YFCDocument docReserverAvalInvOut = invokeYantraApi("reserveAvailableInventory", docReserveItem);

				if (!SCUtil.isVoid(docReserverAvalInvOut)) {
					YFCNodeList<YFCElement> promiseLineListOut = docReserverAvalInvOut.getDocumentElement()
							.getElementsByTagName("PromiseLine");

					populateExistMapAfterResv(promiseLineListOut);

				}
			}

		} catch (Exception e) {

			log.verbose("Error in callResrAvalInv::" + e);
			throw new YFCException(XMLLiterals.ERROR, "Exception occured in callResrAvalInv()" + e);

		}

	}

	//Update AVAL_QUANTITY and RES_QUANTITY in existResrMap after reserveAvailableInventory call.
	private void populateExistMapAfterResv(YFCNodeList<YFCElement> promiseLineListOut) {
		try {

			if (promiseLineListOut.getLength() > 0) {

				for (YFCElement promiseLineOutEle : promiseLineListOut) {
					
					YFCElement eleReservations = promiseLineOutEle.getElementsByTagName("Reservations").item(0);
					String itemIDStr = promiseLineOutEle.getAttribute("ItemID");
					String deliveryMethodStr = promiseLineOutEle.getAttribute("DeliveryMethod", "PICK");
					String shipNodeStr = promiseLineOutEle.getAttribute("ShipNode", "NA");

					String mapKeyStr = itemIDStr + ":" + deliveryMethodStr + ":" + shipNodeStr;

					if (!SCUtil.isVoid(existResrMap) && existResrMap.containsKey(mapKeyStr)) {
						Map<String, String> quantityMap = existResrMap.get(mapKeyStr);
						
						//To get Available and Total reserved quantity after reservation call
						Double avalQtyDou = Double.parseDouble(eleReservations.getAttribute("AvailableQty", "0.0"));
						Double totResDou =  Double.parseDouble(eleReservations.getAttribute("TotalReservedQty", "0.0"));
						
						//Add existing quantity to the existing lines. As call was made only for delta quantity
						String strQty = quantityMap.get(XMLLiterals.QUANTITY);
						if (!SCUtil.isVoid(strQty)) 
						{
							 avalQtyDou = Double.parseDouble(strQty) + avalQtyDou;
							 totResDou = Double.parseDouble(strQty) + totResDou;
						}
						quantityMap.put(XMLLiterals.AVAL_QUANTITY, avalQtyDou.toString());
						quantityMap.put(XMLLiterals.RES_QUANTITY, totResDou.toString());
					}
				}
			}
		} catch (Exception e) 
		{
			log.verbose("Error in populateExistMapAfterResv::" + e);
			throw new YFCException("Error", "Exception occured in populateExistMapAfterResv()" + e);
		}
	}

	//Prepare output of the service.
	private YFCDocument frameCartOutDoc() {
		try {
			YFCDocument outDoc = YFCDocument.createDocument("Cart");
			YFCElement eleCart = outDoc.getDocumentElement();

			eleCart.setAttribute("ReservationId", reservationID);
			eleCart.setAttribute("ReservationExpiryTime", strReservationExpireDate);

			YFCElement eleFulfillmentOptions = eleCart.createChild("FulfillmentOptions");

			Set entrySet = mapForOutput.entrySet();
			Iterator it = entrySet.iterator();

			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				String key = (String) me.getKey();

				YFCElement eleFulfillmentOption = eleFulfillmentOptions.createChild("FulfillmentOption");

				String[] arrSplit = key.split(":");
				eleFulfillmentOption.setAttribute("DeliveryMethod", arrSplit[0]);
				eleFulfillmentOption.setAttribute("NodeId", arrSplit[1]);

				HashSet<String> itemSet = (HashSet<String>) me.getValue();
				Iterator<String> itItem = itemSet.iterator();

				YFCElement eleLineItems = eleFulfillmentOption.createChild("LineItems");

				while (itItem.hasNext()) {
					String strItemID = (String) itItem.next();

					String qtyMapKey = strItemID + ":" + key;
					Map<String, String> qtyMap = existResrMap.get(qtyMapKey);

					String strReqQty = qtyMap.get(XMLLiterals.REQ_QUANTITY);
					String strAvalQty = "";
					String strResQty = "";
					
					//REQ_QUANTITY will be blank for the lines not included in modification calls.
					if (SCUtil.isVoid(strReqQty)) {
						strReqQty = qtyMap.get(XMLLiterals.QUANTITY);
						strAvalQty = qtyMap.get(XMLLiterals.QUANTITY);
						strResQty = qtyMap.get(XMLLiterals.QUANTITY);
					} 
					//Completely cancelled line will not be appended in the final output
					else if (strReqQty.equalsIgnoreCase("0")) {
						continue;
					} 
					else {
						strAvalQty = qtyMap.get(XMLLiterals.AVAL_QUANTITY);
						strResQty = qtyMap.get(XMLLiterals.RES_QUANTITY);
					}

					YFCElement eleLineItem = eleLineItems.createChild("LineItem");
					eleLineItem.setAttribute("Id", strItemID);
					eleLineItem.setAttribute("RequestedQuantity", strReqQty);
					eleLineItem.setAttribute("AvailableQuantity", strAvalQty);
					eleLineItem.setAttribute("ReservedQuantity", strResQty);
				}

			}
			
			//Scenario where all the lines are cancelled
			YFCNodeList<YFCElement> addedLineItemList = outDoc.getDocumentElement().getElementsByTagName("LineItem");
			if (addedLineItemList.getLength() <= 0)
			{
				YFCDocument cancelDoc = YFCDocument.createDocument("Cart");
				YFCElement eleCancel = cancelDoc.getDocumentElement();

				eleCancel.setAttribute("ReservationId", reservationID);
				
				return cancelDoc;
			}

			return outDoc;
		} catch (Exception e) {

			log.verbose("Error in frameCartOutDoc::" + e);
			throw new YFCException("Error", "Exception occured in frameCartOutDoc()" + e);
		}

	}
	
	private YFCDocument getInventoryReservationsTemplate() {
		return YFCDocument.getDocumentFor(
				"<InventoryReservations TotalNumberOfRecords=''>"
						+ "<InventoryReservation DeliveryMethod='' DemandType='' ExpirationDate='' InventoryItemKey='' InventoryReservationKey=''  MinShipByDate='' "
						+ "OrganizationCode='' ProductAvailabilityDate='' Quantity='' ReservationID='' ShipDate='' ShipNode=''>"
							+ "<Item InventoryOrganizationCode='' ItemID='' ProductClass=''  UnitOfMeasure='' />"
						+ "</InventoryReservation>"
				+ "</InventoryReservations>");

	}

}
