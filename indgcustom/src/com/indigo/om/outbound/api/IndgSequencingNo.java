package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XMLUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class IndgSequencingNo extends AbstractCustomApi{
	private static final String ONE="1";
	private static final String INDG_CHANGE_INDG_MSG_SEQ_NO="INDG_changeINDGMsgSeqNo";
	private static final String INDG_CREATE_INDG_MSG_SEQ_NO="INDG_createINDGMsgSeqNo";
	private static final String INDG_DELETE_GET_INDG_MSG_SEQ_NO="INDG_deleteINDGMsgSeqNo";
	private static final String INDG_GET_INDG_MSG_SEQ_NO="INDG_getINDGMsgSeqNo";
	private static final String INDG_GET_INDG_MSG_SEQ_NO_LIST="INDG_getINDGMsgSeqNoList";
	/**
	   * This is the invoke point of the Service
	 * @throws  
	   * 
	   */
	  @Override
	 public YFCDocument invoke(YFCDocument inXml) {
		  return docGetINDGMsgSeqNoList(inXml);
		   
	  }
			
		private YFCDocument docGetINDGMsgSeqNoList(YFCDocument inXml) {
			YFCDocument docGetINDGMsgSeqNoList=null;
		
			YFCElement eleOrderMessage=inXml.getDocumentElement();
			YFCElement eleOrder= eleOrderMessage.getChildElement(XMLLiterals.ORDER_MESSAGE).getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
			if(eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO)!=null) {
				docGetINDGMsgSeqNoList=inputGetINDGMsgSeqNoList(eleOrder);
				
			if(!XmlUtils.isVoid(invokeYantraService(INDG_GET_INDG_MSG_SEQ_NO_LIST, docGetINDGMsgSeqNoList)))
				docGetINDGMsgSeqNoList.getDocumentElement().setAttribute(XMLLiterals.SAP_MSG_SEQ_NO, Integer.parseInt(eleOrderMessage.getAttribute(XMLLiterals.SAP_MSG_SEQ_NO))+ONE);
			
			else 
				docGetINDGMsgSeqNoList=	invokeCreateINDGMsgSeqNo(eleOrder);
			System.out.println("after creation"+docGetINDGMsgSeqNoList);
			}
System.out.println("---docGetINDGMsgSeqNoList final document --"+docGetINDGMsgSeqNoList);
			return docGetINDGMsgSeqNoList;
		}
		private YFCDocument inputGetINDGMsgSeqNoList(YFCElement eleOrder) {
			 YFCDocument docGetINDGMsgSeqNoList=YFCDocument.createDocument(XMLLiterals.GET_INDG_MSG_SEQ_NO_LIST);
			YFCElement eleGetINDGMsgSeqNoList=docGetINDGMsgSeqNoList.getDocumentElement();
			eleGetINDGMsgSeqNoList.setAttribute(XMLLiterals.SAP_ORDER_NO,eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
			eleGetINDGMsgSeqNoList.setAttribute(XMLLiterals.ORDER_NO, eleOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO));
			System.out.println("inputGetINDGMsgSeqNoList method"+docGetINDGMsgSeqNoList);
			return docGetINDGMsgSeqNoList;
			
		}
		
		private YFCDocument invokeCreateINDGMsgSeqNo(YFCElement eleOrder) {
			YFCDocument docGetINDGMsgSeqNoList=YFCDocument.createDocument(XMLLiterals.CREATE_INDG_MSG_SEQ_NO);
			YFCElement eleCreateINDGMsgSeqNo=docGetINDGMsgSeqNoList.getDocumentElement();
			eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO,ONE);
			eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO,eleOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO));
			eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO,eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
			eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.DOCUMENT_TYPE, eleOrder.getAttribute(XMLLiterals.DOCUMENT_TYPE));
			eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.ENTERPRISE_CODE,eleOrder.getAttribute(XMLLiterals.ENTERPRISE_CODE));
			System.out.println("invokeCreateINDGMsgSeqNo---"+docGetINDGMsgSeqNoList);
			
			return invokeYantraService(INDG_CREATE_INDG_MSG_SEQ_NO, docGetINDGMsgSeqNoList);
			
		}
			
	  
	  

}
