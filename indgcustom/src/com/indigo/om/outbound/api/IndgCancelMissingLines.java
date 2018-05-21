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

/**
 * 
 * 
 * @author BSG168
 *
 */
public class IndgCancelMissingLines extends AbstractCustomApi{
	YFCDocument inputDocForChangeOrderAPI=null;
	
	private static final String EMPTY_STRING = "";
	
	private static final String PRIMELINE_STATUS = "9000";
	private static final String ACTION_STATUS = "CANCEL";

	/**
	   * This is the invoke point of the Service
	 * @throws  
	   * 
	   */
	  @Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCDocument getOrderLineListDoc = getOrderLineListFunc(inXml);
		getPrimeLineNo(inXml, getOrderLineListDoc);
		
		return inputDocForChangeOrderAPI;
	}
	  /**
	   * this method forms input to getOrderLineList API
	   * @param inXml
	   * @return
	   */
	
	public YFCDocument getOrderLineListInDoc(YFCDocument inXml) {
		YFCElement inXmlOrderEle = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
		String orderNo = inXmlOrderEle.getAttribute(XMLLiterals.STERLING_ORDER_NO);
		String enterpriseCode = inXmlOrderEle.getAttribute(XMLLiterals.ENTERPRISE_CODE);
		String documentType = inXmlOrderEle.getAttribute(XMLLiterals.DOCUMENT_TYPE);
		YFCElement orderLineEle = inXmlOrderEle.getChildElement(XMLLiterals.ORDER_LINES).getChildElement(XMLLiterals.ORDER_LINE);
		String shipnode = orderLineEle.getAttribute(XMLLiterals.SHIPNODE);
	    YFCDocument getOrderLineListDoc = YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
	    getOrderLineListDoc.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, shipnode);
	    YFCElement orderEle = getOrderLineListDoc.getDocumentElement().createChild(XMLLiterals.ORDER);
	    orderEle.setAttribute(XMLLiterals.ORDER_NO, orderNo);
	    orderEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCode);
	    orderEle.setAttribute(XMLLiterals.DOCUMENT_TYPE, documentType);
	    return getOrderLineListDoc;
	  }
	/**
	 * this method forms template for getOrderLineList API
	 * @return
	 */
	
	public YFCDocument getOrderLineListTemplateDoc() {
	    YFCDocument getOrderListTemp = YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
	    YFCElement orderLineEle = getOrderListTemp.getDocumentElement().createChild(XMLLiterals.ORDER_LINE);
	    orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.SUB_LINE_NO, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.ORDERED_QTY, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY, EMPTY_STRING);
	    YFCElement itemEle = orderLineEle.createChild(XMLLiterals.ITEM);
	    itemEle.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
	    YFCElement extnEle = orderLineEle.createChild(XMLLiterals.EXTN);
	    extnEle.setAttribute(XMLLiterals.EXTN_LEGACY_OMS_CHILD_ORDERNO, EMPTY_STRING);
	    extnEle.setAttribute(XMLLiterals.EXTN_SAP_ORDER_NO, EMPTY_STRING);
	    YFCElement orderEle = orderLineEle.createChild(XMLLiterals.ORDER);
	    orderEle.setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
	    orderEle.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
	    orderEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
	    orderEle.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
	    YFCElement orderStatusEle = orderLineEle.createChild(XMLLiterals.ORDER_STATUSES);
	    YFCElement statusEle = orderStatusEle.createChild(XMLLiterals.ORDER_STATUS);
	    statusEle.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
	    
	    return getOrderListTemp;
	  }
	/**
	 * this method invokes getOrderLineList API
	 * @param inXml
	 * @return
	 */
	
	public YFCDocument getOrderLineListFunc(YFCDocument inXml){
	    return  invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST, getOrderLineListInDoc(inXml), getOrderLineListTemplateDoc());
	 }
	
	/**
	 * this method fetches common primeline no present in input Xml and getOrderLineList api output
	 * @param inXml
	 * @param getOrderLineListDoc
	 */
	public void getPrimeLineNo(YFCDocument inXml, YFCDocument getOrderLineListDoc) {
	 List<String> inXmlLineList=new ArrayList<String>();
	 List<String> getOrderLinesList=new ArrayList<String>();
		YFCElement orderLineListEle = getOrderLineListDoc.getDocumentElement();
		YFCIterable<YFCElement> apiPrimeLineNo = orderLineListEle.getChildren();
		for(YFCElement primeLineEle1:apiPrimeLineNo) {
			String primeLineNo= primeLineEle1.getAttribute(XMLLiterals.PRIME_LINE_NO);
			inXmlLineList.add(primeLineNo);
	
		}
		YFCElement sapLineListEle = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
		YFCIterable<YFCElement> inputLineListEle = sapLineListEle.getChildren();
		for(YFCElement primeLineEle2:inputLineListEle) {
			String primeLineNo= primeLineEle2.getAttribute(XMLLiterals.PRIME_LINE_NO);
			getOrderLinesList.add(primeLineNo);
		}
		removeCommonPrimeLineNo(inXmlLineList, getOrderLinesList, getOrderLineListDoc, inXml);
	}
	/**
	 * this method removes common primeline no
	 * @param inXmlLineList
	 * @param getOrderLinesList
	 * @param getOrderLineListDoc
	 * @param inXml
	 */
	
	public void removeCommonPrimeLineNo(List<String> inXmlLineList, List<String> getOrderLinesList, YFCDocument getOrderLineListDoc,
			YFCDocument inXml) {
		Collection<String> primeLineNoList=new ArrayList<String>();
		List<String> union=new ArrayList<>(getOrderLinesList);
		union.addAll(inXmlLineList);
		List<String> common=new ArrayList<>(getOrderLinesList);
		common.retainAll(inXmlLineList);
		union.removeAll(common);
		for(String i: union)
		{
			primeLineNoList.add(i);
			
		}
		cancelMissingPrimeLineNo(primeLineNoList, getOrderLineListDoc, inXml);
	}
	/**
	 * this method cancels the missing primeline no
	 * @param primeLineNoList
	 * @param getOrderLineListDoc
	 * @param inXml
	 */
	private void cancelMissingPrimeLineNo(Collection<String> primeLineNoList, YFCDocument getOrderLineListDoc, YFCDocument inXml) {
		for(String primeLineNoValue:primeLineNoList) {
			YFCElement getOrderLineListEle = XPathUtil.getXPathElement(getOrderLineListDoc, 
					"/OrderLineList/OrderLine[@PrimeLineNo = \""+primeLineNoValue+"\"]");
			if(!XmlUtils.isVoid(getOrderLineListEle)) {
				String status = getOrderLineListEle.getChildElement(XMLLiterals.ORDER_STATUSES).
						getChildElement(XMLLiterals.ORDER_STATUS).getAttribute(XMLLiterals.STATUS);
				if(!PRIMELINE_STATUS.equals(status)) {
					changeOrderInput(getOrderLineListDoc,primeLineNoValue);
				}
			}
		}
		sendCancelledPrimeLineNoDoc(inXml, inputDocForChangeOrderAPI,getOrderLineListDoc);
	}
	
	/**
	 * this method forms input for changeOrder api
	 * @param getOrderLineListDoc
	 * @param primeLineNoValue
	 */
	private void changeOrderInput(YFCDocument getOrderLineListDoc,String primeLineNoValue) {
		String orderNumber = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
				getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.ORDER_NO);
		String enterpriseCodeVal = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
				getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.ENTERPRISE_CODE);
		String documentTypeVal = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
				getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.DOCUMENT_TYPE);
		String subLineNo = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
				getAttribute(XMLLiterals.SUB_LINE_NO);
		
		inputDocForChangeOrderAPI = YFCDocument.createDocument(XMLLiterals.ORDER);
		inputDocForChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.ORDER_NO, orderNumber);
		inputDocForChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCodeVal);
		inputDocForChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, documentTypeVal);
		YFCElement orderLines = inputDocForChangeOrderAPI.getDocumentElement().createChild(XMLLiterals.ORDER_LINES);
		YFCElement orderLine = orderLines.createChild(XMLLiterals.ORDER_LINE);
		orderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, primeLineNoValue);
		orderLine.setAttribute(XMLLiterals.SUB_LINE_NO, subLineNo);
		orderLine.setAttribute(XMLLiterals.ACTION, ACTION_STATUS);
		invokeYantraApi(XMLLiterals.CHANGE_ORDER_API, inputDocForChangeOrderAPI);
	}
	
	/**
	 * this method forms document containing cancelled order line
	 * @param inXml
	 * @param inputDocForChangeOrderAPI
	 * @param getOrderLineListDoc
	 * @return
	 */
	private YFCDocument sendCancelledPrimeLineNoDoc(YFCDocument inXml, YFCDocument inputDocForChangeOrderAPI,YFCDocument getOrderLineListDoc) {
		YFCElement orderLineEle=getOrderLineListDoc.getDocumentElement();
		String modifyTs = orderLineEle.getChildElement(XMLLiterals.ORDER_LINE).getChildElement(XMLLiterals.ORDER).
				getAttribute(XMLLiterals.MODIFYTS);
		cancellingLine(getOrderLineListDoc,orderLineEle);
		String childOrderNo = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.RELEASE_NO);
		String sapOrderNo=inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.SAP_ORDER_NO);
		inputDocForChangeOrderAPI.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, modifyTs);
		YFCElement extnEle = inputDocForChangeOrderAPI.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES).
				getChildElement(XMLLiterals.ORDER_LINE).createChild(XMLLiterals.EXTN);
		extnEle.setAttribute(XMLLiterals.EXTN_LEGACY_OMS_CHILD_ORDERNO, childOrderNo);
		extnEle.setAttribute(XMLLiterals.EXTN_LEGACY_OMS_CHILD_ORDERNO, sapOrderNo);
		return inputDocForChangeOrderAPI;
	}
	/**
	 * this method cancel the orderLine 
	 * @param getOrderLineListDoc
	 * @param orderLineEle
	 */
	private void cancellingLine(YFCDocument getOrderLineListDoc,YFCElement orderLineEle) {
	
		YFCIterable<YFCElement> getOrderLineEle = orderLineEle.getChildren();
	    for(YFCElement orderLine: getOrderLineEle) {
	    	String primeLineNo1 = orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO);
	    	YFCIterable<YFCElement> changeOrderLineEle = inputDocForChangeOrderAPI.getDocumentElement().
	    			getChildElement(XMLLiterals.ORDER_LINES).getChildren();
	    	for(YFCElement changeOrderLine: changeOrderLineEle) {
	    		String primeLineNo2 = changeOrderLine.getAttribute(XMLLiterals.PRIME_LINE_NO);
	    		
	    		if(primeLineNo1.equals(primeLineNo2)) {
	    			String currentQty = "0";
	    			String originalQty = getOrderLineListDoc.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE)
	    					.getAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY);
	    			inputDocForChangeOrderAPI.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES).
	    			getChildElement(XMLLiterals.ORDER_LINE).setAttribute(XMLLiterals.CURRENT_QTY, currentQty);
	    			inputDocForChangeOrderAPI.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES).
	    			getChildElement(XMLLiterals.ORDER_LINE).setAttribute(XMLLiterals.ORIGINAL_QTY, originalQty);
	    		}
	    	}
	    }
	}
	
}
