package com.indigo.om.outbound.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class IndgCancelOrderInBatchPick extends AbstractCustomApi {
	 private static final String EMPTY_STRING = " ";
	 private static final String YES = "Y";
	 private static final String FOUR = "04";
	 private static final String ONE = "01";
	 private static final String TWO = "02";
	 private static final String NO = "N";
	 private static final String NO_OF_DAYS = "NO_OF_DAYS";
	 private static final String ON_HOLD = "ON_HOLD";
	 private static final String ORDER_TYPE = "ZOCC";
	 private static final String SUB_LINE_NO = "1";
	 private static final String INVENTORY_DIRTY_QUEUE = "INVENTORY_DIRTY_QUEUE";
	 private static final String INDG_CHANGESHIPMENT = "Indg_ChangeShipment";
	 private static final String CANCEL = "CANCEL";
	 private static final String SHORTAGE = "shortage";
	 private static final String DAMAGED = "damaged";
	 private static final String MARK_LINE_AS_SHORTAGE="MarkLineAsShortage";
	 private static final String ITEM_ID = "ItemId";
	 private static final String SHORTAGE_REASON = "ShortageReason";
	 String sExpirationDays = "30";
	 String sCancellationReasonCode;

	
	public YFCDocument invoke(YFCDocument inXml) {
		
		
		return inXml;
	}
	
	public void invokeChangeOrder(YFCDocument docInput)
	{
		System.out.println("hcdjhgjh"+docInput);
		YFCIterable<YFCElement> eleShipmentLine = docInput.getDocumentElement().getChildElement(XMLLiterals.ITEM)
				.getChildElement(XMLLiterals.SHIPMENT_LINES).getChildren(XMLLiterals.SHIPMENT_LINE);
		for(YFCElement shipmentLine : eleShipmentLine)
		{
			System.out.println("jdbgjnhkmhn"+shipmentLine);
			handleCancellation(shipmentLine);
		}
	}
	
	public void handleCancellation(YFCElement shipmentLine)
	{
		System.out.println("hcjshcfjhkjh"+shipmentLine);
		YFCDocument docgetShipmentLineList = YFCDocument.createDocument(XMLLiterals.SHIPMENT_LINE);
		YFCElement eleShipmenLine = docgetShipmentLineList.getDocumentElement();
		eleShipmenLine.setAttribute(XMLLiterals.SHIPMENT_LINE_KEY, shipmentLine.getAttribute(XMLLiterals.SHIPMENT_LINE_KEY));
		YFCDocument docGetShipmentLineListOutput = invokeYantraApi(XMLLiterals.GET_SHIPMENT_LINE_LIST, docgetShipmentLineList, tempgetShipmentLineList());
		System.out.println("jdnjnbknbk"+docGetShipmentLineListOutput);
		invokeChangeOrderAPI(docGetShipmentLineListOutput);
	}
	 private void invokeChangeOrderAPI(YFCDocument docGetShipmentLineList) {
		 System.out.println("fhbjsdhgjahnkjhn"+docGetShipmentLineList);
		 YFCIterable<YFCElement> eleShipmentLine = docGetShipmentLineList.getDocumentElement().getChildren(XMLLiterals.SHIPMENT_LINE);
		 for(YFCElement shipmentLine : eleShipmentLine) {
		 YFCDocument docOrder = YFCDocument.createDocument(XMLLiterals.ORDER);
		 YFCElement eleOrder = docOrder.getDocumentElement();
		 eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, shipmentLine.getAttribute(XMLLiterals.DOCUMENT_TYPE));
		 eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, XMLLiterals.INDIGO_CA);
		 eleOrder.setAttribute(XMLLiterals.ORDER_NO, shipmentLine.getAttribute(XMLLiterals.ORDER_NO));
		 eleOrder.setAttribute(XMLLiterals.ORDER_TYPE, ORDER_TYPE);
		 eleOrder.setAttribute(XMLLiterals.MODIFICATION_REASON_CODE, sCancellationReasonCode);
		 YFCElement eleOrderLine = eleOrder.createChild(XMLLiterals.ORDER_LINES).createChild(XMLLiterals.ORDER_LINE);
		 eleOrderLine.setAttribute(XMLLiterals.ACTION, CANCEL);
		 double sOrderedQty= Double.parseDouble(shipmentLine.getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY))
				 - Double.parseDouble(shipmentLine.getAttribute(XMLLiterals.SHORTAGE_QTY));
		 String sQty = String.valueOf(sOrderedQty);
		 eleOrderLine.setAttribute(XMLLiterals.ORDERED_QTY,sQty);
		 eleOrderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, shipmentLine.getAttribute(XMLLiterals.PRIME_LINE_NO));
		 eleOrderLine.setAttribute(XMLLiterals.SHIPNODE, shipmentLine.getChildElement(XMLLiterals.ORDER_LINE)
				 .getAttribute(XMLLiterals.SHIPNODE));
		 eleOrderLine.setAttribute(XMLLiterals.SUB_LINE_NO,SUB_LINE_NO);
		 YFCElement eleItem = eleOrderLine.createChild(XMLLiterals.ITEM);
		 eleItem.setAttribute(XMLLiterals.ITEM_ID, shipmentLine.getAttribute(XMLLiterals.ITEM_ID));
		 System.out.println("bhfbdjjngkh"+docOrder);
		 invokeYantraApi(XMLLiterals.CHANGE_ORDER_API, docOrder);
		 }
	 }
	
	public void handleNodeInventory(YFCDocument docInput)
	  {
		System.out.println("hfcbjdkhgkjhn"+docInput);
		  String sCancellationReasonCode;
		  YFCElement eleItem = docInput.getDocumentElement().getChildElement(XMLLiterals.ITEM);
		  if(eleItem.getAttribute(SHORTAGE_REASON).equals(SHORTAGE)) {
			  YFCDocument docGetInvControlList = invokeGetInventoryNodeControlList(eleItem);
			  if(docGetInvControlList.getDocumentElement().hasChildNodes())
					sCancellationReasonCode = TWO;
				else
				{
					sCancellationReasonCode = FOUR;
					invokeManageInventoryNodeControlAPI(eleItem);	
				}
			}
			else if(eleItem.getAttribute(XMLLiterals.SHORTAGE_REASON_CODE).equals(DAMAGED))
			{
				sCancellationReasonCode = ONE;
			}
		  }
	
	private YFCDocument tempgetShipmentLineList() {
		YFCDocument tempgetShipmentLineList = YFCDocument.createDocument(XMLLiterals.SHIPMENT_LINES);
		YFCElement eleShipmentLine = tempgetShipmentLineList.getDocumentElement().createChild(XMLLiterals.SHIPMENT_LINE);
		eleShipmentLine.setAttribute(XMLLiterals.UNIT_OF_MEASURE, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.SHORTAGE_QTY, EMPTY_STRING);
		YFCElement eleOrderLine = eleShipmentLine.createChild(XMLLiterals.ORDER_LINE);
		eleOrderLine.setAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.ORDERED_QTY, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.ORDER_LINE_KEY, EMPTY_STRING);
		System.out.println("hndjkhfkhh"+tempgetShipmentLineList);
		return tempgetShipmentLineList;
	}
		  
	  
	  
	  public YFCDocument invokeGetInventoryNodeControlList(YFCElement eleItem)
	  {
			 YFCDocument docgetInvNodeContrlList = YFCDocument.createDocument(XMLLiterals.INVENTORY_NODE_CONTROL);
			 YFCElement eleInventoryNodeControl = docgetInvNodeContrlList.getDocumentElement();
			 eleInventoryNodeControl.setAttribute(XMLLiterals.INVENTORY_PICTURE_CORRECT, YES);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.ITEM_ID, eleItem.getAttribute(XMLLiterals.ITEM_ID));
			 eleInventoryNodeControl.setAttribute(XMLLiterals.NODE, eleItem.getAttribute(XMLLiterals.ORGANIZATION_CODE));
			 eleInventoryNodeControl.setAttribute(XMLLiterals.ORGANIZATION_CODE, XMLLiterals.INDIGO_CA);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleItem.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
			 System.out.println("sjnfkjak"+docgetInvNodeContrlList);
			 return docgetInvNodeContrlList;	 
	  }
	  
	  public void invokeManageInventoryNodeControlAPI(YFCElement eleItem) {
			 YFCDocument docManageInventoryNodeControl = YFCDocument.createDocument(XMLLiterals.INVENTORY_NODE_CONTROL);
			 YFCElement eleInventoryNodeControl = docManageInventoryNodeControl.getDocumentElement();
			 String sInventoryPictureTillDate = getInventoryPictureTillDate();
			 eleInventoryNodeControl.setAttribute(XMLLiterals.INVENTORY_PICTURE_IN_CORRECT_TILL_DATE, sInventoryPictureTillDate);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.INVENTORY_PICTURE_CORRECT, NO);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.ITEM_ID, eleItem.getAttribute(XMLLiterals.ITEM_ID));
			 eleInventoryNodeControl.setAttribute(XMLLiterals.NODE_CONTROL_TYPE, ON_HOLD);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.NODE, eleItem.getChildElement(XMLLiterals.ORDER_LINE)
					 .getAttribute(XMLLiterals.SHIPNODE));
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			 Calendar cal = Calendar.getInstance();
			 String date =  sdf.format(cal.getTime()); 
			 String sStartDate =  date.substring(0,10)+"T"+date.substring(11,23)+"Z";
			 eleInventoryNodeControl.setAttribute(XMLLiterals.START_DATE, sStartDate);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.ORGANIZATION_CODE, XMLLiterals.INDIGO_CA);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleItem.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
			 System.out.println("fjskdgakhj"+docManageInventoryNodeControl);
			 invokeYantraApi(XMLLiterals.MANGE_INVENTORY_NODE_CONTROL, docManageInventoryNodeControl);
			 invokeCreateException(eleItem);
		  
	  }
	  
	  public String getInventoryPictureTillDate() {
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			 Calendar cal = Calendar.getInstance();
			 String  sNoOfDays = getProperty(NO_OF_DAYS);
			 int noOfDays = Integer.parseInt(sNoOfDays);
			 cal.add(Calendar.DAY_OF_MONTH, noOfDays);  
			 String date =  sdf.format(cal.getTime()); 
			 return date.substring(0,10)+"T"+date.substring(11,23)+"Z";
		 }
	  
	  public void invokeCreateException(YFCElement eleItem) {
			 YFCDocument docCreateException = YFCDocument.createDocument(XMLLiterals.INBOX);
			 YFCElement eleInbox = docCreateException.getDocumentElement();
			 String sShipNode = eleItem.getAttribute(XMLLiterals.ORGANIZATION_CODE);
			 eleInbox.setAttribute(XMLLiterals.DETAIL_DESCRIPTION,eleItem.getAttribute(XMLLiterals.ITEM_ID));
			 eleInbox.setAttribute(XMLLiterals.ENTERPRISE_KEY, XMLLiterals.INDIGO_CA);
			 eleInbox.setAttribute(ITEM_ID, eleItem.getAttribute(XMLLiterals.ITEM_ID));
			 eleInbox.setAttribute(XMLLiterals.SHIPNODE_KEY, sShipNode);
			 eleInbox.setAttribute(XMLLiterals.EXCEPTION_TYPE, XMLLiterals.INVENTORY_DIRTY);
			 eleInbox.setAttribute(XMLLiterals.EXPIRATION_DAYS, sExpirationDays);
			 eleInbox.setAttribute(XMLLiterals.QUEUE_ID, INVENTORY_DIRTY_QUEUE);
			 System.out.println("jdigkjg"+docCreateException);
			 invokeYantraApi(XMLLiterals.CREATE_EXCEPTION, docCreateException);
		 }
}


