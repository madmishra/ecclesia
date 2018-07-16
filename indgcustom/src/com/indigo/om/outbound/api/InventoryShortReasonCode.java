package com.indigo.om.outbound.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class InventoryShortReasonCode extends AbstractCustomApi
{
	 private static final String EMPTY_STRING = " ";
	 YFCDocument docOutputgetShipmentList = null;
	 private static final String FOUR = "04";
	 private static final String TWO = "02";
	 private static final String NO ="N";
	 private static final String NO_OF_DAYS = "No_Of_Days";
	 private static final String CANCEL = "CANCEL";
	private static final String ON_HOLD = "ON_HOLD";
	 String sCancellationReasonCode = "01";
	 private static final String INDG_ALERT_RAISE = "Indg_RaiseALert";
	 String sExpirationDays = "30";
	 /**
	  * This method is the invoke point of the service.
	  * 
	  */
	 
	@Override
	public YFCDocument invoke(YFCDocument inXml)
	{
		System.out.println("hfjdhgkjdj"+inXml);
		invokeGetShipmentLineList(inXml);
		
		return inXml;
	}
	private void invokeGetShipmentLineList(YFCDocument inXml)
	{
		YFCIterable<YFCElement> eleShipmentLines  = inXml.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINES)
				.getChildren(XMLLiterals.SHIPMENT_LINE);
		for(YFCElement shipmentLine : eleShipmentLines) {
			System.out.println("fdjbgjdhnkj"+shipmentLine);
			YFCDocument docgetShipmentLineList = YFCDocument.createDocument(XMLLiterals.SHIPMENT_LINE);
		YFCElement eleShipmenLine = docgetShipmentLineList.getDocumentElement();
		eleShipmenLine.setAttribute(XMLLiterals.SHIPMENT_LINE_KEY, shipmentLine.getAttribute(XMLLiterals.SHIPMENT_LINE_KEY));
		System.out.println("kcjgkjfhkjlkj"+docgetShipmentLineList);
		invokeGetInventoryNodeControlList(invokeYantraApi(XMLLiterals.GET_SHIPMENT_LINE_LIST, docgetShipmentLineList,tempgetShipmentLineList()));
		}
	}
		private YFCDocument tempgetShipmentLineList()
		{
			YFCDocument tempgetShipmentLineList = YFCDocument.createDocument(XMLLiterals.SHIPMENT_LINES);
			YFCElement eleShipmentLine = tempgetShipmentLineList.getDocumentElement().createChild(XMLLiterals.SHIPMENT_LINE);
			eleShipmentLine.setAttribute(XMLLiterals.UNIT_OF_MEASURE, EMPTY_STRING);
			eleShipmentLine.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
			eleShipmentLine.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
			eleShipmentLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
			eleShipmentLine.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
			eleShipmentLine.setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
			YFCElement eleOrderLine = eleShipmentLine.createChild(XMLLiterals.ORDER_LINE);
			eleOrderLine.setAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY, EMPTY_STRING);
			eleOrderLine.setAttribute(XMLLiterals.ORDERED_QTY, EMPTY_STRING);
			eleOrderLine.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
			eleOrderLine.setAttribute(XMLLiterals.ORDER_LINE_KEY, EMPTY_STRING);
			System.out.println("fghdgjghjh"+tempgetShipmentLineList);
			return tempgetShipmentLineList;
		}
	
	 private void invokeGetInventoryNodeControlList(YFCDocument docGetShipmentLineList)
	 {
		 System.out.println("dhsuhdasfkfh"+docGetShipmentLineList);
		YFCDocument docGetInvControlList  = invokeYantraApi(XMLLiterals.GET_INVENTORY_NODE_CONTROL_LIST,
				inputGetInvControlList(docGetShipmentLineList));
		System.out.println("bhdhbfjahlrjGSAHGDa"+docGetInvControlList);
		if(docGetInvControlList.getDocumentElement().hasChildNodes())
			
			sCancellationReasonCode = TWO;
		
		else
		{
			sCancellationReasonCode = FOUR;
			invokeManageInventoryNodeControlAPI(docOutputgetShipmentList);	
		
		}
		invokeChangeOrder(docGetShipmentLineList); 
	 }
	 
	 private void invokeManageInventoryNodeControlAPI(YFCDocument docOutputgetShipmentList)
		{
		 System.out.println("dsjdsjff"+docOutputgetShipmentList);
		 YFCElement eleShipementLine = docOutputgetShipmentList.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINE);
			YFCDocument docManageInventoryNodeControl = YFCDocument.createDocument(XMLLiterals.INVENTORY_NODE_CONTROL);
			YFCElement eleInventoryNodeControl = docManageInventoryNodeControl.getDocumentElement();
			String sInventoryPictureTillDate = getInventoryPictureTillDate();
			eleInventoryNodeControl.setAttribute(XMLLiterals.INVENTORY_PICTURE_IN_CORRECT_TILL_DATE, sInventoryPictureTillDate);
			eleInventoryNodeControl.setAttribute(XMLLiterals.INVENTORY_PICTURE_CORRECT, NO);
			eleInventoryNodeControl.setAttribute(XMLLiterals.ITEM_ID, eleShipementLine.getAttribute(XMLLiterals.ITEM_ID));
			eleInventoryNodeControl.setAttribute(XMLLiterals.NODE_CONTROL_TYPE, ON_HOLD);
			eleInventoryNodeControl.setAttribute(XMLLiterals.NODE, eleShipementLine.getChildElement(XMLLiterals.ORDER_LINE)
					.getAttribute(XMLLiterals.SHIPNODE));
			eleInventoryNodeControl.setAttribute(XMLLiterals.ORGANIZATION_CODE, XMLLiterals.INDIGO_CA);
			eleInventoryNodeControl.setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleShipementLine.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
			System.out.println("hcjdgkjhk"+docManageInventoryNodeControl);
			invokeYantraApi(XMLLiterals.MANGE_INVENTORY_NODE_CONTROL, docManageInventoryNodeControl);
			invokeCreateException(eleShipementLine);
		
		}
	 private String getInventoryPictureTillDate()
		{
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Calendar cal = Calendar.getInstance();
				String  sNoOfDays =getProperty(NO_OF_DAYS);
				int noOfDays = Integer.parseInt(sNoOfDays);
				cal.add(Calendar.DAY_OF_MONTH, noOfDays);  
				return sdf.format(cal.getTime()); 
				
		}
	 
	 private YFCDocument inputGetInvControlList(YFCDocument inputGetInvControlList) {
		 System.out.println("hajsajs"+inputGetInvControlList);
		 	YFCElement eleShipementLine = inputGetInvControlList.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINE);
			YFCDocument docgetInvNodeContrlList = YFCDocument.createDocument(XMLLiterals.INVENTORY_NODE_CONTROL);
			YFCElement eleInventoryNodeControl = docgetInvNodeContrlList.getDocumentElement();
			eleInventoryNodeControl.setAttribute(XMLLiterals.ITEM_ID, eleShipementLine.getAttribute(XMLLiterals.ITEM_ID));
			eleInventoryNodeControl.setAttribute(XMLLiterals.NODE, eleShipementLine.getChildElement(XMLLiterals.ORDER_LINE)
					.getAttribute(XMLLiterals.SHIPNODE));
			eleInventoryNodeControl.setAttribute(XMLLiterals.ORGANIZATION_CODE, XMLLiterals.INDIGO_CA);
			eleInventoryNodeControl.setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleShipementLine.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
			System.out.println("hfjdshfjhgd"+docgetInvNodeContrlList);
			return docgetInvNodeContrlList;	 
		 
	 }
	 
	 private void invokeChangeOrder(YFCDocument docGetShipmentLineList)
	 {
		 YFCIterable<YFCElement> eleShipmentLine = docGetShipmentLineList.getDocumentElement().getChildren(XMLLiterals.SHIPMENT_LINE);
		 for(YFCElement shipmentLine : eleShipmentLine) {
		 YFCDocument docOrder = YFCDocument.createDocument(XMLLiterals.ORDER);
		 YFCElement eleOrder = docOrder.getDocumentElement();
		 eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, shipmentLine.getAttribute(XMLLiterals.DOCUMENT_TYPE));
		 eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, shipmentLine.getAttribute(XMLLiterals.INDIGO_CA));
		 eleOrder.setAttribute(XMLLiterals.ORDER_NO, shipmentLine.getAttribute(XMLLiterals.ORDER_NO));
		 YFCElement eleOrderLine = eleOrder.createChild(XMLLiterals.ORDER_LINES).createChild(XMLLiterals.ORDER_LINE);
		 eleOrderLine.setAttribute(XMLLiterals.ACTION, CANCEL);
		 eleOrderLine.setAttribute(XMLLiterals.ORDER_LINE_KEY, shipmentLine.getChildElement(XMLLiterals.ORDER_LINE)
				 .getAttribute(XMLLiterals.ORDER_LINE_KEY));
		 System.out.println("fjhfjhgtk"+docOrder);
		 invokeYantraService(XMLLiterals.CHANGE_ORDER_API, docOrder);
		 }

	 }
	 
	 private void invokeCreateException(YFCElement eleShipementLine)
	 {
		 
		 YFCDocument docCreateException = YFCDocument.createDocument(XMLLiterals.INBOX);
		 YFCElement eleInbox = docCreateException.getDocumentElement();
		 eleInbox.setAttribute(XMLLiterals.ITEM_ID, eleShipementLine.getAttribute(XMLLiterals.ITEM_ID));
		 eleInbox.setAttribute(XMLLiterals.SHIPNODE, eleShipementLine.getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.SHIPNODE));
		 eleInbox.setAttribute(XMLLiterals.EXCEPTION_TYPE, XMLLiterals.INVENTORY_DIRTY);
		 eleInbox.setAttribute(XMLLiterals.EXPIRATION_DAYS, sExpirationDays);
		 YFCDocument docAlert = invokeYantraApi(XMLLiterals.CREATE_EXCEPTION, docCreateException);
		 System.out.println("dbchsbgdjtn"+docAlert);
		 invokeYantraService(INDG_ALERT_RAISE, docAlert);
		 
	 }
	 
	 
}