package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;

/**
 * @author BSG168
 *
 * Custom API to consume SAP002 message from SAP and check if
 * there are orderLines to cancel. The orderLines that are present in 
 * the input XML will get created and if there are extra lines present 
 * for the order, those orderLines will get cancelled.
 */

public class IndgCancelMissingLines extends AbstractCustomApi{
  
    private static final String EMPTY_STRING = "";
    private static final String CANCEL_ORDER_STATUS = "9000";
    private static final String CANCEL_STATUS = "CANCEL";
    private static final String ZERO_QTY = "0";
    private static final String IS_SAP_MSG_REQ = "SAP051MsgReq";
    private static final String FLAG_NO = "N";
    private String cancellationReqId = "";
    private static final String CANCELLATION_TYPE = "SAP051";
    private static final String REASON_CODE = "06";

     /**
       * This is the invoke point of the Service
       * @throws  
       * 
       */
    
      @Override
    public YFCDocument invoke(YFCDocument docInXml) {
    		YFCDocument docGetOrderLineList = getOrderLineListFunc(docInXml);
    		cancellationReqId = docGetOrderLineList.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
    				getAttribute(XMLLiterals.CONDITION_VARIABLE_1);
    		return manageOrderCancellation(docInXml, docGetOrderLineList);
    }
      
     /**
      * this method forms input to getOrderLineList API
      * 
      * @param docInXml
      * @return
      */
    
