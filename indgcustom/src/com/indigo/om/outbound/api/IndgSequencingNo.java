package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
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
	   * This is the invoke point of the Service
	   * @throws  
	   * 
	   */
	  @Override
	 public YFCDocument invoke(YFCDocument inXml) {  
		  return  verifyTypeOfMsg(inXml);
		   
	  }
	  /**
	   * this method identifies the type of message i.e LEGACY OR SAP message
	   * @param inXml
	   */
	  private YFCDocument verifyTypeOfMsg(YFCDocument inXml) {
		
		  YFCElement eleOrderMessage=inXml.getDocumentElement();
		  YFCElement eleOrder=eleOrderMessage.getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
		  if(eleOrderMessage.getAttribute(XMLLiterals.MESSAGE_TYPE_ID).contains(SAP))
		  {	
			  eleOrder.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID,XMLLiterals.SAP_OUTBOUND);
			  return docGetINDGMsgSeqNoList(inXml);
		  }
		  else
		  {
			  eleOrder.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID,XMLLiterals.LEGACY_OUTBOUND);
			 return  docGetINDGMsgSeqNoList(inXml);  
		  }
		 
			  
	  }
	  
	 /**this method is the invoking point for inputGetINDGMsgSeqNoList or  invokeCreateINDGMsgSeqNo method
	  * 
	  * @param inXml
	  * @return
	  */
		private YFCDocument docGetINDGMsgSeqNoList(YFCDocument inXml) {
			System.out.println("docGetINDGMsgSeqNoList"+inXml);
			YFCElement eleOrderMessage=inXml.getDocumentElement();
			YFCElement eleOrder=eleOrderMessage.getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
			System.out.println("----docGetINDGMsgSeqNoList    SAP_ORDER_NO----"+eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
			System.out.println("----docGetINDGMsgSeqNoList    SAP_MSG_SEQ_NO----"+eleOrderMessage.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO));
			System.out.println("----docGetINDGMsgSeqNoList    LEGACY_MSG_SEQ_NO----"+eleOrderMessage.getAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO));
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
			 System.out.println("invokechangeINDGMsgSeqNo input document "+docgetMsgSeqList);
			 YFCElement elegetMsgSeqList=docgetMsgSeqList.getDocumentElement();
			YFCDocument docChangeGetMsgSeq=YFCDocument.createDocument(XMLLiterals.INDG_MSG_SEQ_NO);
			YFCElement eleIndgMsgSeqNo=docChangeGetMsgSeq.getDocumentElement();
			YFCIterable<YFCElement> yfsItrator = elegetMsgSeqList.getChildren(XMLLiterals.INDG_MSG_SEQ_NO);
			for(YFCElement elegetMsgSeqNo : yfsItrator) {
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.SEQUENCE_NO_KEY, elegetMsgSeqNo.getAttribute(XMLLiterals.SEQUENCE_NO_KEY));
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.DOCUMENT_TYPE, elegetMsgSeqNo.getAttribute(XMLLiterals.DOCUMENT_TYPE));
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO,elegetMsgSeqNo.getAttribute(XMLLiterals.ORDER_NO));
			if(!XmlUtils.isVoid(elegetMsgSeqList.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO))){
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO,elegetMsgSeqNo.getAttribute(XMLLiterals.SAP_ORDER_NO));
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO,(Integer.parseInt(elegetMsgSeqNo.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO)))+ONE);
			}
			else {
				eleIndgMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO,EMPTY_STRING);
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO,(Integer.parseInt(elegetMsgSeqNo.getAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO)))+ONE);
			}
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
			YFCDocument docinvokeYantraService=invokeYantraService(INDG_GET_INDG_MSG_SEQ_NO_LIST, docGetINDGMsgSeqNoList);
			if(!XmlUtils.isVoid(docinvokeYantraService)) {
				YFCDocument changesDoc= invokechangeINDGMsgSeqNo(docinvokeYantraService);
				System.out.println("changesDoc"+changesDoc);
				return changesDoc;
			}
			else 
				
			return 	invokeCreateINDGMsgSeqNo(docGetINDGMsgSeqNoList);
		}
	
		/**
		 * this method invokes createINDGMsgSeqNo api
		 * @param docOrderMessage
		 * @return
		 */
		private YFCDocument invokeCreateINDGMsgSeqNo(YFCDocument docOrderMessage) {
			YFCDocument docINDGMsgSeqNoList=formMessageForAPI(docOrderMessage);
			YFCElement eleINDGMsgSeqNoList=docINDGMsgSeqNoList.getDocumentElement();
			if(eleINDGMsgSeqNoList.getAttribute(XMLLiterals.MESSAGE_TYPE_ID).contains(SAP))
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO, ONE);
			else
				eleINDGMsgSeqNoList.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO, ONE);

			return invokeYantraService(INDG_CREATE_INDG_MSG_SEQ_NO, docINDGMsgSeqNoList);
			
		}
		private YFCDocument formMessageForAPI(YFCDocument docOrderMessage) {
			YFCElement eleOrderMessage=docOrderMessage.getDocumentElement();
			YFCElement eleOrder=eleOrderMessage.getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
			YFCDocument docINDGMsgSeqNoList=YFCDocument.createDocument(XMLLiterals.INDG_MSG_SEQ_NO);
			YFCElement eleINDGMsgSeqNo=docINDGMsgSeqNoList.getDocumentElement();
			if(eleOrderMessage.getAttribute(XMLLiterals.MESSAGE_TYPE_ID).contains(SAP) && (!XmlUtils.isVoid(eleOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO))))
			{
				eleINDGMsgSeqNo.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO,ONE);
				eleINDGMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO,eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
				eleINDGMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO,eleOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO));
				eleINDGMsgSeqNo.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID, XMLLiterals.SAP_OUTBOUND);
			}
			else {
				eleINDGMsgSeqNo.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO,ONE);
				eleINDGMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO,eleOrderMessage.getAttribute(XMLLiterals.ORDER_NO));	
				eleINDGMsgSeqNo.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID,XMLLiterals.LEGACY_OUTBOUND);
			}
			
			eleINDGMsgSeqNo.setAttribute(XMLLiterals.DOCUMENT_TYPE, eleOrder.getAttribute(XMLLiterals.DOCUMENT_TYPE));
			eleINDGMsgSeqNo.setAttribute(XMLLiterals.ENTERPRISE_CODE,eleOrder.getAttribute(XMLLiterals.ENTERPRISE_CODE));
			return docINDGMsgSeqNoList;
		}
}
