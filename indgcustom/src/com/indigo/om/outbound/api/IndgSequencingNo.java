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
 */

public class IndgSequencingNo extends AbstractCustomApi {
	
	private static final String EMPTY_STRING = "";
	private static final long ONE = 1;
	private static final CharSequence SAP = "SAP";
	private static final String INDG_CHANGE_INDG_MSG_SEQ_NO="INDG_changeINDGMsgSeqNo";
	private static final String INDG_CREATE_INDG_MSG_SEQ_NO="INDG_createINDGMsgSeqNo";
	private static final String INDG_GET_INDG_MSG_SEQ_NO_LIST="INDG_getINDGMsgSeqNoList";
	Map<String, String> map=new HashMap<>();
	private static final String XPATH_DATE_TYPES= "xpath.date.types";
	String CUSTOMER_REQ_DEL_DATE = "CustReqDeliveryDate";
	String CUSTOMER_REQ_SHIP_DATE = "CustReqShipDate";
	String ORDER_DATE = "OrderDate";
	String ABANDONMENT_TIME = "AbandonmentTime";
	
	/**
	   * @throws ParseException 
	   * This is the invoke point of the Service
	   * @throws  
	   * 
	   */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {  
		System.out.println("hzfgbJHdgkzn"+inXml);
		YFCDocument docMsg=updateMsgSeqNo(inXml);
		System.out.println("shbfhjgh"+docMsg);
		try {
			addMsgSeqNo(docMsg,inXml);
		}
		catch(Exception e) {
			throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_MISSING_VALUE, e);
		}
		addDateTypes();
		upadteMilliSeconds(inXml);
		
		return inXml;
	}
	  
	/**
	 *   
	 * @param docMsg
	 * @param inXml
	 */
	
	private void addMsgSeqNo(YFCDocument docMsg,YFCDocument inXml) {
		System.out.println("gvfyhgtbdhy"+docMsg);
		System.out.println("njdfnjdghidth"+inXml);
		YFCElement eleOrderMessage=inXml.getDocumentElement();
		YFCElement eleINDGMsgSeqNo=docMsg.getDocumentElement();
		System.out.println("cbdhygkuzkdj"+eleINDGMsgSeqNo);
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
	
	
	private void addDateTypes() {
		System.out.println("bgufydbnhiyjmi");
		String xpathPrefixCustom = getProperty(XPATH_DATE_TYPES);
		System.out.println("xpathPrefixCustomhnudhy");
		for (int jCounter = 1; jCounter <= getProperties().size(); jCounter++) {
			String curXpathAtrCustom = getProperty(xpathPrefixCustom + jCounter);	
			String hashKey=curXpathAtrCustom;
			if(!map.containsKey(hashKey))
			{
				map.put(hashKey, hashKey);
			}
		}
		for (String name: map.keySet()){

            String key =name.toString();
            String value = map.get(name).toString();  
            System.out.println(key + "@#$%^&*((((*&^%$ " + value);  


} 
		System.out.println("hdubdhsdbhsfk");
			
	}
	
	
	private void upadteMilliSeconds(YFCDocument inXml)
	{
		YFCElement eleOrderMessage = inXml.getDocumentElement();
		if(!XmlUtils.isVoid(eleOrderMessage.getAttribute(XMLLiterals.MODIFYTS)))
		{
			String smodifyts=addModifyts(eleOrderMessage);
			eleOrderMessage.setAttribute(XMLLiterals.MODIFYTS, smodifyts);
			System.out.println("gdfuhygsugoahotyu8" + inXml );
		}
		
	}
	/**
	 * 
	 * @param eleOrderMessage
	 */
	  
	private String  addModifyts(YFCElement eleOrderMessage) {
		YTimestamp ts = eleOrderMessage.getYTimestampAttribute(XMLLiterals.MODIFYTS);
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
				docChangeIndgSeqNo =docChangeIndgSeqNo(shipmentLine);
			}
		}
		System.out.println("gfefguerjgtidjhyifouj"+docChangeIndgSeqNo);
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
		YFCDocument udghauf=invokeYantraService(INDG_CHANGE_INDG_MSG_SEQ_NO, docChangeGetMsgSeq);
		System.out.println("udghauf");
		return udghauf;
	}
		 
	/**
	 * 		
	 * @param inXml
	 * @return
	 */
	
	private YFCDocument inputGetINDGMsgSeqNoList(YFCDocument inXml) {
		YFCDocument docGetINDGMsgSeqNoList=formMessageForAPI(inXml);
		System.out.println("vfhsvbgjrdbgkdh"+docGetINDGMsgSeqNoList);
		YFCDocument docMsgSeqList=invokeYantraService(INDG_GET_INDG_MSG_SEQ_NO_LIST, docGetINDGMsgSeqNoList);
		System.out.println("ksjfcksdfisjdfg"+docMsgSeqList);
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
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO, ONE);
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO, EMPTY_STRING);
			}
			else {
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO, ONE);
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO,EMPTY_STRING);
			}
			YFCDocument kdjfks=invokeYantraService(INDG_CREATE_INDG_MSG_SEQ_NO, docINDGMsgSeqNoList);
			System.out.println("ckcfksfmlkmzlgxg"+kdjfks);
			return kdjfks;
		}
		else {
			YFCDocument chbdsfjsdg= inputGetINDGMsgSeqNoList(docOrderMessage);
			return chbdsfjsdg;
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
			eleINDGMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO, eleOrderMessage.getAttribute(XMLLiterals.ORDER_NO));	
			eleINDGMsgSeqNo.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID,XMLLiterals.LEGACY_OUTBOUND);
		}
		eleINDGMsgSeqNo.setAttribute(XMLLiterals.DOCUMENT_TYPE, eleOrder.getAttribute(XMLLiterals.DOCUMENT_TYPE));
		eleINDGMsgSeqNo.setAttribute(XMLLiterals.ENTERPRISE_CODE, eleOrder.getAttribute(XMLLiterals.ENTERPRISE_CODE));
		return docINDGMsgSeqNoList;
	}
}
