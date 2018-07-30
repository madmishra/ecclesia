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
			reservationID = inELe.getAttribute("ReserveInventory", "");
			
			basicValidation();

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

	private void basicValidation() 
	{
		try{
			
			if(SCUtil.isVoid(reservationID))
			{
				throw new YFSException("Reservation is invalid or doesn't exist", "INDG10002", "");
			}
			
		}catch (YFSException e) 
		{
			if ("INDG10002".equalsIgnoreCase(e.getErrorCode()))
			{
				throw new YFSException("Reservation is invalid or doesn't exist"+e, "INDG10002", "Reservation is invalid or doesn't exist");
			}
			else
			{
				log.verbose("Error in basicValidation::" + e);
				throw new YFCException(XMLLiterals.ERROR, "Exception occured in basicValidation()" + e);
			}
		}
		catch (Exception e) {

			log.verbose("Error in basicValidation::" + e);
			throw new YFCException(XMLLiterals.ERROR, "Exception occured in basicValidation()" + e);

		}
		
	}

	private YFCDocument callGetInventoryReservationList() {
		try {

			YFCDocument docGetResInvInXml = YFCDocument.createDocument(XMLLiterals.INV_RES);
			YFCElement eleGetRes = docGetResInvInXml.getDocumentElement();
			eleGetRes.setAttribute(XMLLiterals.RESERVATION_ID, reservationID);
			YFCDocument docGetResInvOutXml = invokeYantraApi("getInventoryReservationList", docGetResInvInXml);
			
			String totalRecordsStr = docGetResInvOutXml.getDocumentElement().getAttribute("TotalNumberOfRecords", "0");
			Double totalRecordsDou = Double.parseDouble(totalRecordsStr);
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

				log.verbose("Map size ::" + existResrMap.size());

			}

		} catch (Exception e) {
			log.verbose("Error in prepareMapOfExisitngReservation::" + e);
			throw new YFCException(XMLLiterals.ERROR, "Exception occured in prepareMapOfExisitngReservation()" + e);
		}

	}

	private Map<String, String> createQuantityMap(String quantity) {

		Map<String, String> quantityMap = new HashMap<>();

		quantityMap.put(XMLLiterals.QUANTITY, quantity);
		quantityMap.put(XMLLiterals.REQ_QUANTITY, null);
		quantityMap.put(XMLLiterals.AVAL_QUANTITY, null);
		quantityMap.put(XMLLiterals.RES_QUANTITY, null);

		return quantityMap;
	}

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

						if (0 == Double.compare(0.0, modQuantityDou)) {
							quantityMap.put(XMLLiterals.REQ_QUANTITY, "0");
							// Cancel all quantity in existing reservation for
							// this set
							callCancelReservation(eleLineItem, orgQuantityStr);

						} else if (0 < Double.compare(orgQuantityDou, modQuantityDou)) {
							// Cancel only delta quantity in existing
							// reservation for this set
							Double d = orgQuantityDou - modQuantityDou;
							String cancelledQtyStr = callCancelReservation(eleLineItem, d.toString());

							Double avalQtyDoc = orgQuantityDou - Double.parseDouble(cancelledQtyStr);
							quantityMap.put(XMLLiterals.AVAL_QUANTITY, avalQtyDoc.toString());
							quantityMap.put(XMLLiterals.RES_QUANTITY, avalQtyDoc.toString());

						} else if (0 > Double.compare(orgQuantityDou, modQuantityDou)) {
							// Add only delta quantity in existing reservation
							// for this set
							Double d = modQuantityDou - orgQuantityDou;
							counter++;
							appendItemInReserveDoc(eleLineItem, d.toString(), counter);
						}

						existResrMap.put(mapKeyStr, quantityMap);
					} else {
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

					if (SCUtil.isVoid(strReqQty)) {
						strReqQty = qtyMap.get(XMLLiterals.QUANTITY);
						strAvalQty = qtyMap.get(XMLLiterals.QUANTITY);
						strResQty = qtyMap.get(XMLLiterals.QUANTITY);
					} else if (strReqQty.equalsIgnoreCase("0")) {
						continue;
					} else {
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

}
