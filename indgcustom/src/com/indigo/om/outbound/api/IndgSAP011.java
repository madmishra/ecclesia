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
 * 
 * @author Nikita Shukla	
 * 
 * Custom API to send SAP011 message based on LEGACYOMS011
 *
 */

public class IndgSAP011 extends AbstractCustomApi {
	
	private static final String SUBLINE_VALUE = "1";
	private static final String EMPTY_STRING = "";
	YFCDocument inputXmlForOrderLineList = null;

	@Override
	public YFCDocument invoke(YFCDocument inXml) {
    YFCDocument docOrderLineList = invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST, orderLineListInput(inXml),orderLineListTemplate());
    return  commonPrimeLineNo(docOrderLineList, inXml);
	}
	
	/**
	 * This is the template
	 * 
	 * @return
	 */
	
	private YFCDocument orderLineListTemplate(){
		YFCDocument docOrderLineList = YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
		YFCElement eleOrderLineList = docOrderLineList.getDocumentElement();
		YFCElement eleOrderLine = eleOrderLineList.createChild(XMLLiterals.ORDER_LINE);
		eleOrderLine.setAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO, EMPTY_STRING );
		eleOrderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.SUB_LINE_NO, SUBLINE_VALUE);
		YFCElement eleItem = eleOrderLine.createChild(XMLLiterals.ITEM);
		eleItem.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
		YFCElement eleLinePrice = eleOrderLine.createChild(XMLLiterals.LINE_PRICE_INFO);
		eleLinePrice.setAttribute(XMLLiterals.RETAIL_PRICE, EMPTY_STRING);
		eleLinePrice.setAttribute(XMLLiterals.LIST_PRICE, EMPTY_STRING);
		YFCElement eleOrder = eleOrderLine.createChild(XMLLiterals.ORDER);
		eleOrder.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.ORDER_TYPE, EMPTY_STRING);
		return docOrderLineList;
		
	}
	/**
	 * 
	 * This is the Input Document
	 * @return
	 */
	
	private YFCDocument orderLineListInput(YFCDocument inXml) {
		YFCDocument docOrderLineListInput = YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
		YFCElement eleOrder = docOrderLineListInput.getDocumentElement().createChild(XMLLiterals.ORDER);
		YFCElement eleMsgBody = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
		eleOrder.setAttribute(XMLLiterals.ORDER_NO, eleMsgBody.getAttribute(XMLLiterals.PARENT_LEGACY_OMS_ORDER_NO));
		eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, eleMsgBody.getAttribute(XMLLiterals.ENTERPRISE_CODE));
		eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, eleMsgBody.getAttribute(XMLLiterals.DOCUMENT_TYPE));
		return docOrderLineListInput;
	}
	
	/**
	 * 
	 * Here, the Lines being sent in LEGACYOMS011 input is compared 
	 * with getOrderLineList Output, and if not present in LEGACYOMS011 input
	 * we remove the entire element and print the rest
	 * 
	 * @return
	 */
	
	private YFCDocument commonPrimeLineNo(YFCDocument docOrderLineList, YFCDocument inXml) {
		YFCIterable< YFCElement>  yfsIterator =  docOrderLineList.getDocumentElement().getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderLine : yfsIterator)
		{
			String sPrimeLineNo = orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO) ;
			YFCElement orderLineEle = XPathUtil.getXPathElement(inXml, "/OrderMessage/MessageBody/Order/OrderLines/OrderLine[@PrimeLineNo = \""+
					sPrimeLineNo+"\"]");
			if(XmlUtils.isVoid(orderLineEle)) {
				YFCNode parent = orderLine.getParentNode();
				parent.removeChild(orderLine);
			}
		}
		
		return docOrderLineList;
	}

}
