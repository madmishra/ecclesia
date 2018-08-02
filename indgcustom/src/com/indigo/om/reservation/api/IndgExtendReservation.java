/**
 * 
 */
package com.indigo.om.reservation.api;

import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.baseutil.SCUtil;
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
	
	@Override
	public YFCDocument invoke(YFCDocument iDoc2Service) throws YFSException {
		/*Block Start- copy shifts from a live and temporary reservation*/
		log.beginTimer("IndgExtendReservation-->invoke");
		log.debug("input to service is iDoc2Service-->"+iDoc2Service);
		YFCElement iRoot2Service = iDoc2Service.getDocumentElement();
		
		String sOldResrvation = iRoot2Service.getAttribute("ReservationId");
		if(SCUtil.isVoid(sOldResrvation)){
			sOldResrvation = iRoot2Service.getAttribute("ReservationID");
			iRoot2Service.setAttribute("ReservationId", sOldResrvation);
		}else
			iRoot2Service.setAttribute("ReservationID", sOldResrvation);
		YTimestamp now = new YTimestamp(false);
		String extnMins = getProperty("yfs.MinutesExtendedBeyondPresent", "720");
		YTimestamp expiryDate = YTimestamp.newTimestamp(now,
				Integer.parseInt(extnMins) * 60);
		extendCapacityReservation(sOldResrvation, now, expiryDate);
		
		
		YFCDocument iDoc2CancelReservation = YFCDocument.createDocument("CancelReservation");
		iDoc2CancelReservation.getDocumentElement().appendChild(iDoc2CancelReservation.getDocumentElement().importNode(iRoot2Service));
		log.debug("input to cencelation-->"+iDoc2CancelReservation);
		//invokeYantraService("INDG_CancelReservation", iDoc2CancelReservation);
		YFCDocument outCancelDoc = invokeYantraService("INDG_CancelReservation", iDoc2CancelReservation);
		/*Block End*/
		log.debug("output from cancellation-->"+outCancelDoc);
		/*Block Start- Extend Reservation*/
		log.debug("Minutes to extend the reservation starting now-->"+extnMins);
		iRoot2Service.setAttribute("TargetReservationExpiryTime", expiryDate);
		log.debug("TargetReservationExpiryTime-->"+expiryDate);
		return invokeYantraService("INDG_CheckOutCart", iDoc2Service);
		/*Block end*/

	}


	private void extendCapacityReservation(String sOldResrvation,
			YTimestamp now, YTimestamp expiryDate) {
		YFCDocument oDoc4mGetCapacityConsumptionDtls = findResPoolConsmptnDtl(
				sOldResrvation, now);
		if (oDoc4mGetCapacityConsumptionDtls.hasChildNodes()) {
			YFCNodeList<YFCElement> nlCapacityConsumptions = oDoc4mGetCapacityConsumptionDtls
					.getElementsByTagName("CapacityConsumption");
			if (nlCapacityConsumptions!=null && nlCapacityConsumptions.getLength() > 0) {
				YFCElement eCapacityConsumptionOnReservation = nlCapacityConsumptions
						.item(0);
				YFCDocument iDoc2ManageCapacityReservation = YFCDocument
						.createDocument("CapacityReservationList");
				YFCElement eRoot2ManageCapacity = iDoc2ManageCapacityReservation
						.getDocumentElement()
						.createChild("CapacityReservation");
				eRoot2ManageCapacity.setAttribute("ReservationId",
						sOldResrvation);
				eRoot2ManageCapacity.setAttribute("ReservationExpirationDate",
						expiryDate);
				eRoot2ManageCapacity.setAttribute("IsSlotBased",
						"Y");
				eRoot2ManageCapacity.setAttribute("Operation",
						"Modify");
				eRoot2ManageCapacity.appendChild(eRoot2ManageCapacity.importNode(eCapacityConsumptionOnReservation));
				invokeYantraApi("manageCapacityReservation", iDoc2ManageCapacityReservation);
				
			}

		}
	}
	
	
	private YFCDocument findResPoolConsmptnDtl(String sOldResrvation, YTimestamp now) {
		YFCDocument iDoc2GetCapacityConsumptionDtls =  YFCDocument.createDocument("ConsumptionDetail");
		YFCElement eRoot2GetCapacityConsumptionDtls = iDoc2GetCapacityConsumptionDtls.getDocumentElement();
		eRoot2GetCapacityConsumptionDtls.setAttribute("ReservationID", sOldResrvation);
		eRoot2GetCapacityConsumptionDtls.setAttribute("ReservationExpirationDate",now);
		eRoot2GetCapacityConsumptionDtls.setAttribute("ReservationExpirationDateQryType","GT");
		return invokeYantraApi("getResourcePoolConsumptionDetailsList", iDoc2GetCapacityConsumptionDtls);
		
		
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
			ServiceInvoker invoker = ServiceInvokerManager.getInstance().getServiceInvoker();
			invoker.setYFSEnvironment(oEnv);
			
			String in1 = "<Items><Item AccountNo='' AdjustmentType='ADJUSTMENT'  ItemID='100001' OrganizationCode='Matrix' ProductClass='' "+
"Quantity='10' ShipNode='Mtrx_Store_1' SupplyType='ONHAND' UnitOfMeasure='EACH' />"+
"<Item AccountNo='' AdjustmentType='ADJUSTMENT'  ItemID='100002' OrganizationCode='Matrix' ProductClass='' "+ 
"Quantity='15' ShipNode='Mtrx_Store_2' SupplyType='ONHAND' UnitOfMeasure='EACH' /></Items>";
			System.out.println(in1);
			YFCDocument doc1 = YFCDocument.getDocumentFor(in1);
		invoker.invokeYantraApi("adjustInventory", doc1);
		
		
		String in2 = "<ReserveItemInventory  ItemID='100001' OrganizationCode='Matrix' ProductClass='' QtyToBeReserved='5' ReservationID='RES1' "+"ShipNode='Mtrx_Store_1' UnitOfMeasure='EACH' />";
							YFCDocument doc2 = YFCDocument.getDocumentFor(in2);
						invoker.invokeYantraApi("reserveItemInventory", doc2);
			
			String in = "<Cart ReservationID='RES1' EnterpriseCode='Matrix' AllowPartialReservations='true' IgnoreSafetyFactor='true'>" +
					"<LineItems><LineItem Id='100001' Quantity='4' DeliveryMethod='PICK'	NodeId='Mtrx_Store_1' />" +
					"<LineItem Id='100003' Quantity='2' DeliveryMethod='PICK'		NodeId='Mtrx_Store_3' />" +
					"</LineItems><ConfigurationOverrides SafetyFactorOverride='2'>" +
					"<Capacity LookForwardWindow='2' ReservationExpiryWindow='15' LegacyOMSProcessingTime='60' SAPAcknowledgementTime='5' StoreProcessingTime='120' StorePreClosingBufferTime='60' />" +
					"</ConfigurationOverrides>" +
					"</Cart>";
			YFCDocument doc = YFCDocument.getDocumentFor(in);
			invoker.invokeYantraService("INDG_ExtendReservation", doc);
			oEnv.commit();
			invoker.releaseYFSEnvironment();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

	}*/

}
