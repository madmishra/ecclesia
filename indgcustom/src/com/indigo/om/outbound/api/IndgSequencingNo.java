package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XMLUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class IndgSequencingNo extends AbstractCustomApi{
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
			String ONE="1";
			YFCElement eleOrderMessage=inXml.getDocumentElement();
			YFCElement eleOrder= eleOrderMessage.getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
			if(eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO)!=null) {
				docGetINDGMsgSeqNoList=YFCDocument.createDocument(XMLLiterals.GET_INDG_MSG_SEQ_NO_LIST);
			YFCElement eleGetINDGMsgSeqNoList=docGetINDGMsgSeqNoList.getDocumentElement();
			eleGetINDGMsgSeqNoList.setAttribute(XMLLiterals.SAP_ORDER_NO,eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
			eleGetINDGMsgSeqNoList.setAttribute(XMLLiterals.ORDER_NO, eleOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO));
			if(!XmlUtils.isVoid(invokeYantraApi("getINDGMsgSeqNoList",docGetINDGMsgSeqNoList)))
				eleGetINDGMsgSeqNoList.setAttribute("SterlingToSterlingToSAPMessageSequenceNumber", Integer.parseInt(eleOrderMessage.getAttribute("SterlingToSterlingToSAPMessageSequenceNumber"))+1);
			
			else {
				docGetINDGMsgSeqNoList=YFCDocument.createDocument(XMLLiterals.CREATE_INDG_MSG_SEQ_NO);
				YFCElement eleCreateINDGMsgSeqNo=docGetINDGMsgSeqNoList.getDocumentElement();
				eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.SAP_MSG_SEQ_NO,ONE);
				eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.ORDER_NO,eleOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO));
				eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.SAP_ORDER_NO,eleOrder.getAttribute(XMLLiterals.SAP_ORDER_NO));
				eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.DOCUMENT_TYPE, eleOrder.getAttribute(XMLLiterals.DOCUMENT_TYPE));
				eleCreateINDGMsgSeqNo.setAttribute(XMLLiterals.ENTERPRISE_CODE,eleOrder.getAttribute(XMLLiterals.ENTERPRISE_CODE));
				
			}
				}
			System.out.println(docGetINDGMsgSeqNoList+"*****");
			return docGetINDGMsgSeqNoList;
		}
			
	  
	  

}
