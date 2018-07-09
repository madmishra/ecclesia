package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;

public class IndgSAP011 extends AbstractCustomApi {
	
	private static final String SUBLINE_VALUE = "1";
	private static final String EMPTY_STRING = "";
	YFCDocument inputXmlForOrderLineList = null;

	@Override
	public YFCDocument invoke(YFCDocument inXml) {
    YFCDocument docOrderLineList = invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST, orderLineListInput(inXml),orderLineListTemplate());
    return  commonPrimeLineNo(docOrderLineList, inXml);
	}
	
	private YFCDocument orderLineListTemplate(){
		YFCDocument docOrderLineList = YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
		YFCElement eleOrderLineList = docOrderLineList.getDocumentElement();
		YFCElement eleOrderLine = eleOrderLineList.createChild(XMLLiterals.ORDER_LINE);
		eleOrderLine.setAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO, EMPTY_STRING );
		eleOrderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.SUB_LINE_NO, SUBLINE_VALUE);
		YFCElement eleItem = eleOrderLineList.createChild(XMLLiterals.ITEM);
		eleItem.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
		YFCElement eleLinePrice = eleOrderLineList.createChild(XMLLiterals.LINE_PRICE_INFO);
		eleLinePrice.setAttribute(XMLLiterals.RETAIL_PRICE, EMPTY_STRING);
		YFCElement eleOrder = eleOrderLineList.createChild(XMLLiterals.ORDER);
		eleOrder.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.ORDER_TYPE, EMPTY_STRING);
		
		return docOrderLineList;
		
	}
	
	private YFCDocument orderLineListInput(YFCDocument inXml) {
		YFCDocument docOrderLineListInput = YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
		YFCElement eleOrder = docOrderLineListInput.getDocumentElement().createChild(XMLLiterals.ORDER);
		YFCElement eleMsgBody = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
		eleOrder.setAttribute(XMLLiterals.ORDER_NO, eleMsgBody.getAttribute(XMLLiterals.PARENT_LEGACY_OMS_ORDER_NO));
		eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, eleMsgBody.getAttribute(XMLLiterals.ENTERPRISE_CODE));
		eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, eleMsgBody.getAttribute(XMLLiterals.DOCUMENT_TYPE));
		
		return docOrderLineListInput;
	}
	
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
