package com.indigo.om.outbound.api;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.ibm.icu.text.SimpleDateFormat;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.date.YTimestamp;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * @author BSG170
 * 
 * Custom code to update sequence no for the messages that are being sent by sterling
 * It also updates the time field attributes with the milliseconds and timezone 
 *
 */

public class IndgSequencingNo extends AbstractCustomApi {
	
	private static final String EMPTY_STRING = "";
	private static final long ONE = 1;
	private static final String SAP001 = "SAP001";
	private static final CharSequence SAP = "SAP";
	private static final String INDG_CHANGE_INDG_MSG_SEQ_NO="INDG_changeINDGMsgSeqNo";
	private static final String INDG_CREATE_INDG_MSG_SEQ_NO="INDG_createINDGMsgSeqNo";
	private static final String INDG_GET_INDG_MSG_SEQ_NO_LIST="INDG_getINDGMsgSeqNoList";
	Map<String, String> map=new HashMap<>();
	private static final String XPATH_DATE_TYPES= "xpath.date.types";
	private static final long TWO = 2;
	
	/**
	   * @throws ParseException 
	   * This is the invoke point of the Service
	   * @throws  
	   * 
	   */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) { 
		if(inXml.getDocumentElement().getAttribute(XMLLiterals.MESSAGE_TYPE_ID).equals(SAP001))
		{
			addDateTypes();
			updateMilliSeconds(inXml);
		}else{
		YFCDocument docMsg=updateMsgSeqNo(inXml);
		try {
			addMsgSeqNo(docMsg,inXml);
		}
		catch(Exception e) {
			throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_MISSING_VALUE, e);
		}
		addDateTypes();
		updateMilliSeconds(inXml);
		}
		return inXml;
	}
	  
	/**
	 * This method adds sequence no to the input file
	 * @param docMsg
	 * @param inXml
	 */
	
	private void addMsgSeqNo(YFCDocument docMsg,YFCDocument inXml) {
		YFCElement eleOrderMessage=inXml.getDocumentElement();
		YFCElement eleINDGMsgSeqNo=docMsg.getDocumentElement();
		String sSAPMsgSeqNo=eleINDGMsgSeqNo.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO);
		if(!XmlUtils.isVoid(sSAPMsgSeqNo) && eleINDGMsgSeqNo.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID).contains(SAP) )
		{
			eleOrderMessage.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO, sSAPMsgSeqNo);
		}	
		else
		{
			eleOrderMessage.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO, eleINDGMsgSeqNo.getAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO));
		}
	}
	 /**
	  * This method stores the different types of date fields present in incoming messages
	  */
	
	private void addDateTypes() {
		String xpathPrefixCustom = getProperty(XPATH_DATE_TYPES);
		for (int jCounter = 1; jCounter <= getProperties().size(); jCounter++) {
			String curXpathAtrCustom = getProperty(xpathPrefixCustom + jCounter);
			String hashKey=xpathPrefixCustom + jCounter;
			if(!map.containsKey(hashKey))
			{
				map.put(hashKey, curXpathAtrCustom);
			}
		}
	}
	
	/**
	 * This method invoke addMilliseconds method to update  the time field with milliseconds
	 * @param inXml
	 */
	
	private void updateMilliSeconds(YFCDocument inXml) {
		YFCElement eleOrderMessage = inXml.getDocumentElement();
		YFCElement eleOrder = eleOrderMessage.getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
		
		if(!XmlUtils.isVoid(eleOrderMessage.getAttribute(XMLLiterals.MODIFYTS)))
		{
			YTimestamp ts = eleOrderMessage.getYTimestampAttribute(XMLLiterals.MODIFYTS);
			eleOrderMessage.setAttribute(XMLLiterals.MODIFYTS, addMilliseconds(ts));
		}
		if(!XmlUtils.isVoid(eleOrder.getAttribute(XMLLiterals.CUSTOMER_REQ_DELIVERY_DATE)) &&
				!XmlUtils.isVoid(eleOrder.getAttribute(XMLLiterals.CUSTOMER_REQ_SHIP_DATE))) {
			invokeaddMilliseconds(eleOrder);
		}
		if(!XmlUtils.isVoid(eleOrder.getAttribute(XMLLiterals.CUSTOMER_REQ_DELIVERY_DATE)))
		{
			YTimestamp ts = eleOrder.getYTimestampAttribute(XMLLiterals.ORDER_DATE);
			eleOrder.setAttribute(XMLLiterals.ORDER_DATE, addMilliseconds(ts));
		}
		if(!XmlUtils.isVoid(eleOrder.getAttribute(XMLLiterals.ABANDONMENT_TIME))) {
			YTimestamp ts = eleOrder.getYTimestampAttribute(XMLLiterals.ABANDONMENT_TIME);
			eleOrder.setAttribute(XMLLiterals.ABANDONMENT_TIME, addMilliseconds(ts));
		}
	}
	   
	/**
	 * This method invokes addMilliseconds method
	 * @param eleOrder
	 */
	private void invokeaddMilliseconds(YFCElement eleOrder) {
		YTimestamp ts = eleOrder.getYTimestampAttribute(XMLLiterals.CUSTOMER_REQ_DELIVERY_DATE);
		String sCusReqDelDate=addMilliseconds(ts);
		eleOrder.setAttribute(XMLLiterals.CUSTOMER_REQ_DELIVERY_DATE, sCusReqDelDate);
		YTimestamp ts2 = eleOrder.getYTimestampAttribute(XMLLiterals.CUSTOMER_REQ_SHIP_DATE);
		String sCusReqShipDate=addMilliseconds(ts2);
		eleOrder.setAttribute(XMLLiterals.CUSTOMER_REQ_SHIP_DATE, sCusReqShipDate);
	}
	/**
	 * This method updates given time field with milliseconds
	 * @param eleOrderMessage
	 */
	  
	private String  addMilliseconds(YTimestamp ts) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String sModifyts= format.format(ts);
		return sModifyts.substring(0,10)+"T"+sModifyts.substring(11,23)+"Z";
	}

	/**
	  * this method is the invoking point for inputGetINDGMsgSeqNoList or  invokeCreateINDGMsgSeqNo method
	  * 
	  * @param inXml
	  * @return
	  */
	  
	private YFCDocument updateMsgSeqNo(YFCDocument inXml) {
			return inputGetINDGMsgSeqNoList(inXml);
	}
		
	/**
	 * 
	 * @param docgetMsgSeqList
	 * @param inXml
	 * @return
	 */
		
	private YFCDocument invokechangeINDGMsgSeqNo(YFCDocument docgetMsgSeqList,YFCDocument inXml) {
		String sSequeneceType;
		String sOrderNo;
		YFCElement eleOrderMessage=inXml.getDocumentElement();
		YFCElement eleOrder=eleOrderMessage.getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
		if(eleOrderMessage.getAttribute(XMLLiterals.MESSAGE_TYPE_ID).contains(SAP)) {
			sSequeneceType = XMLLiterals.SAP_OUTBOUND;
			sOrderNo=eleOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO);
		}
		else {
			sSequeneceType = XMLLiterals.LEGACY_OUTBOUND;
			sOrderNo=eleOrderMessage.getAttribute(XMLLiterals.ORDER_NO);
		}
		return getMsgSeqList(docgetMsgSeqList, sSequeneceType, sOrderNo);
	}
		 
	/**
	 * 
	 * @param docgetMsgSeqList
	 * @param sSequeneceType
	 * @param sOrderNo
	 * @return
	 */
	
	private YFCDocument getMsgSeqList(YFCDocument docgetMsgSeqList, String sSequeneceType, String sOrderNo) {
		YFCDocument docChangeIndgSeqNo=null;
		YFCElement elegetMsgSeqList=docgetMsgSeqList.getDocumentElement();
		YFCIterable<YFCElement> yfsItrator =elegetMsgSeqList.getChildren(XMLLiterals.INDG_MSG_SEQ_NO);
		for(YFCElement shipmentLine: yfsItrator) {
			if(shipmentLine.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID).equals(sSequeneceType) && 
					shipmentLine.getAttribute(XMLLiterals.ORDER_NO).equals(sOrderNo)) {
				docChangeIndgSeqNo = docChangeIndgSeqNo(shipmentLine);
			}
		}
		return docChangeIndgSeqNo;
	}
	
	/**
	 * 
	 * @param shipmentLine
	 * @return
	 */
		 
	private YFCDocument docChangeIndgSeqNo(YFCElement shipmentLine) {
		YFCDocument docChangeGetMsgSeq=YFCDocument.createDocument(XMLLiterals.INDG_MSG_SEQ_NO);
		YFCElement eleIndgMsgSeqNo=docChangeGetMsgSeq.getDocumentElement();
		eleIndgMsgSeqNo.setAttribute(XMLLiterals.ENTERPRISE_CODE, shipmentLine.getAttribute(XMLLiterals.ENTERPRISE_CODE));
		eleIndgMsgSeqNo.setAttribute(XMLLiterals.SEQUENCE_NO_KEY, shipmentLine.getAttribute(XMLLiterals.SEQUENCE_NO_KEY));
		eleIndgMsgSeqNo.setAttribute(XMLLiterals.DOCUMENT_TYPE, shipmentLine.getAttribute(XMLLiterals.DOCUMENT_TYPE));
		eleIndgMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO, shipmentLine.getAttribute(XMLLiterals.ORDER_NO));
		if((!XmlUtils.isVoid(shipmentLine.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID))) && shipmentLine.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID).contains(SAP))
		{
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO, shipmentLine.getAttribute(XMLLiterals.SAP_ORDER_NO));
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO,(Integer.parseInt(shipmentLine.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO)))+ONE);
		}
		else {
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO,EMPTY_STRING);
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO,(Integer.parseInt(shipmentLine.getAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO)))+ONE);
		}
		return invokeYantraService(INDG_CHANGE_INDG_MSG_SEQ_NO, docChangeGetMsgSeq);
	}
		 
	/**
	 * 		
	 * @param inXml
	 * @return
	 */
	
	private YFCDocument inputGetINDGMsgSeqNoList(YFCDocument inXml) {
		YFCDocument docGetINDGMsgSeqNoList=formMessageForAPI(inXml);
		YFCDocument docMsgSeqList=invokeYantraService(INDG_GET_INDG_MSG_SEQ_NO_LIST, docGetINDGMsgSeqNoList);
		if(docMsgSeqList.getDocumentElement().hasChildNodes()) {
			return invokechangeINDGMsgSeqNo(docMsgSeqList,inXml);
		}
		else 
			return 	invokeCreateINDGMsgSeqNo(inXml);
	}
	
	/**
	 * 	
	 * @param docOrderMessage
	 * @return
	 */
	
	private YFCDocument invokeCreateINDGMsgSeqNo(YFCDocument docOrderMessage) {
		YFCElement eleOrderMsg=docOrderMessage.getDocumentElement();
		if(XmlUtils.isVoid(eleOrderMsg.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO)) ||
				XmlUtils.isVoid(eleOrderMsg.getAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO)))
		{
			YFCDocument docINDGMsgSeqNoList=formMessageForAPI(docOrderMessage);
			YFCElement eleINDGMsgSeqNoList=docINDGMsgSeqNoList.getDocumentElement();
			if(!XmlUtils.isVoid(eleINDGMsgSeqNoList.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID)) && eleINDGMsgSeqNoList.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID).contains(SAP)) {
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO, TWO);
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO, EMPTY_STRING);
			}
			else {
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO, ONE);
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO,EMPTY_STRING);
			}
			return invokeYantraService(INDG_CREATE_INDG_MSG_SEQ_NO, docINDGMsgSeqNoList);
		}
		else {
			return inputGetINDGMsgSeqNoList(docOrderMessage);
		}
	}
		
	/**
	 * 
	 * @param docOrderMessage
	 * @return
	 */
	
	private YFCDocument formMessageForAPI(YFCDocument docOrderMessage) {
		YFCElement eleOrderMessage=docOrderMessage.getDocumentElement();
		YFCElement eleOrder=eleOrderMessage.getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
		YFCDocument docINDGMsgSeqNoList=YFCDocument.createDocument(XMLLiterals.INDG_MSG_SEQ_NO);
		YFCElement eleINDGMsgSeqNo=docINDGMsgSeqNoList.getDocumentElement();
		if(eleOrderMessage.getAttribute(XMLLiterals.MESSAGE_TYPE_ID).contains(SAP) && (!XmlUtils.isVoid(eleOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO))))
		{
			eleINDGMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO, eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
			eleINDGMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO, eleOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO));
			eleINDGMsgSeqNo.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID, XMLLiterals.SAP_OUTBOUND);
		}
		else {
			eleINDGMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO, getOrderNo(eleOrderMessage));	
			eleINDGMsgSeqNo.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID,XMLLiterals.LEGACY_OUTBOUND);
		}
		eleINDGMsgSeqNo.setAttribute(XMLLiterals.ENTERPRISE_CODE, eleOrder.getAttribute(XMLLiterals.ENTERPRISE_CODE));
		return docINDGMsgSeqNoList;
	}
	
	/**
	 * 
	 * @param eleOrderMessage
	 * @return
	 */
	
	private String getOrderNo(YFCElement eleOrderMessage) {
	  String orderNo = eleOrderMessage.getAttribute(XMLLiterals.ORDER_NO);
	  if(XmlUtils.isVoid(orderNo)) {
	    orderNo = eleOrderMessage.getAttribute(XMLLiterals.LEGACY_OMS_ORDER_NO);
	  }
	  return orderNo;
	}
}
