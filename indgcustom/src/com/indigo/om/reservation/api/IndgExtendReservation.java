/**
 * 
 */
package com.indigo.om.reservation.api;

import java.util.HashMap;
import java.util.Map;

import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.date.YTimestamp;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSException;

/**
 * @author BSG106
 * 
 */
public class IndgExtendReservation extends AbstractCustomApi {
	private static YFCLogCategory log = YFCLogCategory.instance(IndgExtendReservation.class);
	
	/*
	 * ***********************************************************************************/
	/*Test with a multiApi
	 <MultiApi>
	<API Name="adjustInventory">
		<Input>
			<Items>
				<Item AccountNo="" AdjustmentType="ADJUSTMENT"  ItemID="100001" OrganizationCode="Matrix" ProductClass="" Quantity="10" ShipNode="Mtrx_Store_1" SupplyType="ONHAND" UnitOfMeasure="EACH" />
				<Item AccountNo="" AdjustmentType="ADJUSTMENT"  ItemID="100002" OrganizationCode="Matrix" ProductClass="" Quantity="15" ShipNode="Mtrx_Store_2" SupplyType="ONHAND" UnitOfMeasure="EACH" />
				<Item AccountNo="" AdjustmentType="ADJUSTMENT"  ItemID="100003" OrganizationCode="Matrix" ProductClass="" Quantity="20" ShipNode="Mtrx_Store_3" SupplyType="ONHAND" UnitOfMeasure="EACH" />
			</Items>
		</Input>
	</API>
	<API Name="reserveItemInventory">
		<Input>
			<ReserveItemInventory  ItemID="100001" OrganizationCode="Matrix" ProductClass="" QtyToBeReserved="5" ReservationID="RES1" ShipNode="Mtrx_Store_1" UnitOfMeasure="EACH" />
		</Input>
	</API>
	<API Name="reserveItemInventory">
		<Input>
			<ReserveItemInventory  ItemID="100002" OrganizationCode="Matrix" ProductClass="" QtyToBeReserved="5" ReservationID="RES1" ShipNode="Mtrx_Store_2" UnitOfMeasure="EACH" />
		</Input>
	</API>
	<API Name="reserveItemInventory">
		<Input>
			<ReserveItemInventory  ItemID="100003" OrganizationCode="Matrix" ProductClass="" QtyToBeReserved="5" ReservationID="RES1" ShipNode="Mtrx_Store_3" UnitOfMeasure="EACH" />
		</Input>
	</API>
	<API FlowName="INDG_ExtendReservation">
		<Input>
			<Cart ReservationId="RES1" EnterpriseCode="Matrix" AllowPartialReservations="true" 
IgnoreSafetyFactor="true">
			<LineItems>
				<LineItem Id="100001" Quantity="4" DeliveryMethod="PICK"
				NodeId="Mtrx_Store_1" /> <!-- Sample of a quantity change (from 5 to 4) -->
				
				<LineItem Id="100003" Quantity="2" DeliveryMethod="PICK"
				NodeId="Mtrx_Store_3" /> <!-- Sample of a new item being added -->
				<!--LineItem Id="1234567890123" Quantity="11" DeliveryMethod="PICK"
				NodeId="0965" /--> <!-- Sample of a quantity change (from 9 to 11) -->
			</LineItems>
			<ConfigurationOverrides SafetyFactorOverride="2"> 
				<Capacity LookForwardWindow="2" ReservationExpiryWindow="15"
				LegacyOMSProcessingTime="60" SAPAcknowledgementTime="5"
				StoreProcessingTime="120" StorePreClosingBufferTime="60" />
			</ConfigurationOverrides>
		</Cart>
		</Input>
	</API>
	
	
</MultiApi>
*/
	
	
	
	
	/*
	 * Assumptions are :-
	 * 
	 * <!-- If Indigo wants to extend the reservation, Indigo will send
	 * TargetReservationExpiryTime and all items in the cart. Sterling should
	 * have a default extension time period in the event Indigo doesn't send the
	 * TargetReservationTime. --> <!-- Indigo is expected to send only the lines
	 * that have changed for modify requests. --> <!-- If Indigo wants to cancel
	 * the entire reservation, then Action will be sent as Cancel and no line
	 * items need to be sent. -->
	 * 
	 * Input XML is :- <?xml version="1.0"?> <Cart ReservationId="RES1"
	 * EnterpriseCode="Matrix" AllowPartialReservations="true"
	 * TargetReservationExpiryTime="2018-09-21T12:34:00Z"
	 * IgnoreSafetyFactor="true"> <LineItems> <LineItem Id="100001" Quantity="4"
	 * DeliveryMethod="PICK" NodeId="Mtrx_Store_1" /> <!-- Sample of a quantity
	 * change (from 5 to 4) --> <LineItem Id="100002" Quantity="0"
	 * DeliveryMethod="PICK" NodeId="Mtrx_Store_2" /> <!-- Sample of a removal
	 * item --> <LineItem Id="100003" Quantity="2" DeliveryMethod="PICK"
	 * NodeId="Mtrx_Store_3" /> <!-- Sample of a new item being added -->
	 * <!--LineItem Id="1234567890123" Quantity="11" DeliveryMethod="PICK"
	 * NodeId="0965" /--> <!-- Sample of a quantity change (from 9 to 11) -->
	 * </LineItems> <ConfigurationOverrides SafetyFactorOverride="2"> <Capacity
	 * LookForwardWindow="2" ReservationExpiryWindow="15"
	 * LegacyOMSProcessingTime="60" SAPAcknowledgementTime="5"
	 * StoreProcessingTime="120" StorePreClosingBufferTime="60" />
	 * </ConfigurationOverrides> </Cart>
	 * 
	 * Output XML is :-
	 * 
	 * <?xml version="1.0"?> <!--Regardless of how many lines Indigo sends, the
	 * response should contain all lines in the reservation --> <!-- If Indigo
	 * cancelled all lines, then the cart will be sent with no expiry time and
	 * no fulfillment options --> <Cart ReservationId=
	 * "abc - note that the value may be different than the reservation id that was sent in the request"
	 * ReservationExpiryTime="2018-05-21T12:34:00Z"> <FulfillmentOptions> <!--
	 * Store 1 - BEGIN --> <!-- Sterling will send different destinations for
	 * different delivery methods for the same node --> <FulfillmentOption
	 * DeliveryMethod="PICK" NodeId="0280"> <LineItems> <LineItem
	 * Id="1234567890123" RequestedQuantity="4" AvailableQuantity="4"
	 * ReservedQuantity="4" /> <!-- Note that item 1234567890124 has been
	 * removed from the reservation. --> <LineItem Id="1234567890125"
	 * RequestedQuantity="2" AvailableQuantity="0" ReservedQuantity="0" /> <!--
	 * Example of a line being unavailable --> </LineItems> </FulfillmentOption>
	 * <!-- Store 1 - END --> <!-- Store 2 - BEGIN --> <FulfillmentOption
	 * DeliveryMethod="PICK" NodeId="0965"> <LineItems> <LineItem
	 * Id="1234567890123" RequestedQuantity="11" AvailableQuantity="10"
	 * ReservedQuantity="9" /> <!-- Or 10 if partial reservations were requested
	 * --> <LineItem Id="1234567890124" RequestedQuantity="2"
	 * AvailableQuantity="2" ReservedQuantity="2" /> </LineItems>
	 * </FulfillmentOption> <!-- Store 2 - END --> </FulfillmentOptions> </Cart>
	 * 
	 * While modifying or extending an existing reservation, if the existing
	 * reservation doesn't exist, or if it has expired, then Sterling would
	 * return a HTTP response code of 400, with the error and Error Description
	 * value of "Reservation is details containing the error code and error
	 * description and Error Code of INDG10002 invalid or doesn't exist.".
	 */
	@Override
	public YFCDocument invoke(YFCDocument iDoc2Service) throws YFSException {
		/*Block Start- copy shifts from a live and temporary reservation*/
		log.beginTimer("IndgExtendReservation-->invoke");
		log.debug("input to service is iDoc2Service-->"+iDoc2Service);
		YFCElement iRoot2Service = iDoc2Service.getDocumentElement();
		
		String sOldResrvation = iRoot2Service.getAttribute("ReservationId");
		YFCDocument iDoc2GetReservation = YFCDocument
				.createDocument("InventoryReservation");
		YFCElement iRoot2GetReservation = iDoc2GetReservation.getDocumentElement();
		iRoot2GetReservation.setAttribute("ReservationId",
				sOldResrvation);
		iRoot2GetReservation.setAttribute("ExpirationDateQryType", "GT");
		YTimestamp now = new YTimestamp(false);
		iRoot2GetReservation.setAttribute("ExpirationDate",now);
		log.debug("input to getInventoryReservationList is iDoc2GetReservation-->"+iDoc2GetReservation);
		YFCDocument oDoc4mGetReservation = invokeYantraApi(
				"getInventoryReservationList", iDoc2GetReservation);
		log.debug("output from getInventoryReservationList is existing reservation in DB-->"+oDoc4mGetReservation);
		PopulateShiftFromExistingReservation(oDoc4mGetReservation, iRoot2Service, sOldResrvation);
		/*Block End*/
		
		
		/*Block Start- Cancel Reservation*/
		YFCDocument iDoc2CancelReservation = YFCDocument.getDocumentFor(iRoot2Service
				.toString());
		log.debug("input to cencelation-->"+iDoc2CancelReservation);
		invokeYantraService("INDG_CancelReservation", iDoc2CancelReservation);
		/*Block End*/
		
		/*Block Start- Extend Reservation*/
		String extnMins = getProperty("yfs.MinutesExtendedBeyondPresent", "720");
		log.debug("Minutes to extend the reservation starting now-->"+extnMins);
		YTimestamp expiryDate = YTimestamp.newTimestamp(now,
				Integer.parseInt(extnMins) * 60);
		iRoot2Service.setAttribute("TargetReservationExpiryTime", expiryDate);
		log.debug("TargetReservationExpiryTime-->"+expiryDate);
		return invokeYantraService("INDG_CheckOutCart", iDoc2Service);
		/*Block end*/

	}
	
	
	private void PopulateShiftFromExistingReservation(
			YFCDocument oDoc4mGetReservation, YFCElement iRoot2Service, String sOldResrvation) {
		Map<String,String> mStoreShiftForResvn = new HashMap<String,String>();
		YFCNodeList<YFCElement> nlInvenReserv = oDoc4mGetReservation.getElementsByTagName("InventoryReservation");
		for(YFCElement elInvenReserv:nlInvenReserv){
			//String sInvItemKey = elInvenReserv.getAttribute("InventoryItemKey");
			String sStore = elInvenReserv.getAttribute("ShipNode");
			if(mStoreShiftForResvn.containsKey(sStore))continue;
			mStoreShiftForResvn.put(sStore,findShift(sStore,sOldResrvation));
		}
		if(!mStoreShiftForResvn.isEmpty()){
			for(YFCElement eLineItem:iRoot2Service.getElementsByTagName("LineItem")){
				eLineItem.setAttribute("AllocatedShiftId", mStoreShiftForResvn.get(eLineItem.getAttribute("NodeId")));
			}
			log.debug("shifts copied from unexpired reservations-->iRoot2Service"+ iRoot2Service);
		}
		
	}


