package com.indigo.om.outbound.api;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.w3c.dom.Document;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.utils.ExceptionUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;

public class IndgCancelOrderInBatchPick {
	 private static final String EMPTY_STRING = " ";
	 private static final String YES = "Y";
	 private static final String FOUR = "04";
	 private static final String ONE = "01";
	 private static final String TWO = "02";
	 private static final String NO = "N";
	 private static final String ON_HOLD = "ON_HOLD";
	 private static final String ORDER_TYPE = "ZOCC";
	 private static final String SUB_LINE_NO = "1";
	 private static final String INVENTORY_DIRTY_QUEUE = "INVENTORY_DIRTY_QUEUE";
	 private static final String CANCEL = "CANCEL";
	 private static final String SHORTAGE = "shortage";
	 private static final String DAMAGED = "damaged";
	 private static final String ITEM_ID = "ItemId";
	 private static final String SHORTAGE_REASON = "ShortageReason";
	 private static final String ADJUSTMENT_VAL = "ADJUSTMENT";
	 private static final String ORGANIZATION_CODE_VAL = "Indigo_CA";
	 private static final String SUPPLY_TYPE = "ONHAND";
	 private static final String UOM = "EACH";
	 private YFSEnvironment env;
	 String sExpirationDays = "30";
	 String sCancellationReasonCode;

	

	 
	public IndgCancelOrderInBatchPick (YFSEnvironment env) {
		
		this.env = env;
		
	} 
	
