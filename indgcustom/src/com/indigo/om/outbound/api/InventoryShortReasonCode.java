package com.indigo.om.outbound.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
* 
* @author BSG170
*
* Custom Code consumes the data that is is being sent from UI. If the ShortageReasonCodeActual is damaged the  
* sCancellationReasonCode is set to "01" else getInventoryNodeControlList API is invoked to check
* whether that particular combination of item and node is marked dirty if it is marked dirty sCancellationReasonCode 
* is to "02" else manageInventoryNodeControlList API is invoked and sCancellationReasonCode is set to "04"
* and cancel the order based on shortage quantity.
* 
*/

public class InventoryShortReasonCode extends AbstractCustomApi {
	
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
	 String sExpirationDays = "30";
	 String sCancellationReasonCode;
	 YFCDocument docGetShptLineListOutput = null;
	 private static final String ADJUSTMENT_VAL = "ADJUSTMENT";
	 private static final String ORGANIZATION_CODE_VAL = "Indigo_CA";
	 private static final String SUPPLY_TYPE = "ONHAND";
	 private static final String UOM = "EACH";
	 
	 /**
	  * This method is the invoke point of the service.
	  * 
	  */
	 
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCDocument outXml = invokeYantraService(INDG_CHANGESHIPMENT, inXml);
		handleOrderPickShortages(inXml);
		adjustInvForShortageQty(inXml);
		return outXml;
	}
	
	/**
	 * This method forms the input to getShipmentLineList API
	 * @param inXml
	 */
	
	public void handleOrderPickShortages(YFCDocument inXml) {
		YFCElement eleShipment = inXml.getDocumentElement();
		if((eleShipment.getAttribute(XMLLiterals.SHORTAGE_REASON_CODE)!=null) && (eleShipment.getAttribute(XMLLiterals.SHORTAGE_REASON_CODE).equals(DAMAGED) ||
				eleShipment.getAttribute(XMLLiterals.SHORTAGE_REASON_CODE).equals(SHORTAGE))) {
				YFCIterable<YFCElement> eleShipmentLines = eleShipment.getChildElement(XMLLiterals.SHIPMENT_LINES)
					.getChildren(XMLLiterals.SHIPMENT_LINE);
				for(YFCElement shipmentLine : eleShipmentLines) {
					invokegetShipmentLineList(shipmentLine, inXml);
			}
		}
	}
	
	/**
	 * 
	 * @param shipmentLine
	 * @param inXml
	 */
	
	public void invokegetShipmentLineList(YFCElement shipmentLine, YFCDocument inXml ) {
		YFCDocument docgetShipmentLineList = YFCDocument.createDocument(XMLLiterals.SHIPMENT_LINE);
		YFCElement eleShipmenLine = docgetShipmentLineList.getDocumentElement();
		eleShipmenLine.setAttribute(XMLLiterals.SHIPMENT_LINE_KEY, shipmentLine.getAttribute(XMLLiterals.SHIPMENT_LINE_KEY));
		docGetShptLineListOutput = invokeYantraApi(XMLLiterals.GET_SHIPMENT_LINE_LIST, docgetShipmentLineList,tempgetShipmentLineList());
		invokeGetInventoryNodeControlList(inXml);
	}
	
	/**
	 * This method forms the template for getShipmentLineList API
	 * @return
	 */
	
	private YFCDocument tempgetShipmentLineList() {
		YFCDocument tempgetShipmentLineList = YFCDocument.createDocument(XMLLiterals.SHIPMENT_LINES);
		YFCElement eleShipmentLine = tempgetShipmentLineList.getDocumentElement().createChild(XMLLiterals.SHIPMENT_LINE);
		eleShipmentLine.setAttribute(XMLLiterals.UNIT_OF_MEASURE, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.SHORTAGE_QTY, EMPTY_STRING);
		YFCElement eleOrderLine = eleShipmentLine.createChild(XMLLiterals.ORDER_LINE);
		eleOrderLine.setAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.ORDERED_QTY, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.ORDER_LINE_KEY, EMPTY_STRING);
		return tempgetShipmentLineList;
	}
	
	/**
	 * This method invokes getInventoryNodeControlList and based on the output of the API 
	 * the sCancellationReasonCode is set and invokeManageInventoryNodeControlAPI is invoked
	 * if the dirty flag is not set.
	 * It invokes changeOrder API irrespective of getInventoryNodeControlList API
	 * 
	 * @param docGetShipmentLineList 
	 * @param inXml
	 */
	
	private void invokeGetInventoryNodeControlList(YFCDocument inXml) {
		YFCElement eleShipment = inXml.getDocumentElement();
		if(eleShipment.getAttribute(XMLLiterals.SHORTAGE_REASON_CODE).equals(SHORTAGE)) {
			YFCDocument docInputGetInvControlList = inputGetInvControlList(docGetShptLineListOutput);
			YFCDocument docGetInvControlList = invokeYantraApi(XMLLiterals.GET_INVENTORY_NODE_CONTROL_LIST, docInputGetInvControlList);
			if(docGetInvControlList.getDocumentElement().hasChildNodes())
				sCancellationReasonCode = TWO;
			else
			{
				sCancellationReasonCode = FOUR;
				invokeManageInventoryNodeControlAPI();	
			}
		}
		else if(eleShipment.getAttribute(XMLLiterals.SHORTAGE_REASON_CODE).equals(DAMAGED))
		{
			sCancellationReasonCode = ONE;
		}
		invokeChangeOrder(); 
	}
	
	/**
	 * This method invokes invokeManageInventoryNodeControl API
	 * @param docGetShipmentLineList
	 */
	 
	 private void invokeManageInventoryNodeControlAPI() {
		 YFCElement eleShipementLine = docGetShptLineListOutput.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINE);
		 YFCDocument docManageInventoryNodeControl = YFCDocument.createDocument(XMLLiterals.INVENTORY_NODE_CONTROL);
		 YFCElement eleInventoryNodeControl = docManageInventoryNodeControl.getDocumentElement();
		 String sInventoryPictureTillDate = getInventoryPictureTillDate();
		 eleInventoryNodeControl.setAttribute(XMLLiterals.INVENTORY_PICTURE_IN_CORRECT_TILL_DATE, sInventoryPictureTillDate);
		 eleInventoryNodeControl.setAttribute(XMLLiterals.INVENTORY_PICTURE_CORRECT, NO);
		 eleInventoryNodeControl.setAttribute(XMLLiterals.ITEM_ID, eleShipementLine.getAttribute(XMLLiterals.ITEM_ID));
		 eleInventoryNodeControl.setAttribute(XMLLiterals.NODE_CONTROL_TYPE, ON_HOLD);
		 eleInventoryNodeControl.setAttribute(XMLLiterals.NODE, eleShipementLine.getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.SHIPNODE));
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		 Calendar cal = Calendar.getInstance();
		 String date = sdf.format(cal.getTime()); 
		 String sStartDate = date.substring(0,10)+"T"+date.substring(11,23)+"Z";
		 eleInventoryNodeControl.setAttribute(XMLLiterals.START_DATE, sStartDate);
		 eleInventoryNodeControl.setAttribute(XMLLiterals.ORGANIZATION_CODE, XMLLiterals.INDIGO_CA);
		 eleInventoryNodeControl.setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleShipementLine.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
		 invokeYantraApi(XMLLiterals.MANGE_INVENTORY_NODE_CONTROL, docManageInventoryNodeControl);
		 invokeCreateException(eleShipementLine);
	}
	 
	 /**
	  * This method calculates the getInventoryPictureTillDate by adding
	  * no of days fetched from service argument
	  * @return
	  */
	 
	 private String getInventoryPictureTillDate() {
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		 Calendar cal = Calendar.getInstance();
		 String sNoOfDays = getProperty(NO_OF_DAYS);
		 int noOfDays = Integer.parseInt(sNoOfDays);
		 cal.add(Calendar.DAY_OF_MONTH, noOfDays);  
		 String date = sdf.format(cal.getTime()); 
		 return date.substring(0,10)+"T"+date.substring(11,23)+"Z";
	 }
	 
	 /**
	  * This method forms input document for getInventoryNodeControlList API
	  * @param inputGetInvControlList
	  * @return
	  */
	 
	 private YFCDocument inputGetInvControlList(YFCDocument inputGetInvControlList) {
		 YFCElement eleShipementLine = inputGetInvControlList.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINE);
		 YFCDocument docgetInvNodeContrlList = YFCDocument.createDocument(XMLLiterals.INVENTORY_NODE_CONTROL);
		 YFCElement eleInventoryNodeControl = docgetInvNodeContrlList.getDocumentElement();
		 eleInventoryNodeControl.setAttribute(XMLLiterals.INVENTORY_PICTURE_CORRECT, YES);
		 eleInventoryNodeControl.setAttribute(XMLLiterals.ITEM_ID, eleShipementLine.getAttribute(XMLLiterals.ITEM_ID));
		 eleInventoryNodeControl.setAttribute(XMLLiterals.NODE, eleShipementLine.getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.SHIPNODE));
		 eleInventoryNodeControl.setAttribute(XMLLiterals.ORGANIZATION_CODE, XMLLiterals.INDIGO_CA);
		 eleInventoryNodeControl.setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleShipementLine.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
		 return docgetInvNodeContrlList;	 
	 }
	 
	 /**
	  * This method forms the input and invokes changeOrder API
	  * @param docGetShipmentLineList
	  */
	 
	 private void invokeChangeOrder() {
		 YFCIterable<YFCElement> eleShipmentLine = docGetShptLineListOutput.getDocumentElement().getChildren(XMLLiterals.SHIPMENT_LINE);
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
		 eleOrderLine.setAttribute(XMLLiterals.SHIPNODE, shipmentLine.getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.SHIPNODE));
		 eleOrderLine.setAttribute(XMLLiterals.SUB_LINE_NO,SUB_LINE_NO);
		 YFCElement eleItem = eleOrderLine.createChild(XMLLiterals.ITEM);
		 eleItem.setAttribute(XMLLiterals.ITEM_ID, shipmentLine.getAttribute(XMLLiterals.ITEM_ID));
		 invokeYantraApi(XMLLiterals.CHANGE_ORDER_API, docOrder);
		 }
	 }
	 
	 /**
	  * This method forms input for createException API
	  * @param eleShipementLine
	  */
	 
	 private void invokeCreateException(YFCElement eleShipementLine) {
		 YFCDocument docCreateException = YFCDocument.createDocument(XMLLiterals.INBOX);
		 YFCElement eleInbox = docCreateException.getDocumentElement();
		 String sShipNode = eleShipementLine.getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.SHIPNODE);
		 eleInbox.setAttribute(XMLLiterals.DETAIL_DESCRIPTION,eleShipementLine.getAttribute(XMLLiterals.ITEM_ID));
		 eleInbox.setAttribute(XMLLiterals.ENTERPRISE_KEY, XMLLiterals.INDIGO_CA);
		 eleInbox.setAttribute(XMLLiterals.ITEMID, eleShipementLine.getAttribute(XMLLiterals.ITEM_ID));
		 eleInbox.setAttribute(XMLLiterals.SHIPNODE_KEY, sShipNode);
		 eleInbox.setAttribute(XMLLiterals.EXCEPTION_TYPE, XMLLiterals.INVENTORY_DIRTY);
		 eleInbox.setAttribute(XMLLiterals.EXPIRATION_DAYS, sExpirationDays);
		 eleInbox.setAttribute(XMLLiterals.QUEUE_ID, INVENTORY_DIRTY_QUEUE);
		 invokeYantraApi(XMLLiterals.CREATE_EXCEPTION, docCreateException);
	 }
	 
	 /**
	  * This method forms the input for adjustInventory API for the
	  * shortage quantity of orderPick input XML.
	  * @param inXml
	  */
	 
	 private void adjustInvForShortageQty(YFCDocument inXml) {
		 YFCDocument docAdjustInv = YFCDocument.createDocument(XMLLiterals.ITEMS);
		 YFCElement eleItem = docAdjustInv.getDocumentElement().createChild(XMLLiterals.ITEM);
		 eleItem.setAttribute(XMLLiterals.ADJUSTMENT_TYPE, ADJUSTMENT_VAL);
		 eleItem.setAttribute(XMLLiterals.ITEM_ID, docGetShptLineListOutput.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINE).getAttribute(XMLLiterals.ITEM_ID));
		 eleItem.setAttribute(XMLLiterals.ORGANIZATION_CODE, ORGANIZATION_CODE_VAL);
		 eleItem.setAttribute(XMLLiterals.QUANTITY, inXml.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINES).getChildElement(XMLLiterals.SHIPMENT_LINE).getAttribute(XMLLiterals.SHORTAGE_QTY));
		 eleItem.setAttribute(XMLLiterals.SHIP_NODE, docGetShptLineListOutput.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINE).getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.SHIP_NODE));
		 eleItem.setAttribute(XMLLiterals.SUPPLY_TYPE, SUPPLY_TYPE);
		 eleItem.setAttribute(XMLLiterals.UNIT_OF_MEASURE, UOM);
		 invokeYantraApi(XMLLiterals.ADJUST_INVENTORY_API, docAdjustInv);
	 }
}