package com.indigo.om.outbound.api;

import java.text.ParseException;

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
 */

public class IndgSequencingNo extends AbstractCustomApi {
	
	private static final String EMPTY_STRING = "";
	private static final long ONE = 1;
	private static final CharSequence SAP = "SAP";
	private static final String INDG_CHANGE_INDG_MSG_SEQ_NO="INDG_changeINDGMsgSeqNo";
	private static final String INDG_CREATE_INDG_MSG_SEQ_NO="INDG_createINDGMsgSeqNo";
	private static final String INDG_GET_INDG_MSG_SEQ_NO_LIST="INDG_getINDGMsgSeqNoList";
	
	/**
	   * @throws ParseException 
	   * This is the invoke point of the Service
	   * @throws  
	   * 
	   */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {  
		YFCDocument docMsg=checkDataExists(inXml);
		try {
			addMsgSeqNo(docMsg,inXml);
		}
		catch(Exception e) {
			throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_MISSING_VALUE, e);
		}
		return inXml;
	}
	  
	/**
	 * This method appends SequenceNo to the input file
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
		if(!XmlUtils.isVoid(eleOrderMessage.getYTimestampAttribute(XMLLiterals.MODIFYTS))) {
			addModifyts(eleOrderMessage);
		}
	}
	
	/**
	 * This method modifies the time format of Modifyts attribute
	 * @param eleOrderMessage
	 */
	  
	private void addModifyts(YFCElement eleOrderMessage) {
		YTimestamp ts = eleOrderMessage.getYTimestampAttribute(XMLLiterals.MODIFYTS);
		eleOrderMessage.removeAttribute(XMLLiterals.MODIFYTS);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String sModifyts= format.format(ts);
		sModifyts = sModifyts.substring(0,10)+"T"+sModifyts.substring(11,23)+"Z";
		eleOrderMessage.setAttribute(XMLLiterals.MODIFYTS, sModifyts);
		
	}
		
	/**
	 * This method fetches the Sequence type and OrderNo and invokes getMsgSeqList method
	 * @param docgetMsgSeqList
	 * @param inXml
	 * @return
	 */
		
	private YFCDocument invokegetMsgSeqList(YFCDocument docgetMsgSeqList,YFCDocument inXml) {
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
	 * This method invokes invokechangeINDGMsgSeqNo method
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
				docChangeIndgSeqNo =invokechangeINDGMsgSeqNo(shipmentLine);
			}
		}
		return docChangeIndgSeqNo;
	}
	
	/**
	 * This method invokes invoke antraService INDG_changeINDGMsgSeqNo
	 * @param shipmentLine
	 * @return
	 */
		 
	private YFCDocument invokechangeINDGMsgSeqNo(YFCElement shipmentLine) {
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
	
	private YFCDocument checkDataExists(YFCDocument inXml) {
		YFCDocument docGetINDGMsgSeqNoList=formMessageForAPI(inXml);
		YFCDocument docMsgSeqList=invokeYantraService(INDG_GET_INDG_MSG_SEQ_NO_LIST, docGetINDGMsgSeqNoList);
		if(docMsgSeqList.getDocumentElement().hasChildNodes()) {
			return invokegetMsgSeqList(docMsgSeqList,inXml);
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
		YFCDocument docCreateIndgMsgSeqNo=null;
		if(XmlUtils.isVoid(eleOrderMsg.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO)) ||
				XmlUtils.isVoid(eleOrderMsg.getAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO)))
		{
			YFCDocument docINDGMsgSeqNoList=formMessageForAPI(docOrderMessage);
			YFCElement eleINDGMsgSeqNoList=docINDGMsgSeqNoList.getDocumentElement();
			if(!XmlUtils.isVoid(eleINDGMsgSeqNoList.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID)) && eleINDGMsgSeqNoList.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID).contains(SAP)) {
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO, ONE);
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO, EMPTY_STRING);
			}
			else {
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO, ONE);
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO,EMPTY_STRING);
			}
			docCreateIndgMsgSeqNo=invokeYantraService(INDG_CREATE_INDG_MSG_SEQ_NO, docINDGMsgSeqNoList);
		}
			return docCreateIndgMsgSeqNo;

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
			eleINDGMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO, eleOrderMessage.getAttribute(XMLLiterals.ORDER_NO));	
			eleINDGMsgSeqNo.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID,XMLLiterals.LEGACY_OUTBOUND);
		}
		eleINDGMsgSeqNo.setAttribute(XMLLiterals.DOCUMENT_TYPE, eleOrder.getAttribute(XMLLiterals.DOCUMENT_TYPE));
		eleINDGMsgSeqNo.setAttribute(XMLLiterals.ENTERPRISE_CODE, eleOrder.getAttribute(XMLLiterals.ENTERPRISE_CODE));
		return docINDGMsgSeqNoList;
	}
}