	private String findShift(String sInvItemKey, String sStore) {
		//TODO:Neeraj:put logic to find shift
		return "dummy_shift_1";
	}


	/**
	 * @param args
	 */
	/*public static void main(String[] args) {

		YFSContext oEnv = null;
		try {
			YFSInitializer.initialize();
			oEnv = new YCPContext("admin", "password");
			oEnv.setUserTokenIgnored(true);
			oEnv.setSystemCall(true);
			
			YFCDocument iDoc2GetReservation = YFCDocument.createDocument("getReservation");
			YFCElement iRoot2GetReservation = iDoc2GetReservation.getDocumentElement();
			iRoot2GetReservation.setAttribute("ReservationId",
					"RES1");
			iRoot2GetReservation.setAttribute("ExpirationDateQryType", "GT");
			YDate now1 = new YDate(false);
			YTimestamp now = new YTimestamp(false);
			iRoot2GetReservation.setAttribute("ExpirationDate",now);
			iRoot2GetReservation.setAttribute("ExpirationDate1",now1.getYTimestamp());
			iRoot2GetReservation.setDateTimeAttribute("expirationDate3", now);
			iRoot2GetReservation.setDateTimeAttribute("expirationDate4", now1);
			org.w3c.dom.Document oDoc = YIFClientFactory.getInstance().getApi()
					.getInventoryReservationList(oEnv, iDoc2GetReservation.getDocument());
			YFCDocument outdoc= YFCDocument.getDocumentFor(oDoc);
		} catch (Exception ex) {

			ex.printStackTrace();
		}

	}*/

}