    public YFCDocument docGetOrderLineListInput(YFCDocument docInXml) {
        YFCElement eleInXmlOrder = docInXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
        		getChildElement(XMLLiterals.ORDER);
        String sOrderNo = eleInXmlOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO);
        String sEnterpriseCode = eleInXmlOrder.getAttribute(XMLLiterals.ENTERPRISE_CODE);
        String sDocumentType = eleInXmlOrder.getAttribute(XMLLiterals.DOCUMENT_TYPE);
        YFCElement eleOrderLine = eleInXmlOrder.getChildElement(XMLLiterals.ORDER_LINES).getChildElement(XMLLiterals.ORDER_LINE);
        String sShipnode = eleOrderLine.getAttribute(XMLLiterals.SHIPNODE);
        YFCDocument docGetOrderLineList = YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
        docGetOrderLineList.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, sShipnode);
        YFCElement eleOrder = docGetOrderLineList.getDocumentElement().createChild(XMLLiterals.ORDER);
        eleOrder.setAttribute(XMLLiterals.ORDER_NO, sOrderNo);
        eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, sEnterpriseCode);
        eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, sDocumentType);
        return docGetOrderLineList;
      }
    
    /**
     * this method forms template for getOrderLineList API
     * @return
     */
    
    public YFCDocument docGetOrderLineListTemplate() {
        YFCDocument docGetOrderListTemp = YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
        YFCElement eleOrderLine = docGetOrderListTemp.getDocumentElement().createChild(XMLLiterals.ORDER_LINE);
        eleOrderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
        eleOrderLine.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
        eleOrderLine.setAttribute(XMLLiterals.SUB_LINE_NO, EMPTY_STRING);
        eleOrderLine.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
        eleOrderLine.setAttribute(XMLLiterals.ORDERED_QTY, EMPTY_STRING);
        eleOrderLine.setAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY, EMPTY_STRING);
        eleOrderLine.setAttribute(XMLLiterals.CONDITION_VARIABLE_1, EMPTY_STRING);
        eleOrderLine.setAttribute(XMLLiterals.CONDITION_VARIABLE_2, EMPTY_STRING);
        YFCElement eleItemEle = eleOrderLine.createChild(XMLLiterals.ITEM);
        eleItemEle.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
        YFCElement eleExtn = eleOrderLine.createChild(XMLLiterals.EXTN);
        eleExtn.setAttribute(XMLLiterals.EXTN_SAP_ORDER_NO, EMPTY_STRING);
        YFCElement eleOrder = eleOrderLine.createChild(XMLLiterals.ORDER);
        eleOrder.setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
        eleOrder.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
        eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
        eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
        YFCElement eleOrderStatus = eleOrderLine.createChild(XMLLiterals.ORDER_STATUSES);
        YFCElement eleStatus = eleOrderStatus.createChild(XMLLiterals.ORDER_STATUS);
        eleStatus.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
        return docGetOrderListTemp;
      }
    
    /**
     * 
     * this method invokes getOrderLineList API
     * @param docInXml
     * @return
     */
    
    public YFCDocument getOrderLineListFunc(YFCDocument docInXml){
        return  invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST, docGetOrderLineListInput(docInXml), docGetOrderLineListTemplate());
     }
    
   /**
    * this method fetches unique prime line no present in input XML and getOrderLineList API output
    * and invokes changeOrder API for those whose status is not cancelled
    * 
    * @param docInXml
    * @param docGetOrderLineList
    * @return
    */
    
    public YFCDocument manageOrderCancellation(YFCDocument docInXml, YFCDocument docGetOrderLineList) {
        YFCElement eleOrderLineList = docGetOrderLineList.getDocumentElement();
        YFCIterable<YFCElement> apiPrimeLineNo = eleOrderLineList.getChildren(XMLLiterals.ORDER_LINE);
        YFCDocument cancelLineDoc = changeOrderInput(docGetOrderLineList);
        YFCElement orderLines = cancelLineDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
        for(YFCElement eleOrderLine:apiPrimeLineNo) {
        	String sPrimeLineNo= eleOrderLine.getAttribute(XMLLiterals.PRIME_LINE_NO);
           YFCElement orderLineEle = XPathUtil.getXPathElement(docInXml,"//OrderLines/OrderLine[@PrimeLineNo=\""+sPrimeLineNo+"\"]");
            if(XmlUtils.isVoid(orderLineEle) && !CANCEL_ORDER_STATUS.equals(eleOrderLine.getAttribute(XMLLiterals.STATUS))) {
                eleOrderLine.setAttribute(XMLLiterals.ACTION, CANCEL_STATUS);
                eleOrderLine.setAttribute(XMLLiterals.ORDERED_QTY, ZERO_QTY);
                eleOrderLine.setAttribute(XMLLiterals.CONDITION_VARIABLE_1, cancellationReqId);
                eleOrderLine.setAttribute(XMLLiterals.CONDITION_VARIABLE_2, REASON_CODE);
                addOrderInfomrationForSAP(docInXml,cancelLineDoc,eleOrderLine);
                deleteChildNodes(eleOrderLine);
                orderLines.importNode(eleOrderLine);
            }
        }
        if(orderLines.hasChildNodes()) {
            invokeYantraApi(XMLLiterals.CHANGE_ORDER_API, cancelLineDoc);
        } else {
          cancelLineDoc.getDocumentElement().setAttribute(IS_SAP_MSG_REQ,FLAG_NO);
        }
        cancelLineDoc.getDocumentElement().setAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED,FLAG_NO);
        return cancelLineDoc;
    }
    
    /**
     * this method forms input for changeOrder API
     * 
     * @param docGetOrderLineList
     * @return
     */
    
    private YFCDocument changeOrderInput(YFCDocument docGetOrderLineList) {
        YFCElement orderEle = docGetOrderLineList.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
                getChildElement(XMLLiterals.ORDER);
        YFCDocument docInputChangeOrderAPI = YFCDocument.createDocument(XMLLiterals.ORDER);
        docInputChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.ORDER_NO, orderEle.getAttribute(XMLLiterals.ORDER_NO));
        docInputChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, 
                orderEle.getAttribute(XMLLiterals.ENTERPRISE_CODE));
        docInputChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, 
                orderEle.getAttribute(XMLLiterals.DOCUMENT_TYPE));
        docInputChangeOrderAPI.getDocumentElement().createChild(XMLLiterals.ORDER_LINES);
        return docInputChangeOrderAPI;
    }
    
    /**
     * this method forms document containing cancelled order line
     * 
     * @param docInXml
     * @param docInputChangeOrderAPI
     * @param eleOrderLine
     */
    
    private void addOrderInfomrationForSAP(YFCDocument docInXml, YFCDocument docInputChangeOrderAPI,YFCElement eleOrderLine) {
        String sModifyts = eleOrderLine.getChildElement(XMLLiterals.ORDER).
                getAttribute(XMLLiterals.MODIFYTS);
        String sOrderType=docInXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER)
        		.getAttribute(XMLLiterals.ORDER_TYPE);
        String sapOrderNo = eleOrderLine.getAttribute(XMLLiterals.EXTN_SAP_ORDER_NO);
        docInputChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, sModifyts);
        docInputChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.ORDER_TYPE, sOrderType);
        docInputChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO, sapOrderNo);
        docInputChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.MODIFICATION_REFRENCE_1, CANCELLATION_TYPE);
    }
    
    /**
     * Deletes unwanted child nodes from the ChangeOrder Input
     * 
     * @param eleOrderLine
     */
    
    private void deleteChildNodes(YFCElement eleOrderLine) {
      YFCElement orderEle = eleOrderLine.getChildElement(XMLLiterals.ORDER);
      YFCNode parent = orderEle.getParentNode();
      parent.removeChild(orderEle);
      YFCElement statusEle = eleOrderLine.getChildElement(XMLLiterals.ORDER_STATUSES);
      parent = statusEle.getParentNode();
      parent.removeChild(statusEle);
      orderEle.removeAttribute(XMLLiterals.STATUS);
    }
}
    