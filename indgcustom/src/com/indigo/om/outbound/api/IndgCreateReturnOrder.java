package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * @author BSG170
 * 
 * Custom code to create return order
 *
 */

public class IndgCreateReturnOrder extends AbstractCustomApi {


	private static final String EMPTY_STRING = "";
	private static final String SUBLINE_VALUE = "1";
	private static final String SALES_ORDER_VALUE = "0001";
	private static final String CAD = "CAD"; 
	YFCDocument doccreateOrderInput = YFCDocument.createDocument(XMLLiterals.ORDER);

	 /**
	  * This method is the invoke point of the service.
	  * 
	  */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml)
	{
		YFCDocument docCreateOrderOutput = null;
		YFCDocument outDocgetOrderLineList = getOrderLineList(inXml);
		String sOrderHeaderKey = null;
		YFCElement eleOrder = outDocgetOrderLineList.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).getChildElement(XMLLiterals.ORDER);
		if(!YFCObject.isVoid(eleOrder)) {
		YFCIterable<YFCElement> eleOrderLine = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY)
				.getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES).getChildren(XMLLiterals.ORDER_LINE);
		sOrderHeaderKey = eleOrder.getAttribute(XMLLiterals.ORDER_HEADER_KEY);
		createOrderHeaderInput(inXml);
		for(YFCElement orderLine : eleOrderLine) {
			formOrderLineElement(doccreateOrderInput, orderLine, sOrderHeaderKey);
		}
		docCreateOrderOutput = invokeYantraApi(XMLLiterals.CREATE_ORDER, doccreateOrderInput, createOrderTemplate());
		}
		return docCreateOrderOutput;
	}

	/**
	 * This method invokes getOrderList API
	 * @param inXml
	 * @return
	 */
	
	private YFCDocument getOrderLineList(YFCDocument inXml) 
	{
		return invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST, getOrderLineListInput(inXml), getOrderLineListTemplate());
		
	}
	private void createOrderHeaderInput(YFCDocument inXml)
	{
		YFCElement eleInXml = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
		YFCElement  elePersonInfo = eleInXml.getChildElement(XMLLiterals.PERSON_INFO_BILL_TO);
		YFCElement eleOrder= doccreateOrderInput.getDocumentElement();
		YFCElement eleOrderLines = eleOrder.createChild(XMLLiterals.ORDER_LINES);
		eleOrder.setAttribute(XMLLiterals.ORDER_NO, eleInXml.getAttribute(XMLLiterals.RETURN_ORDER_NO));
		eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, eleInXml.getAttribute(XMLLiterals.DOCUMENT_TYPE));
		eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, eleInXml.getAttribute(XMLLiterals.ENTERPRISE_CODE));
		eleOrder.setAttribute(XMLLiterals.ORDER_TYPE, eleInXml.getAttribute(XMLLiterals.ORDER_TYPE));
		eleOrder.importNode(elePersonInfo);
		YFCElement elePriceInfo = eleOrder.createChild(XMLLiterals.PRICE_INFO);
		elePriceInfo.setAttribute(XMLLiterals.CURRENCY, CAD);
		elePriceInfo.setAttribute(XMLLiterals.ENTERPRISE_CURRENCY, CAD);
		YFCElement eleRerferences = eleOrder.createChild(XMLLiterals.REFERENCES);
		YFCElement eleRerference1 = eleRerferences.createChild(XMLLiterals.REFERENCE);
		eleRerference1.setAttribute(XMLLiterals.NAME, XMLLiterals.HUVALUE);
		eleRerference1.setAttribute(XMLLiterals.VALUE, eleInXml.getAttribute(XMLLiterals.HUVALUE));
	}
	

	
	/**
	 * This method forms the OrderLines for createOrder input
	 * @param eleOrderLines
	 * @param orderLine
	 * @param eleInXml
	 * @param sOrderHeaderKey
	 */
	
	private  void formOrderLineElement(YFCDocument doccreateOrderInput, YFCElement orderLine , String sOrderHeaderKey)
	{
		YFCElement eleOLine = doccreateOrderInput.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES).createChild(XMLLiterals.ORDER_LINE);
		String sShipNode = orderLine.getAttribute(XMLLiterals.RETURN_NODE_ID);
		eleOLine.setAttribute(XMLLiterals.ORDERED_QTY, orderLine.getAttribute(XMLLiterals.QTY));
		eleOLine.setAttribute(XMLLiterals.PRIME_LINE_NO, orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO));
		eleOLine.setAttribute(XMLLiterals.SUB_LINE_NO, SUBLINE_VALUE);
		eleOLine.setAttribute(XMLLiterals.SHIPNODE, sShipNode);
		eleOLine.setAttribute(XMLLiterals.RETURN_REASON, orderLine.getAttribute(XMLLiterals.RETURN_REASON_CODE));	
		YFCElement eleDerivedFrom = eleOLine.createChild(XMLLiterals.DERIVED_FROM);
		eleDerivedFrom.setAttribute(XMLLiterals.ORDER_HEADER_KEY, sOrderHeaderKey);
		eleDerivedFrom.setAttribute(XMLLiterals.PRIME_LINE_NO, orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO));
		eleDerivedFrom.setAttribute(XMLLiterals.SUB_LINE_NO, SUBLINE_VALUE);
		eleDerivedFrom.setAttribute(XMLLiterals.DOCUMENT_TYPE,  SALES_ORDER_VALUE);
		YFCElement eleItem = eleOLine.createChild(XMLLiterals.ITEM);
		eleItem.setAttribute(XMLLiterals.ITEM_ID, orderLine.getChildElement(XMLLiterals.ITEM).getAttribute(XMLLiterals.ITEM_ID));
		YFCElement eleRerferences = eleOLine.createChild(XMLLiterals.REFERENCES);
		YFCElement eleRerference2 = eleRerferences.createChild(XMLLiterals.REFERENCE);
		eleRerference2.setAttribute(XMLLiterals.NAME, XMLLiterals.DISPOSITION);
		eleRerference2.setAttribute(XMLLiterals.VALUE, orderLine.getAttribute(XMLLiterals.DISPOSITION));
		
		}	
	
	/** This method form template for createOrder API
	 * 
	 * @return
	 */

	private YFCDocument createOrderTemplate() 
	{
		YFCDocument docCreateOrder = YFCDocument.createDocument(XMLLiterals.ORDER);
		YFCElement eleCreateOrder = docCreateOrder.getDocumentElement();
		eleCreateOrder.setAttribute(XMLLiterals.CUSTOMER_PO_NO, EMPTY_STRING);
		eleCreateOrder.setAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO, EMPTY_STRING);
		eleCreateOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		eleCreateOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
		eleCreateOrder.setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
		eleCreateOrder.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		eleCreateOrder.setAttribute(XMLLiterals.ORDER_TYPE, EMPTY_STRING);
		YFCElement  eleOrderLine = eleCreateOrder.createChild(XMLLiterals.ORDER_LINES).createChild(XMLLiterals.ORDER_LINE);
		eleOrderLine.setAttribute(XMLLiterals.ORDERED_QTY, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.RETURN_REASON, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.SUB_LINE_NO, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		YFCElement eleReference = eleOrderLine.createChild(XMLLiterals.REFERENCES).createChild(XMLLiterals.REFERENCE);
		eleReference.setAttribute(XMLLiterals.NAME, EMPTY_STRING);
		eleReference.setAttribute(XMLLiterals.VALUE, EMPTY_STRING);
		YFCElement eleDerivedFromOrder = eleOrderLine.createChild(XMLLiterals.DERIVED_FROM_ORDER);
		eleDerivedFromOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		eleDerivedFromOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
		eleDerivedFromOrder.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		YFCElement eleDerivedFromOrderLine = eleOrderLine.createChild(XMLLiterals.DERIVED_FROM_ORDER_LINE);
		eleDerivedFromOrderLine.setAttribute(XMLLiterals.CUSTOMER_PO_NO, EMPTY_STRING);
		eleDerivedFromOrderLine.setAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO, EMPTY_STRING);
		eleDerivedFromOrderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		eleDerivedFromOrderLine.setAttribute(XMLLiterals.SUB_LINE_NO, EMPTY_STRING);
		YFCElement eleItem = eleDerivedFromOrderLine.createChild(XMLLiterals.ITEM);
		eleItem.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
		return docCreateOrder;
	}
	
	/**
	 * This method forms template getOrderLineList API
	 * @return
	 */

	private YFCDocument getOrderLineListTemplate() 
	{
		YFCDocument getOrderLineList  =YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
		YFCElement eleOrderLineList = getOrderLineList.getDocumentElement();
		YFCElement eleOrderLine = eleOrderLineList.createChild(XMLLiterals.ORDER_LINE);
		eleOrderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		YFCElement eleOrder = eleOrderLine.createChild(XMLLiterals.ORDER);
		eleOrder.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.ORDER_HEADER_KEY, EMPTY_STRING);
		return getOrderLineList;
	}
	
	/**
	 * This method forms getOrderLineList input
	 * @param inXml
	 * @return
	 */

	private YFCDocument getOrderLineListInput(YFCDocument inXml) 
	{
		YFCDocument docgetOrderListInput = YFCDocument.createDocument(XMLLiterals.ORDER_LINE); 
		YFCElement eleOrderLine = docgetOrderListInput.getDocumentElement();
		YFCElement eleInXml = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY)
				.getChildElement(XMLLiterals.ORDER);
		YFCElement eleMsgBody = eleInXml.getChildElement(XMLLiterals.ORDER_LINES).getChildElement(XMLLiterals.ORDER_LINE);
		eleOrderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, eleMsgBody.getAttribute(XMLLiterals.PRIME_LINE_NO));
		eleOrderLine.setAttribute(XMLLiterals.CUSTOMER_PO_NO, eleMsgBody.getAttribute(XMLLiterals.LEGACY_OMS_ORDER_NO));
		YFCElement eleOrder = eleOrderLine.createChild(XMLLiterals.ORDER);
		eleOrder.setAttribute(XMLLiterals.ORDER_NO, eleInXml.getAttribute(XMLLiterals.PARENT_LEGACY_OMS_ORDER_NO));
		eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, SALES_ORDER_VALUE);
		eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, eleInXml.getAttribute(XMLLiterals.ENTERPRISE_CODE));
		return docgetOrderListInput;	
	}



	}