	public void invokeChangeOrder(YFCDocument docInput)
	{
		System.out.println("hcdjhgjh"+docInput);
		YFCIterable<YFCElement> eleShipmentLine = docInput.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINES)
				.getChildren(XMLLiterals.SHIPMENT_LINE);
		for(YFCElement shipmentLine : eleShipmentLine)
		{
			System.out.println("jdbgjnhkmhn"+shipmentLine);
			handleCancellation(shipmentLine);
		  }
		String sShortageReason= docInput.getDocumentElement().getChildElement(XMLLiterals.ITEM)
				.getAttribute(XMLLiterals.SHORTAGE_REASON);
		System.out.println("hsjheg"+sShortageReason);
		if(sShortageReason.equals(SHORTAGE)) {
			adjustInvForShortageQty(docInput);
		}
	}
	
	public  void adjustInvForShortageQty(YFCDocument inXml) {
		System.out.println("hudehigtjheio"+inXml);
		String minus = "-";
		YFCElement eleItemInput = inXml.getDocumentElement().getChildElement(XMLLiterals.ITEM);
		String quantity = inXml.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINES).
				getChildElement(XMLLiterals.SHIPMENT_LINE).getAttribute(XMLLiterals.BACKROOM_PICK_QUANTITY);
		YFCDocument docAdjustInv = YFCDocument.createDocument(XMLLiterals.ITEMS);
		YFCElement eleItem = docAdjustInv.getDocumentElement().createChild(XMLLiterals.ITEM);
		eleItem.setAttribute(XMLLiterals.ADJUSTMENT_TYPE, ADJUSTMENT_VAL);
		eleItem.setAttribute(XMLLiterals.ITEM_ID, eleItemInput.getAttribute(XMLLiterals.ITEM_ID));
		eleItem.setAttribute(XMLLiterals.ORGANIZATION_CODE,ORGANIZATION_CODE_VAL);
		eleItem.setAttribute(XMLLiterals.QUANTITY, minus.concat(quantity));
		eleItem.setAttribute(XMLLiterals.SHIP_NODE,eleItemInput.getAttribute(XMLLiterals.ORGANIZATION_CODE));
		eleItem.setAttribute(XMLLiterals.UNIT_OF_MEASURE, UOM);
		eleItem.setAttribute(XMLLiterals.SUPPLY_TYPE, SUPPLY_TYPE);
		System.out.println("jdshjgnhkjlkj"+docAdjustInv);
		invokeAPI(docAdjustInv,XMLLiterals.ADJUST_INVENTORY_API);
		
	}
	
	public void handleCancellation(YFCElement shipmentLine)
	{
		System.out.println("hcjshcfjhkjh"+shipmentLine);
		YFCDocument docgetShipmentLineList = YFCDocument.createDocument(XMLLiterals.SHIPMENT_LINE);
		YFCElement eleShipmenLine = docgetShipmentLineList.getDocumentElement();
		eleShipmenLine.setAttribute(XMLLiterals.SHIPMENT_LINE_KEY, shipmentLine.getAttribute(XMLLiterals.SHIPMENT_LINE_KEY));
	    env.setApiTemplate(XMLLiterals.GET_SHIPMENT_LINE_LIST, tempgetShipmentLineList().getDocument());
	    System.out.println(docgetShipmentLineList + "aaaaaaaa");
	    YFCDocument docGetShipmentLineListOutput = invokeAPI(docgetShipmentLineList, XMLLiterals.GET_SHIPMENT_LINE_LIST);
		env.clearApiTemplates();
		System.out.println("jdnjnbknbk"+docGetShipmentLineListOutput);
		invokeChangeOrderAPI(docGetShipmentLineListOutput);
	}
	 public void invokeChangeOrderAPI(YFCDocument docGetShipmentLineList) {
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
		 System.out.println(docOrder + "bbbbbbbbb");
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
		 invokeAPI(docOrder, XMLLiterals.CHANGE_ORDER_API);
		 }
	 }
	
	public void handleNodeInventory(YFCDocument docInput)
	  {
		System.out.println("hfcbjdkhgkjhn"+docInput);
		 
		  YFCElement eleItem = docInput.getDocumentElement().getChildElement(XMLLiterals.ITEM);
		  if(eleItem.getAttribute(SHORTAGE_REASON).equals(SHORTAGE)) {
			  YFCDocument docGetInvControlList = invokeGetInventoryNodeControlList(eleItem);
			  System.out.println(docGetInvControlList + "cccccccccc");
			  if((docGetInvControlList!=null)  && (docGetInvControlList.getDocumentElement().hasChildNodes()))
					sCancellationReasonCode = TWO;
				else
				{
					sCancellationReasonCode = FOUR;
					invokeManageInventoryNodeControlAPI(eleItem);	
				}
			} 
			else if(eleItem.getAttribute(SHORTAGE_REASON).equals(DAMAGED))
			{
				sCancellationReasonCode = ONE;
			}
		  System.out.println(sCancellationReasonCode + "dddddddddd");
		  }
	
	public YFCDocument tempgetShipmentLineList() {
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
			 System.out.println("plwqqqjfiret"+eleInventoryNodeControl);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.INVENTORY_PICTURE_CORRECT, YES);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.ITEM_ID, eleItem.getAttribute(XMLLiterals.ITEM_ID));
			 eleInventoryNodeControl.setAttribute(XMLLiterals.NODE, eleItem.getAttribute(XMLLiterals.ORGANIZATION_CODE));
			 eleInventoryNodeControl.setAttribute(XMLLiterals.ORGANIZATION_CODE, XMLLiterals.INDIGO_CA);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleItem.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
			 System.out.println("sjnfkjak"+docgetInvNodeContrlList);
			 return invokeAPI(docgetInvNodeContrlList, XMLLiterals.GET_INVENTORY_NODE_CONTROL_LIST);	    
	  }
	  
	  public void invokeManageInventoryNodeControlAPI(YFCElement eleItem) {
			 YFCDocument docManageInventoryNodeControl = YFCDocument.createDocument(XMLLiterals.INVENTORY_NODE_CONTROL);
			 YFCElement eleInventoryNodeControl = docManageInventoryNodeControl.getDocumentElement();
			 String sInventoryPictureTillDate = getInventoryPictureTillDate();
			 System.out.println(sInventoryPictureTillDate + "eeeeeeeeee");
			 eleInventoryNodeControl.setAttribute(XMLLiterals.INVENTORY_PICTURE_IN_CORRECT_TILL_DATE, sInventoryPictureTillDate);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.INVENTORY_PICTURE_CORRECT, NO);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.ITEM_ID, eleItem.getAttribute(XMLLiterals.ITEM_ID));
			 eleInventoryNodeControl.setAttribute(XMLLiterals.NODE_CONTROL_TYPE, ON_HOLD);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.NODE, eleItem.getAttribute(XMLLiterals.ORGANIZATION_CODE));
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			 Calendar cal = Calendar.getInstance();
			 String date =  sdf.format(cal.getTime()); 
			 String sStartDate =  date.substring(0,10)+"T"+date.substring(11,23)+"Z";
			 eleInventoryNodeControl.setAttribute(XMLLiterals.START_DATE, sStartDate);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.ORGANIZATION_CODE, XMLLiterals.INDIGO_CA);
			 eleInventoryNodeControl.setAttribute(XMLLiterals.UNIT_OF_MEASURE, eleItem.getAttribute(XMLLiterals.UNIT_OF_MEASURE));
			 System.out.println("fjskdgakhj"+docManageInventoryNodeControl);
			 invokeAPI(docManageInventoryNodeControl, XMLLiterals.MANGE_INVENTORY_NODE_CONTROL);
			 invokeCreateException(eleItem);
		  
	  }
	  
	  public String getInventoryPictureTillDate() {
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			 Calendar cal = Calendar.getInstance();
			 String  sNoOfDays = "15";
			 int noOfDays = Integer.parseInt(sNoOfDays);
			 cal.add(Calendar.DAY_OF_MONTH, noOfDays);  
			 String date =  sdf.format(cal.getTime());
			 System.out.println(date.substring(0,10)+"T"+date.substring(11,23)+"Z" + "ffffffffff");
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
			 invokeAPI(docCreateException,XMLLiterals.CREATE_EXCEPTION);
		 }
	  
	  public YFCDocument invokeAPI(YFCDocument inputDoc, String sAPIName)
	  {
		  System.out.println("hjuhdfudsh"+inputDoc);
		  Document docOutput;
			 try {
				 docOutput = YIFClientFactory.getInstance().getApi().invoke(env,  sAPIName, inputDoc.getDocument());
				} catch (YIFClientCreationException yifCCEx) {
					throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_SERVICE_CC_EXP, yifCCEx);
				}catch (RemoteException remexp) {
					throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_SERVICE_REMOTE_EXP, remexp);
				}
			 YFCDocument docOut = YFCDocument.getDocumentFor(docOutput);
			 System.out.println(docOut + "fffffffff");
			 return docOut;
	  }
}


