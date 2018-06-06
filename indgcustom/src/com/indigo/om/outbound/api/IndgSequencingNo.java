package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XMLUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
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
			  eleOrder.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID, XMLLiterals.SAP_OUTBOUND);
			  return docGetINDGMsgSeqNoList(inXml);
		  }
		  else
		  {
			  eleOrder.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID, XMLLiterals.LEGACY_OUTBOUND);
			 return  docGetINDGMsgSeqNoList(inXml);  
		  }
		 
			  
	  }
	  
	 /**this method is the invoking point for inputGetINDGMsgSeqNoList or  invokeCreateINDGMsgSeqNo method
	  * 
	  * @param inXml
	  * @return
	  */
		private YFCDocument docGetINDGMsgSeqNoList(YFCDocument inXml) {
			YFCElement eleOrderMessage=inXml.getDocumentElement();
			YFCElement eleOrder=eleOrderMessage.getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
			System.out.println("----docGetINDGMsgSeqNoList    SAP_ORDER_NO----"+eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
			System.out.println("----docGetINDGMsgSeqNoList    SAP_MSG_SEQ_NO----"+eleOrderMessage.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO));
			System.out.println("----docGetINDGMsgSeqNoList    LEGACY_MSG_SEQ_NO----"+eleOrderMessage.getAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO));
			if(!(XmlUtils.isVoid(eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO)) && (XmlUtils.isVoid(eleOrderMessage.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO))))
					|| (!XmlUtils.isVoid(eleOrderMessage.getAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO))  ))
				return inputGetINDGMsgSeqNoList(eleOrderMessage);
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
			 YFCElement elegetMsgSeqList=docgetMsgSeqList.getDocumentElement().getChildElement(XMLLiterals.INDG_MSG_SEQ_NO);
			YFCDocument docChangeGetMsgSeq=YFCDocument.createDocument(XMLLiterals.INDG_MSG_SEQ_NO);
			YFCElement eleIndgMsgSeqNo=docChangeGetMsgSeq.getDocumentElement();
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.SEQUENCE_NO_KEY, elegetMsgSeqList.getAttribute(XMLLiterals.SEQUENCE_NO_KEY));
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.DOCUMENT_TYPE, elegetMsgSeqList.getAttribute(XMLLiterals.DOCUMENT_TYPE));
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO,elegetMsgSeqList.getAttribute(XMLLiterals.ORDER_NO));
			System.out.println(" invokechangeINDGMsgSeqNo SAP_ORDER_NO"+elegetMsgSeqList.getAttribute(XMLLiterals.SAP_ORDER_NO));
			if(!XmlUtils.isVoid(elegetMsgSeqList.getAttribute(XMLLiterals.SAP_ORDER_NO)))
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO,elegetMsgSeqList.getAttribute(XMLLiterals.SAP_ORDER_NO));
			else
				eleIndgMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO,EMPTY_STRING);
			System.out.println("invokechangeINDGMsgSeqNo  SAP_MSG_SEQ_NO"+elegetMsgSeqList.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO));
			if(!XmlUtils.isVoid(elegetMsgSeqList.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO)))
			eleIndgMsgSeqNo.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO,(Integer.parseInt(elegetMsgSeqList.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO)))+ONE);
			else
				eleIndgMsgSeqNo.setAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO,(Integer.parseInt(elegetMsgSeqList.getAttribute(XMLLiterals.LEGACY_MSG_SEQ_NO)))+ONE);
			System.out.println("invokechangeINDGMsgSeqNo ChangeGetMsgSeq"+docChangeGetMsgSeq);
			return invokeYantraService(INDG_CHANGE_INDG_MSG_SEQ_NO, docChangeGetMsgSeq);
		}
		
		/**
		 * this method invokes getINDGMsgSeqNoList api
		 * @param eleOrderMessage
		 * @return
		 */
			
		private YFCDocument inputGetINDGMsgSeqNoList(YFCElement eleOrderMessage) {
			System.out.println("inputGetINDGMsgSeqNoList INPUT"+eleOrderMessage);
			YFCElement eleOrder= eleOrderMessage.getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
			 YFCDocument docGetINDGMsgSeqNoList=YFCDocument.createDocument(XMLLiterals.INDG_MSG_SEQ_NO);
			YFCElement eleGetINDGMsgSeqNoList=docGetINDGMsgSeqNoList.getDocumentElement();
			String sOrderNo=eleOrderMessage.getAttribute(XMLLiterals.ORDER_NO);
			System.out.println("ORDER_NO"+sOrderNo);
			if(XmlUtils.isVoid(sOrderNo))
					eleGetINDGMsgSeqNoList.setAttribute(XMLLiterals.ORDER_NO,eleOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO));	
			else 
				eleGetINDGMsgSeqNoList.setAttribute(XMLLiterals.ORDER_NO,sOrderNo);
			eleGetINDGMsgSeqNoList.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID, eleOrder.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID));
			System.out.println("inputGetINDGMsgSeqNoList SAP_ORDER_NO"+ eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
			if(!XmlUtils.isVoid(eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO)))
				eleGetINDGMsgSeqNoList.setAttribute(XMLLiterals.SAP_ORDER_NO,eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
			else
				eleGetINDGMsgSeqNoList.setAttribute(XMLLiterals.SAP_ORDER_NO,EMPTY_STRING);
			
			System.out.println("inputGetINDGMsgSeqNoList DOCUMENT"+docGetINDGMsgSeqNoList);
			if(!XmlUtils.isVoid(invokeYantraService(INDG_GET_INDG_MSG_SEQ_NO_LIST, docGetINDGMsgSeqNoList)))
				return invokechangeINDGMsgSeqNo(invokeYantraService(INDG_GET_INDG_MSG_SEQ_NO_LIST, docGetINDGMsgSeqNoList));
			else 
				
			return 	invokeCreateINDGMsgSeqNo(docGetINDGMsgSeqNoList);
		}
	
		/**
		 * this method invokes createINDGMsgSeqNo api
		 * @param docOrderMessage
		 * @return
		 */
		private YFCDocument invokeCreateINDGMsgSeqNo(YFCDocument docOrderMessage) {
			System.out.println("invokeCreateINDGMsgSeqNo INPUT"+docOrderMessage);
			YFCElement eleOrderMessage=docOrderMessage.getDocumentElement();
			YFCElement eleOrder=eleOrderMessage.getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
			YFCDocument docGetINDGMsgSeqNoList=YFCDocument.createDocument(XMLLiterals.INDG_MSG_SEQ_NO);
			YFCElement eleCreateINDGMsgSeqNo=docGetINDGMsgSeqNoList.getDocumentElement();
			eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.SEQUENCE_TYPE_ID, eleOrder.getAttribute(XMLLiterals.SEQUENCE_TYPE_ID));
			eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO,ONE);
			System.out.println("invokeCreateINDGMsgSeqNo SAP_ORDER_NO"+eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
			if(!XmlUtils.isVoid(eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO))) {
			eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO,eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
			}
			else {
			eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO,EMPTY_STRING );
			}
			String sOrderNo=eleOrderMessage.getAttribute(XMLLiterals.ORDER_NO);
			System.out.println("invokeCreateINDGMsgSeqNo sOrderNo"+sOrderNo);
			if(XmlUtils.isVoid(sOrderNo))
				eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO,eleOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO));	
			else 
				eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO,sOrderNo);
			eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.DOCUMENT_TYPE, eleOrder.getAttribute(XMLLiterals.DOCUMENT_TYPE));
			eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.ENTERPRISE_CODE,eleOrder.getAttribute(XMLLiterals.ENTERPRISE_CODE));
			System.out.println("invokeCreateINDGMsgSeqNo OUTPUT"+docGetINDGMsgSeqNoList);
			return invokeYantraService(INDG_CREATE_INDG_MSG_SEQ_NO, docGetINDGMsgSeqNoList);
			
		}
			
	  
	  

}
