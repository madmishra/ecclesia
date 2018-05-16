package com.indigo.om.outbound.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class CancelMissingLines extends AbstractCustomApi{
	YFCDocument inputDocForChangeOrderAPIDOC=null;
	
	private static final String EMPTY_STRING = "";
	private List<String> lineList1;
	private List<String> lineList2;
	private static final String PRIMELINE_STATUS = "9000";
	private static final String ACTION_STATUS = "CANCEL";

	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		System.out.println("INSIDE PROGRAM");
		YFCDocument getOrderLineListDoc = getOrderLineListFunc(inXml);
		System.out.println(getOrderLineListDoc + "Final Document");
		getPrimeLineNoFromBothDoc(inXml, getOrderLineListDoc);
		System.out.println(inputDocForChangeOrderAPIDOC + "FINAL DOC");
		return inputDocForChangeOrderAPIDOC;
	}
	
	public YFCDocument getOrderLineListInDoc(YFCDocument inXml) {
		YFCElement inEle = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
		String orderNo = inEle.getAttribute(XMLLiterals.STERLING_ORDER_NO);
		String enterpriseCode = inEle.getAttribute(XMLLiterals.ENTERPRISE_CODE);
		String documentType = inEle.getAttribute(XMLLiterals.DOCUMENT_TYPE);
		YFCElement orderLineEle = inEle.getChildElement(XMLLiterals.ORDER_LINES).getChildElement(XMLLiterals.ORDER_LINE);
		String shipnode = orderLineEle.getAttribute(XMLLiterals.SHIPNODE);
	    YFCDocument getOrderDoc = YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
	    getOrderDoc.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, shipnode);
	    YFCElement orderEle = getOrderDoc.getDocumentElement().createChild(XMLLiterals.ORDER);
	    orderEle.setAttribute(XMLLiterals.ORDER_NO, orderNo);
	    orderEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCode);
	    orderEle.setAttribute(XMLLiterals.DOCUMENT_TYPE, documentType);
	    System.out.println("<<<<<<<<<<<<<<<<<<getOrderLineListInDoc>>>>>>>>>>>>>>>>>>>>>>"+getOrderDoc);
	    return getOrderDoc;
	  }
	
	public YFCDocument getOrderLineListTemplateDoc() {
	    YFCDocument getOrderListTemp = YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
	    YFCElement orderLineEle = getOrderListTemp.getDocumentElement().createChild(XMLLiterals.ORDER_LINE);
	    orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.SUB_LINE_NO, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.ORDER_HEADER_KEY, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.ORDERED_QTY, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY, EMPTY_STRING);
	    YFCElement extnEle = getOrderListTemp.getDocumentElement().createChild(XMLLiterals.EXTN);
	    extnEle.setAttribute(XMLLiterals.EXTN_LEGACY_OMS_CHILD_ORDERNO, EMPTY_STRING);
	    extnEle.setAttribute(XMLLiterals.EXTN_SAP_ORDER_NO, EMPTY_STRING);
	    YFCElement orderEle = getOrderListTemp.getDocumentElement().createChild(XMLLiterals.ORDER);
	    orderEle.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
	    orderEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
	    orderEle.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
	    YFCElement orderStatusEle = getOrderListTemp.getDocumentElement().createChild(XMLLiterals.ORDER_STATUSES);
	    YFCElement statusEle = orderStatusEle.createChild(XMLLiterals.ORDER_STATUS);
	    statusEle.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
	    System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<getOrderLineListTemplateDoc>>>>>>>>>>>>>>>>>>>"+getOrderListTemp);
	    
	    return getOrderListTemp;
	  }
	
	public YFCDocument getOrderLineListFunc(YFCDocument inXml){
	    return  invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST, getOrderLineListInDoc(inXml), getOrderLineListTemplateDoc());
	 }
	
	public void getPrimeLineNoFromBothDoc(YFCDocument inXml, YFCDocument getOrderLineListDoc) {
		YFCElement orderLineListEle = getOrderLineListDoc.getDocumentElement();
		YFCIterable<YFCElement> apiPrimeLineNo = orderLineListEle.getChildren();
		System.out.println("<<<<<<<<<<<<<<<<orderLineListEle>>>>>>>>>>>>"+orderLineListEle);
		for(YFCElement primeLineEle1:apiPrimeLineNo) {
			String primeLineNo= primeLineEle1.getAttribute(XMLLiterals.PRIME_LINE_NO);
			System.out.println("primeLineNo IS:"+primeLineNo);
			lineList1.add("primeLineNo");
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<lineList1>>>>>>>>>>>>"+lineList1);
		}
		YFCElement sapLineListEle = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
		System.out.println("<<<<<<<<<<<<<<<<<sapLineListEle>>>>>>"+sapLineListEle);
		YFCIterable<YFCElement> inputLineListEle = sapLineListEle.getChildren();
		System.out.println("<<<<<<<<<<<<<inputLineListEle>>>>>>>>>>"+inputLineListEle);
		for(YFCElement primeLineEle2:inputLineListEle) {
			String primeLineNo= primeLineEle2.getAttribute(XMLLiterals.PRIME_LINE_NO);
			System.out.println("<<<<<<<<<<<<<<primeLineNo 222222222>>>>>>>>>>>>>>>"+primeLineNo);
			lineList2.add("primeLineNo");
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<lineList2>>>>>>>>>>>>"+lineList2);
		}
		removeCommonPrimeLineNo(lineList1, lineList2, getOrderLineListDoc, inXml);
	}
	
	public void removeCommonPrimeLineNo(List<String> lineList1, List<String> lineList2, YFCDocument getOrderLineListDoc,
			YFCDocument inXml) {
		Collection<String> list=new ArrayList<>();
		List<String> union=new ArrayList<>(lineList2);
		union.addAll(lineList1);
		List<String> common=new ArrayList<>(lineList2);
		common.retainAll(lineList1);
		union.removeAll(common);
		for(String i: union)
		{
			list.add(i);
			
		}
		for(String i:list) {
			System.out.println(i);
		}
		cancelMissingPrimeLineNo(list, getOrderLineListDoc, inXml);
	}
	
	private void cancelMissingPrimeLineNo(Collection<String> list, YFCDocument getOrderLineListDoc, YFCDocument inXml) {
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<LISt VALue>>>>>>>>>>"+list);
		for(String primeLineNoValue:list) {
			YFCElement getOrderLineListEle = XPathUtil.getXPathElement(getOrderLineListDoc, 
					"/OrderLineList/OrderLine[@PrimeLineNo = \""+primeLineNoValue+"\"]");
			if(!XmlUtils.isVoid(getOrderLineListEle)) {
				String status = getOrderLineListEle.getChildElement(XMLLiterals.ORDER_LINE).
						getChildElement(XMLLiterals.ORDER_STATUSES).getChildElement(XMLLiterals.ORDER_STATUS).
						getAttribute(XMLLiterals.STATUS);
				if(!PRIMELINE_STATUS.equals(status)) {
					String orderHeaderKey = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
							getAttribute(XMLLiterals.ORDER_HEADER_KEY);
					String orderNumber = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
							getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.ENTERPRISE_CODE);
					String enterpriseCodeVal = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
							getChildElement(XMLLiterals.EXTN).getAttribute(XMLLiterals.EXTN_SAP_ORDER_NO);
					String documentTypeVal = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
							getChildElement(XMLLiterals.EXTN).getAttribute(XMLLiterals.DOCUMENT_TYPE);
					String subLineNo = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
							getAttribute(XMLLiterals.SUB_LINE_NO);
					
					YFCDocument inputDocForChangeOrderAPI = YFCDocument.createDocument(XMLLiterals.ORDER);
					inputDocForChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.ORDER_HEADER_KEY, orderHeaderKey);
					inputDocForChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.ORDER_NO, orderNumber);
					inputDocForChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCodeVal);
					inputDocForChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, documentTypeVal);
					YFCElement orderLines = inputDocForChangeOrderAPI.createElement(XMLLiterals.ORDER_LINES);
					YFCElement orderLine = orderLines.createChild(XMLLiterals.ORDER_LINE);
					orderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, primeLineNoValue);
					orderLine.setAttribute(XMLLiterals.SUB_LINE_NO, subLineNo);
					orderLine.setAttribute(XMLLiterals.ACTION, ACTION_STATUS);
					System.out.println(inputDocForChangeOrderAPI + "ForChangeOrderDoc");
					YFCDocument changeOrderOutputDoc = invokeYantraApi(XMLLiterals.CHANGE_ORDER_API, inputDocForChangeOrderAPI, 
							changeOrderTemplateDoc());	
					System.out.println(changeOrderOutputDoc + "both prime lines");
	    			sendCancelledPrimeLineNoDoc(changeOrderOutputDoc, inXml, inputDocForChangeOrderAPI);
				}
			}
		}
		
	}
	
	private void sendCancelledPrimeLineNoDoc(YFCDocument changeOrderOutputDoc, YFCDocument inXml, 
			YFCDocument inputDocForChangeOrderAPI) {
		String modifyTs = changeOrderOutputDoc.getDocumentElement().getAttribute(XMLLiterals.MODIFYTS);
		System.out.println("<<<<<<<<<<<<<<MODIFYTS>>>>>>>>>>"+modifyTs);
		String childOrderNo = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.RELEASE_NO);
		inputDocForChangeOrderAPI.getDocumentElement().getChildElement(XMLLiterals.ORDER).
		setAttribute(XMLLiterals.MODIFYTS, modifyTs);
		inputDocForChangeOrderAPI.getDocumentElement().getChildElement(XMLLiterals.ORDER).
		getChildElement(XMLLiterals.ORDER_LINES).getChildElement(XMLLiterals.ORDER_LINE).
		getChildElement(XMLLiterals.EXTN).setAttribute(XMLLiterals.EXTN_LEGACY_OMS_CHILD_ORDERNO, childOrderNo);
	
	}
	
	public YFCDocument changeOrderTemplateDoc() {
	    YFCDocument changeOrderInputDoc = YFCDocument.createDocument(XMLLiterals.ORDER);
	    changeOrderInputDoc.getDocumentElement().setAttribute(XMLLiterals.ORDER_HEADER_KEY, EMPTY_STRING);
	    changeOrderInputDoc.getDocumentElement().setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
	    changeOrderInputDoc.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
	    changeOrderInputDoc.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
	    YFCElement orderLinesEle = changeOrderInputDoc.getDocumentElement().createChild(XMLLiterals.ORDER_LINES);
	    YFCElement orderLineEle = orderLinesEle.createChild(XMLLiterals.ORDER_LINE);
	    orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.SUB_LINE_NO, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.ACTION, EMPTY_STRING);
	    YFCElement extnEle = orderLineEle.createChild(XMLLiterals.EXTN);
	    extnEle.setAttribute(XMLLiterals.EXTN_LEGACY_OMS_CHILD_ORDERNO, EMPTY_STRING);
	    System.out.println("<<<<<<<<<<<<<<<<<<CHANGE ORDER TEMPLATE>>>>>>>>>>>>"+changeOrderInputDoc);
	    return changeOrderInputDoc;
	  }
}
