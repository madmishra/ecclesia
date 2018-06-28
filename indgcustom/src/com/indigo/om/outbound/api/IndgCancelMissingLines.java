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
    private static final String ZERO_QTY = "0";
    private static final String IS_SAP_MSG_REQ = "SAP051MsgReq";
    private static final String FLAG_NO = "N";
    private String cancellationReqId = "SAP051";
    private static final String CANCELLATION_TYPE = "SAP";
    private static final String REASON_CODE = "03";
    private static final String CALL_LEGACYOMS003_SERVICE = "CALL_LEGACYOMS003_SERVICE";
    private static final String CANCELLED = "9000";
    private static final String NO = "N";
    private static final String YES = "Y";
    private String isFullOrderCancelled = "";
    private static final String CALL_SAP051_SERVICE = "Indg_SAP051_FullOdrCancelled"; 
    private static final String CALL_LEGACYOMS051_SERVICE = "Indg_Leg052_FullOdrCancelled";
    private static final String CANCEL_STATUS = "CANCEL";
    private static final String DEFAULT = "DEFAULT";
    private static final String ONE = "1";
    

    /**
      * This is the invoke point of the Service
      * @throws  
      * 
      */
    
    @Override
    public YFCDocument invoke(YFCDocument inXml) {
    	System.out.println(inXml + "xsjhdjshh");
    	YFCDocument docGetOrderLineList1 = getOrderLineListFunc(inXml);
    	System.out.println(docGetOrderLineList1 + "klakskuiy");
    	docIsFullOrderCancelled(inXml, docGetOrderLineList1);
    	System.out.println(inXml + "auyuigds");
    	String inputDocString = inXml.toString();
	    YFCDocument docLegacy003NoOdrLinesInp = YFCDocument.getDocumentFor(inputDocString);
	    System.out.println(docLegacy003NoOdrLinesInp + "ahhuidys");
	    docLegacy003NoOrderLines(docLegacy003NoOdrLinesInp);
    	YFCDocument docGoodLinesSAP002 = docOrderLineCancelled(inXml, docGetOrderLineList1);
    	System.out.println(docGoodLinesSAP002 + "sahsuiwdd");
    	YFCDocument docGetOrderLineList2 = getOrderLineListFunc(docGoodLinesSAP002);
    	System.out.println(docGetOrderLineList2 + "jsaiuydg");
    	String modifyTs = docGetOrderLineList2.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
				getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.MODIFYTS);
    	docGoodLinesSAP002.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, modifyTs);
		manageOrderCancellation(docGoodLinesSAP002, docGetOrderLineList2);
		System.out.println(docGoodLinesSAP002 + "ajhjusa");
		callLegacy003opQueue(docGoodLinesSAP002);
		docScheduleOrderLineInput(docGoodLinesSAP002);
		YFCDocument docGetOrderLineList3 = getOrderLineListFunc(docGoodLinesSAP002);
		docIsFullOrderCancelled(docGoodLinesSAP002, docGetOrderLineList3);
		docLegacy003NoOrderLines(docGoodLinesSAP002);
    	return null;	
    }
      
    public YFCDocument getOrderLineListInput(YFCDocument inXml) {
          YFCElement eleInXmlOrder = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
          		getChildElement(XMLLiterals.ORDER);
          String sOrderNo = eleInXmlOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO);
          String sEnterpriseCode = eleInXmlOrder.getAttribute(XMLLiterals.ENTERPRISE_CODE);
          String sDocumentType = eleInXmlOrder.getAttribute(XMLLiterals.DOCUMENT_TYPE);
          YFCElement eleOrderLine = eleInXmlOrder.getChildElement(XMLLiterals.ORDER_LINES).getChildElement(XMLLiterals.ORDER_LINE);
          String sShipnode = eleOrderLine.getAttribute(XMLLiterals.SHIPNODE);
          YFCDocument docGetOrderLineList1 = YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
          docGetOrderLineList1.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, sShipnode);
          YFCElement eleOrder = docGetOrderLineList1.getDocumentElement().createChild(XMLLiterals.ORDER);
          eleOrder.setAttribute(XMLLiterals.ORDER_NO, sOrderNo);
          eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, sEnterpriseCode);
          eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, sDocumentType);
          return docGetOrderLineList1;
    }
      
    public YFCDocument getOrderLineListTemplate() {
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
          eleOrderLine.setAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO, EMPTY_STRING);
  	      eleOrderLine.setAttribute(XMLLiterals.CUSTOMER_PO_NO, EMPTY_STRING);
          YFCElement eleItemEle = eleOrderLine.createChild(XMLLiterals.ITEM);
          eleItemEle.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
          YFCElement eleOrder = eleOrderLine.createChild(XMLLiterals.ORDER);
          eleOrder.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
          eleOrderLine.setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
          eleOrder.setAttribute(XMLLiterals.ORDER_TYPE, EMPTY_STRING);
          eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
          eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
          YFCElement eleOrderStatus = eleOrderLine.createChild(XMLLiterals.ORDER_STATUSES);
          YFCElement eleStatus = eleOrderStatus.createChild(XMLLiterals.ORDER_STATUS);
          eleStatus.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
          return docGetOrderListTemp;
    }
      
    public YFCDocument getOrderLineListFunc(YFCDocument inXml){
          return  invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST, getOrderLineListInput(inXml), getOrderLineListTemplate());
    }
      
    private void docIsFullOrderCancelled(YFCDocument inXml, YFCDocument docGetOrderLineList) {
  		YFCIterable<YFCElement> inputOrderLineEle = docGetOrderLineList.getDocumentElement().getChildren(XMLLiterals.ORDER_LINE);
  		for(YFCElement orderLineElement : inputOrderLineEle) {
  			String orderLineStatus = orderLineElement.getChildElement(XMLLiterals.ORDER_STATUSES).
  					getChildElement(XMLLiterals.ORDER_STATUS).getAttribute(XMLLiterals.STATUS);
  			if(!orderLineStatus.equals(CANCELLED))
  			{ isFullOrderCancelled = NO;
  				break; }
  			else
  			{ isFullOrderCancelled = YES; }
  		}	
  		if(isFullOrderCancelled.equals(NO)) {
  			inXml.getDocumentElement().setAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED, isFullOrderCancelled);
  		}
  		else
  			inXml.getDocumentElement().setAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED, isFullOrderCancelled);
  	}
    
    private YFCDocument docOrderLineCancelled(YFCDocument inXml, YFCDocument docGetOrderLineList1) {
    	String fullOdrCancel = inXml.getDocumentElement().getAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED);
    	YFCDocument docOrderHeaderAttr = YFCDocument.createDocument(XMLLiterals.ORDER);
    	docOrderHeaderAttr.getDocumentElement().setAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED, fullOdrCancel);
    	docOrderHeaderAttr.getDocumentElement().setAttribute(XMLLiterals.LEGACY_OMS_CANCELLATION_REQ_ID, cancellationReqId);
    	YFCElement odrLinesEle = docOrderHeaderAttr.getDocumentElement().createChild(XMLLiterals.ORDER_LINES);
    	YFCIterable<YFCElement> yfsItrator = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
    			getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES).getChildren(XMLLiterals.ORDER_LINE);
 	    for(YFCElement orderLineEle : yfsItrator) {
 	    	String primeLineNo = orderLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO);
	    	YFCElement orderLine = XPathUtil.getXPathElement(docGetOrderLineList1, "/OrderLineList/OrderLine[@PrimeLineNo = \""+
	    	primeLineNo+"\"]");
	    	String status =  orderLine.getChildElement(XMLLiterals.ORDER_STATUSES).getChildElement(XMLLiterals.ORDER_STATUS).
	    			getAttribute(XMLLiterals.STATUS);
	    	if(status.equals(CANCELLED)) {
	    		odrLinesEle.importNode(orderLine);
	    		YFCNode parent = orderLineEle.getParentNode();
			    parent.removeChild(orderLineEle);
	    	} 
 	    }
 	    if(!XmlUtils.isVoid(odrLinesEle)) {
 	    	System.out.println(docOrderHeaderAttr + "xndsjkdhg");
 	    	callSAP051opQueue(docOrderHeaderAttr);
 	    	callLegacyOMS052opQueue(docOrderHeaderAttr);
 	    }
 	   System.out.println(inXml + "zjahsa");
 	   return inXml;
    }
    
    private void callSAP051opQueue(YFCDocument doc) {
	     invokeYantraService(getProperty(CALL_SAP051_SERVICE), doc);
	}
	
	private void callLegacyOMS052opQueue(YFCDocument doc) {
	     invokeYantraService(getProperty(CALL_LEGACYOMS051_SERVICE), doc);
	}
	
	private void docLegacy003NoOrderLines(YFCDocument docLegacy003NoOdrLinesInp) {
		String isFullOdrCancelled = docLegacy003NoOdrLinesInp.getDocumentElement().getAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED);
		if(isFullOdrCancelled.equals(YES)) {
			YFCIterable<YFCElement> yfsItratorOrderLine = docLegacy003NoOdrLinesInp.getDocumentElement().getChildElement(XMLLiterals.
					MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES).getChildren(XMLLiterals.ORDER_LINE);
			for(YFCElement orderLineEle : yfsItratorOrderLine) {
				YFCNode parent = orderLineEle.getParentNode();
			    parent.removeChild(orderLineEle);
			}
			System.out.println(docLegacy003NoOdrLinesInp + "gdgsayutq");
			callLegacy003opQueue(docLegacy003NoOdrLinesInp);
		}
	}
	
	private void callLegacy003opQueue(YFCDocument doc) {
	     invokeYantraService(getProperty(CALL_LEGACYOMS003_SERVICE), doc);
	}
	
	public YFCDocument manageOrderCancellation(YFCDocument inXml, YFCDocument docGetOrderLineList2) {
        YFCElement eleOrderLineList = docGetOrderLineList2.getDocumentElement();
        YFCIterable<YFCElement> apiPrimeLineNo = eleOrderLineList.getChildren(XMLLiterals.ORDER_LINE);
        YFCDocument cancelLineDoc = changeOrderInput(docGetOrderLineList2);
        YFCDocument cancelLineMsgDoc = changeOrderInput(docGetOrderLineList2);
        YFCElement orderLines = cancelLineDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
        YFCElement msgOrderLines = cancelLineMsgDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
        for(YFCElement eleOrderLine : apiPrimeLineNo) {
        	String sPrimeLineNo= eleOrderLine.getAttribute(XMLLiterals.PRIME_LINE_NO);
            YFCElement orderLineEle = XPathUtil.getXPathElement(inXml,"//OrderLines/OrderLine[@PrimeLineNo=\""+sPrimeLineNo+"\"]");
            if(XmlUtils.isVoid(orderLineEle) && !CANCELLED.equals(eleOrderLine.getChildElement(XMLLiterals.ORDER_STATUSES).
            		getChildElement(XMLLiterals.ORDER_STATUS).getAttribute(XMLLiterals.STATUS))) {
                eleOrderLine.setAttribute(XMLLiterals.ACTION, CANCEL_STATUS);
                eleOrderLine.setAttribute(XMLLiterals.ORDERED_QTY, ZERO_QTY);
                eleOrderLine.setAttribute(XMLLiterals.CONDITION_VARIABLE_1, cancellationReqId);
                eleOrderLine.setAttribute(XMLLiterals.CONDITION_VARIABLE_2, REASON_CODE);
                deleteChildNodes(eleOrderLine);
                orderLines.importNode(eleOrderLine);
                addOrderInfomrationForSAP(inXml,cancelLineDoc);
            } else if(CANCELLED.equals(eleOrderLine.getChildElement(XMLLiterals.ORDER_STATUSES).
            		getChildElement(XMLLiterals.ORDER_STATUS).getAttribute(XMLLiterals.STATUS))){
              msgOrderLines.importNode(eleOrderLine);
            }
        }
        if(orderLines.hasChildNodes()) {
            invokeYantraApi(XMLLiterals.CHANGE_ORDER_API, cancelLineDoc);
        } else {
          cancelLineDoc.getDocumentElement().setAttribute(IS_SAP_MSG_REQ,FLAG_NO);
        }
        if(msgOrderLines.hasChildNodes()) {
          invokeYantraService("Indg_OnCancelEvent", cancelLineMsgDoc);
        }
        cancelLineDoc.getDocumentElement().setAttribute(XMLLiterals.IS_FULL_ORDER_CANCELLED,FLAG_NO);
        return cancelLineDoc;
    }
	
	private YFCDocument changeOrderInput(YFCDocument docGetOrderLineList2) {
        YFCElement orderEle = docGetOrderLineList2.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
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
	
	private void addOrderInfomrationForSAP(YFCDocument inXml, YFCDocument docInputChangeOrderAPI) {
        String sOrderType=inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER)
        		.getAttribute(XMLLiterals.ORDER_TYPE);
        String sapOrderNo = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER)
            .getAttribute(XMLLiterals.SAP_ORDER_NO);
        docInputChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.ORDER_TYPE, sOrderType);
        docInputChangeOrderAPI.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES)
        .getChildElement(XMLLiterals.ORDER_LINE).setAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO, sapOrderNo);
        docInputChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.MODIFICATION_REFRENCE_1, CANCELLATION_TYPE);
    }
	
	private void deleteChildNodes(YFCElement eleOrderLine) {
	      YFCElement orderEle = eleOrderLine.getChildElement(XMLLiterals.ORDER);
	      YFCNode parent = orderEle.getParentNode();
	      parent.removeChild(orderEle);
	      YFCElement statusEle = eleOrderLine.getChildElement(XMLLiterals.ORDER_STATUSES);
	      parent = statusEle.getParentNode();
	      parent.removeChild(statusEle);
	      orderEle.removeAttribute(XMLLiterals.STATUS);
	}

	private void docScheduleOrderLineInput(YFCDocument inXml) {
		YFCDocument docScheduleOrderLine = YFCDocument.createDocument(XMLLiterals.PROMISE);
		YFCElement elePromise = docScheduleOrderLine.getDocumentElement();
		YFCElement eleOrder = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
		elePromise.setAttribute(XMLLiterals.ALLOCATION_RULE_ID, DEFAULT);
		elePromise.setAttribute(XMLLiterals.CHECK_INVENTORY, YES);
		elePromise.setAttribute(XMLLiterals.DOCUMENT_TYPE, eleOrder.getAttribute(XMLLiterals.DOCUMENT_TYPE));
		elePromise.setAttribute(XMLLiterals.ENTERPRISE_CODE, eleOrder.getAttribute(XMLLiterals.ENTERPRISE_CODE));
		elePromise.setAttribute(XMLLiterals.IGNORE_RELEASE_DATE, YES);
		elePromise.setAttribute(XMLLiterals.ORDER_NO, eleOrder.getAttribute(XMLLiterals.STERLING_ORDER_NO));
		elePromise.setAttribute(XMLLiterals.SCHEDULE_AND_RELEASE, YES);
		YFCElement elePromiseLines = elePromise.createChild(XMLLiterals.PROMISE_LINES);
	    YFCIterable<YFCElement> yfsItrator = eleOrder.getChildElement(XMLLiterals.ORDER_LINES).getChildren(XMLLiterals.ORDER_LINE);
	    for(YFCElement eleOrderLine : yfsItrator) {
	    	YFCElement elePromiseLine = elePromiseLines.createChild(XMLLiterals.PROMISE_LINE);
	    	elePromiseLine.setAttribute(XMLLiterals.DELIVERY_DATE, eleOrder.getAttribute(XMLLiterals.ORDER_DATE));
	    	elePromiseLine.setAttribute(XMLLiterals.PRIME_LINE_NO, eleOrderLine.getAttribute(XMLLiterals.PRIME_LINE_NO));
	    	elePromiseLine.setAttribute(XMLLiterals.QUANTITY, eleOrderLine.getAttribute(XMLLiterals.ORDERED_QTY));
	    	elePromiseLine.setAttribute(XMLLiterals.SHIPNODE, eleOrderLine.getAttribute(XMLLiterals.SHIPNODE));
	    	elePromiseLine.setAttribute(XMLLiterals.SUB_LINE_NO, ONE);
	    }
	    invokeYantraApi(XMLLiterals.SCHEDULE_ORDER_LINES, docScheduleOrderLine);  
	}
}
    