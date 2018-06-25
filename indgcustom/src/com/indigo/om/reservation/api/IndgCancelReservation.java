package com.indigo.om.reservation.api;

import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;

public class IndgCancelReservation extends AbstractCustomApi{

	YFCDocument docCanRsrvInvInXml = null;
	YFCDocument docCanRsrvInvOutXml = null;
	private String reservationId = "";
	private static YFCLogCategory log;
		
	static {
		log = YFCLogCategory.instance(IndgCancelReservation.class);
	}
	
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		log.verbose("The Input to invoke() : "+inXml);
		
		prepareInputForCancelReservation(inXml);
		
		invokeCancelReservation();
		
		return docCanRsrvInvOutXml;
		
	}


	private void invokeCancelReservation() {
		
		YFCNodeList<YFCElement> nlCancelReservation = docCanRsrvInvInXml.getElementsByTagName("CancelReservation");
		try{
		for(int i=0;i<nlCancelReservation.getLength();i++){
			YFCElement eleCancelReservation = nlCancelReservation.item(i);
			invokeYantraApi("cancelReservation", YFCDocument.getDocumentFor(eleCancelReservation.toString()));
		}
		docCanRsrvInvOutXml = YFCDocument.createDocument("Cart");
		docCanRsrvInvOutXml.getDocumentElement().setAttribute("ReservationId", reservationId);
		} catch(Exception exc){
			docCanRsrvInvOutXml = YFCDocument.createDocument("Cart");
			docCanRsrvInvOutXml.getDocumentElement().setAttribute("ReservationId", "");
		}
		
	}


	private void prepareInputForCancelReservation(YFCDocument inXml) {
		YFCDocument docInvReserv = YFCDocument.createDocument("InventoryReservation");
		reservationId = inXml.getDocumentElement().getAttribute("ReservationId");
		docInvReserv.getDocumentElement().setAttribute("ReservationID", reservationId);
		docInvReserv.getDocumentElement().setAttribute("OrganizationCode", inXml.getDocumentElement().getAttribute("EnterpriseCode"));
		
		YFCDocument docInvReservOut = invokeYantraApi("getInventoryReservationList", docInvReserv);
		
//		YFCDocument docInvReservOut = YFCDocument.getDocumentFor(new File("C:/Input/GIRLOutput.xml"));
		
		YFCNodeList<YFCElement> nlInvenReserv = docInvReservOut.getElementsByTagName("InventoryReservation");
		
		docCanRsrvInvInXml = YFCDocument.createDocument("Cancellations");
		for(int i = 0; i<nlInvenReserv.getLength();i++){
			YFCElement eleInvReserv = nlInvenReserv.item(i);
			YFCElement eleCancelReservation = docCanRsrvInvInXml.getDocumentElement().createChild("CancelReservation");
			
			eleCancelReservation.setAttribute("ReservationID", eleInvReserv.getAttribute("ReservationID"));
			eleCancelReservation.setAttribute("QtyToBeCancelled", eleInvReserv.getAttribute("Quantity"));
			eleCancelReservation.setAttribute("ShipNode", eleInvReserv.getAttribute("ShipNode"));
			eleCancelReservation.setAttribute("OrganizationCode", eleInvReserv.getAttribute("OrganizationCode"));
			eleCancelReservation.setAttribute("ItemID", eleInvReserv.getChildElement("Item").getAttribute("ItemID"));
			eleCancelReservation.setAttribute("ProductClass", eleInvReserv.getChildElement("Item").getAttribute("ProductClass"));
			eleCancelReservation.setAttribute("UnitOfMeasure", eleInvReserv.getChildElement("Item").getAttribute("UnitOfMeasure"));
		}
	}
	
//	 public static void main(String[] args) {
//		
//		 IndgCancelReservation obj = new IndgCancelReservation();
//		 File f = new File("C:/Input/CRInput.xml");
//		 YFCDocument doc = YFCDocument.getDocumentFor(f);
//		
//		 obj.invoke(doc);
//		 }

}
