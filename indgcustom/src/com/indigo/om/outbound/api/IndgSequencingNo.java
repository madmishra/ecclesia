package com.indigo.om.outbound.api;

import java.text.ParseException;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.ibm.icu.text.SimpleDateFormat;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.date.YTimestamp;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
/**
 * 
 * 
 * @author BSG170
 *
 */
public class IndgSequencingNo extends AbstractCustomApi{
	private static final String EMPTY_STRING = "";
	private static final long ONE=1;
	private static final CharSequence SAP="SAP";
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
		  YFCDocument docMsg=updateMsgSeqNo(inXml);
		  System.out.println("husnztusrdyvbujmn"+docMsg);
		  YFCDocument dxvh=addMsgSeqNo(docMsg,inXml);
		  System.out.println("jnghbbbbbbuiohykitgooir"+dxvh);
		  return dxvh;
		   
	  }
	  /**
	   * 
	   * @param docMsg
	   * @param inXml
	   * @return
	   */
	  private YFCDocument addMsgSeqNo(YFCDocument docMsg,YFCDocument inXml)  {
		  System.out.println("hmnuion vit eo umtin,oivnut");
		  YFCElement eleOrderMessage=inXml.getDocumentElement();
		  YFCElement eleINDGMsgSeqNo=docMsg.getDocumentElement();
		  String sSAPMsgSeqNo=eleINDGMsgSeqNo.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO);
		  if(!XmlUtils.isVoid(sSAPMsgSeqNo))
			  eleOrderMessage.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO, sSAPMsgSeqNo);
		  else
			  eleOrderMessage.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO, eleINDGMsgSeqNo.
					  getAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO));
		 
		  YTimestamp ts = eleOrderMessage.getYTimestampAttribute(XMLLiterals.MODIFYTS);
		  eleOrderMessage.removeAttribute(XMLLiterals.MODIFYTS);
		  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		  String sModifyts= format.format(ts);
		  sModifyts = sModifyts.substring(0,10)+"T"+sModifyts.substring(11,23)+"Z";
		  eleOrderMessage.setAttribute(XMLLiterals.MODIFYTS, sModifyts);
		  System.out.println("huINHCRUSVTUIBJRTNYUK"+inXml);
		return   inXml;
	  }
	  
	 /**this method is the invoking point for inputGetINDGMsgSeqNoList or  invokeCreateINDGMsgSeqNo method
	  * 
	  * @param inXml
	  * @return
	  */
		private YFCDocument updateMsgSeqNo(YFCDocument inXml) {
			System.out.println("yuthynsbvujyhgJFUG"+inXml);
			YFCElement eleOrderMessage=inXml.getDocumentElement();
			YFCElement eleOrder=eleOrderMessage.getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
			System.out.println("funhuuuuuuuuuuuuuyjmug"+eleOrder);
			System.out.println("urthyuynukbjumituj"+eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
			System.out.println("jkenhv gutjhykju"+XmlUtils.isVoid(eleOrderMessage.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO)));
			System.out.println("ycw7tiyh4nwu5i"+eleOrderMessage.getAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO));
			if(!(XmlUtils.isVoid(eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO))
					&& (XmlUtils.isVoid(eleOrderMessage.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO))))
					|| (!XmlUtils.isVoid(eleOrderMessage.getAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO))  ))
				return inputGetINDGMsgSeqNoList(inXml);
			else
				return invokeCreateINDGMsgSeqNo(inXml);	
		}
		
		/**this method invokes chengeINDGMsgSeqNo api
		 * 
		 * @param docgetMsgSeqList
		 * @return
		 */
		 private YFCDocument invokechangeINDGMsgSeqNo(YFCDocument docgetMsgSeqList) {
			 System.out.println("INVOKECHANGEfhjnec tivwmoh");
			 System.out.println("BTUHNI UPchbuNYIHGTKVLMCWEZopt"+docgetMsgSeqList);
			 YFCElement elegetMsgSeqList=docgetMsgSeqList.getDocumentElement().getChildElement(XMLLiterals.INDG_MSG_SEQ_NO);
			YFCDocument docChangeGetMsgSeq=YFCDocument.createDocument(XMLLiterals.INDG_MSG_SEQ_NO);
			YFCElement eleIndgMsgSeqNo=docChangeGetMsgSeq.getDocumentElement();

			eleIndgMsgSeqNo.setAttribute(XMLLiterals.ENTERPRISE_CODE, elegetMsgSeqList.getAttribute(XMLLiterals.ENTERPRISE_CODE));
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.SEQUENCE_NO_KEY, elegetMsgSeqList.getAttribute(XMLLiterals.SEQUENCE_NO_KEY));
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.DOCUMENT_TYPE, elegetMsgSeqList.getAttribute(XMLLiterals.DOCUMENT_TYPE));
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO,elegetMsgSeqList.getAttribute(XMLLiterals.ORDER_NO));
			System.out.println("unhch muigwsoit i,popeMAGRN"+elegetMsgSeqList.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID));
			if((!XmlUtils.isVoid(elegetMsgSeqList.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID))) && 
					elegetMsgSeqList.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID).contains(SAP)){
				System.out.println("IFPARTMICEO");
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO,elegetMsgSeqList.getAttribute(XMLLiterals.SAP_ORDER_NO));
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO,(Integer.parseInt(elegetMsgSeqList.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO)))+ONE);
			}
			else {
				System.out.println("elsepart NJKRN");
				eleIndgMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO,EMPTY_STRING);
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO,(Integer.parseInt(elegetMsgSeqList.getAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO)))+ONE);
			}
			return invokeYantraService(INDG_CHANGE_INDG_MSG_SEQ_NO, docChangeGetMsgSeq);
			}
			
	
			 
		
		
		/**
		 * this method invokes getINDGMsgSeqNoList api
		 * @param eleOrderMessage
		 * @return
		 */
			
		private YFCDocument inputGetINDGMsgSeqNoList(YFCDocument inXml) {
			YFCDocument docGetINDGMsgSeqNoList=formMessageForAPI(inXml);
			System.out.println("UJw hbuwehfumukngngkjm"+docGetINDGMsgSeqNoList);
			YFCDocument docMsgSeqList=invokeYantraService(INDG_GET_INDG_MSG_SEQ_NO_LIST, docGetINDGMsgSeqNoList);
			System.out.println("jnhfjnhjgrfdsn fkrdmjklkld"+docMsgSeqList);
			if(docMsgSeqList.getDocumentElement().hasChildNodes()) {
				return invokechangeINDGMsgSeqNo(docMsgSeqList);
			}
			else 
				
			return 	invokeCreateINDGMsgSeqNo(inXml);
		}
	
		/**
		 * this method invokes createINDGMsgSeqNo api
		 * @param docOrderMessage
		 * @return
		 */
		private YFCDocument invokeCreateINDGMsgSeqNo(YFCDocument docOrderMessage) {
			System.out.println("createindgmsgNMITDN");
			YFCDocument docINDGMsgSeqNoList=formMessageForAPI(docOrderMessage);
			YFCElement eleINDGMsgSeqNoList=docINDGMsgSeqNoList.getDocumentElement();
			if(!XmlUtils.isVoid(eleINDGMsgSeqNoList.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID)) && 
					eleINDGMsgSeqNoList.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID).contains(SAP)) {
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO, ONE);
			eleINDGMsgSeqNoList.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO, EMPTY_STRING);
			}
			else {
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO, ONE);
			eleINDGMsgSeqNoList.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO,EMPTY_STRING);
			}
			System.out.println("NUEHUITROVNY OJ OIIRIURUIORTIURTR"+docINDGMsgSeqNoList);
			return invokeYantraService(INDG_CREATE_INDG_MSG_SEQ_NO, docINDGMsgSeqNoList);
		}
		/**
		 * this method forms document for createINDGMsgSeqNo and getINDGMsgseqNoList API
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
			
				eleINDGMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO,eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
				eleINDGMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO,eleOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO));
				eleINDGMsgSeqNo.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID, XMLLiterals.SAP_OUTBOUND);
			}
			else {
				eleINDGMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO,eleOrderMessage.getAttribute(XMLLiterals.ORDER_NO));	
				eleINDGMsgSeqNo.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID,XMLLiterals.LEGACY_OUTBOUND);
			}
			
			eleINDGMsgSeqNo.setAttribute(XMLLiterals.DOCUMENT_TYPE, eleOrder.getAttribute(XMLLiterals.DOCUMENT_TYPE));
			eleINDGMsgSeqNo.setAttribute(XMLLiterals.ENTERPRISE_CODE,eleOrder.getAttribute(XMLLiterals.ENTERPRISE_CODE));
			return docINDGMsgSeqNoList;
		}
}
