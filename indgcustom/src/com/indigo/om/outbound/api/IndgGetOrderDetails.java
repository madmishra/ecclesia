package com.indigo.om.outbound.api;

import java.util.HashMap;
import java.util.Map;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;

/** 
 * @author BSG165
 *
 */

public class IndgGetOrderDetails extends AbstractCustomApi {

	private static final String EMPTY_STRING = "";
	private static final String STAGING = "STAGING";

	/**
	 * 
	 * This is the invoke point of the Service
	 * 
	 * @throws 
	 */

	@Override
	public YFCDocument invoke(YFCDocument docInXml) {
		YFCElement eleInXml = docInXml.getDocumentElement();
		String sOrderNo = eleInXml.getAttribute(XMLLiterals.ORDER_NO);
		String sEnterpriseCode = eleInXml.getAttribute(XMLLiterals.ENTERPRISE_CODE);
		String sDocumentType = eleInXml.getAttribute(XMLLiterals.DOCUMENT_TYPE);
		return invokeGetCompleteOrderDetails(sOrderNo, sEnterpriseCode, sDocumentType);
	}

	/**
	 * This method forms input for getCompleteOrderDetails API
	 * 
	 * @param shipNode
	 * @param orderNo
	 * @param enterpriseCode
	 * @return
	 */

	private YFCDocument inputGetOrderDetails(String sDocumentType, String sEnterpriseCode, String sOrderNo) {
		YFCDocument docInputGetOrderDetails = YFCDocument.createDocument(XMLLiterals.ORDER);
		YFCElement eleOrder = docInputGetOrderDetails.getDocumentElement();
		eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, sDocumentType);
		eleOrder.setAttribute(XMLLiterals.ORDER_NO, sOrderNo);
		eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, sEnterpriseCode);
		return docInputGetOrderDetails;
	}

	/**
	 * This method forms input for getShipmentDetails API
	 * 
	 * @param shipmentKey
	 * @return
	 */

	private YFCDocument inputGetShipmentDetails(String sShipmentKey) {
		YFCDocument docInputGetOrderDetails = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		YFCElement eleShipment = docInputGetOrderDetails.getDocumentElement();
		eleShipment.setAttribute(XMLLiterals.SHIPMENT_KEY, sShipmentKey);
		return docInputGetOrderDetails;
	}

	/**
	 * 
	 * This method forms template for getShipmentDetails template
	 * @return
	 */

	private YFCDocument getShipmentDetailsTemplate() {
		YFCDocument docGetShipmentDetailsTemplate = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		YFCElement eleShipment = docGetShipmentDetailsTemplate.getDocumentElement();
		YFCElement eleAdditionalDates = eleShipment.createChild(XMLLiterals.ADDITIONAL_DATES);
		YFCElement eleAdditionalDate = eleAdditionalDates.createChild(XMLLiterals.ADDITIONAL_DATE);
		eleAdditionalDate.setAttribute(XMLLiterals.DATE_TYPE_ID, EMPTY_STRING);
		eleAdditionalDate.setAttribute(XMLLiterals.EXPECTED_DATE, EMPTY_STRING);
		eleAdditionalDate.setAttribute(XMLLiterals.REFERENCE_KEY, EMPTY_STRING);
		eleAdditionalDate.setAttribute(XMLLiterals.REFERENCE_TYPE, EMPTY_STRING);
		return docGetShipmentDetailsTemplate;
	}

	/**
	 * 
	 * This method forms template for getOrderDetails template
	 * @return
	 */

	private YFCDocument getOrderDetailsTemplate() {
		YFCDocument docGetOrderDetailsTemp = YFCDocument.createDocument(XMLLiterals.ORDER);
		YFCElement eleOrder = docGetOrderDetailsTemp.getDocumentElement();
		eleOrder.setAttribute(XMLLiterals.CUSTOMER_CONTACT_ID, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.CUSTOMER_EMAIL_ID, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.ENTERED_BY, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.ORDER_TYPE, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.SELLER_ORGANIZATION_CODE, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.MIN_ORDER_STATUS, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.MAX_ORDER_STATUS, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.MAX_ORDER_STATUS_DESC, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.MULTIPLE_STATUSES_EXIST, EMPTY_STRING);
		eleOrder.setAttribute(XMLLiterals.DIVISION, EMPTY_STRING);
		YFCElement eleOrderLines = eleOrder.createChild(XMLLiterals.ORDER_LINES);
		YFCElement eleOrderLine = eleOrderLines.createChild(XMLLiterals.ORDER_LINE);
		eleOrderLine.setAttribute(XMLLiterals.DELIVERY_METHOD, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.DEPARTMENT_METHOD, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.DEPARTMENT_CODE, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.FULFILLMENT_TYPE, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.ORDER_LINE_KEY, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.GIFT_FLAG, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.GIFT_WRAP, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.ORDERED_QTY, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.ORIGINAL_ORDERED_QTY, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.OVERALL_STATUS, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.REMAINING_QTY, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.REQUESTED_DELIVERY_DATE, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.REQ_SHIP_DATE, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.RESERVATION_ID, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.SHIP_NODE, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.CUSTOMER_PO_NO, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.CUSTOMER_LINE_PO_NO, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.PICKUP_AREA, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.PICKUP_BIN_NUMBER, EMPTY_STRING);
		eleOrderLine.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
		YFCElement eleItem = eleOrderLine.createChild(XMLLiterals.ITEM);
		eleItem.setAttribute(XMLLiterals.COST_CURRENCY, EMPTY_STRING);
		eleItem.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
		eleItem.setAttribute(XMLLiterals.ITEM_SHORT_DESC, EMPTY_STRING);
		eleItem.setAttribute(XMLLiterals.PRODUCT_LINE, EMPTY_STRING);
		eleItem.setAttribute(XMLLiterals.UNIT_COST, EMPTY_STRING);
		eleItem.setAttribute(XMLLiterals.UNIT_OF_MEASURE, EMPTY_STRING);
		YFCElement eleLinePriceInfo = eleOrderLine.createChild(XMLLiterals.LINE_PRICE_INFO);
		eleLinePriceInfo.setAttribute(XMLLiterals.LIST_PRICE, EMPTY_STRING);
		eleLinePriceInfo.setAttribute(XMLLiterals.RETAIL_PRICE, EMPTY_STRING);
		YFCElement eleReferences = eleOrderLine.createChild(XMLLiterals.REFERENCES);
		YFCElement eleReference = eleReferences.createChild(XMLLiterals.REFERENCE);
		eleReference.setAttribute(XMLLiterals.NAME, EMPTY_STRING);
		eleReference.setAttribute(XMLLiterals.VALUE, EMPTY_STRING);
		YFCElement eleAdditionalAddresses = eleOrderLine.createChild(XMLLiterals.ADDITIONAL_ADDRESSES);
		YFCElement eleAdditionalAddress = eleAdditionalAddresses.createChild(XMLLiterals.ADDITIONAL_ADDRESS);
		YFCElement elePersonInfo = eleAdditionalAddress.createChild(XMLLiterals.PERSON_INFO);
		elePersonInfo.setAttribute(XMLLiterals.ADDRESS_ID, EMPTY_STRING);
		elePersonInfo.setAttribute(XMLLiterals.FIRST_NAME, EMPTY_STRING);
		elePersonInfo.setAttribute(XMLLiterals.LAST_NAME, EMPTY_STRING);
		YFCElement elePersonInfoBillTo = eleOrder.createChild(XMLLiterals.PERSONINFO_BILL_TO);
		elePersonInfoBillTo.setAttribute(XMLLiterals.ADDRESS_LINE_1, EMPTY_STRING);
		elePersonInfoBillTo.setAttribute(XMLLiterals.ADDRESS_LINE_2, EMPTY_STRING);
		elePersonInfoBillTo.setAttribute(XMLLiterals.COUNTRY, EMPTY_STRING);
		elePersonInfoBillTo.setAttribute(XMLLiterals.DAY_PHONE, EMPTY_STRING);
		elePersonInfoBillTo.setAttribute(XMLLiterals.EMAIL_ID, EMPTY_STRING);
		elePersonInfoBillTo.setAttribute(XMLLiterals.FIRST_NAME, EMPTY_STRING);
		elePersonInfoBillTo.setAttribute(XMLLiterals.LAST_NAME, EMPTY_STRING);
		elePersonInfoBillTo.setAttribute(XMLLiterals.MIDDLE_NAME, EMPTY_STRING);
		elePersonInfoBillTo.setAttribute(XMLLiterals.STATE, EMPTY_STRING);
		elePersonInfoBillTo.setAttribute(XMLLiterals.ZIP_CODE, EMPTY_STRING);
		YFCElement elePriceInfo = eleOrder.createChild(XMLLiterals.PRICE_INFO);
		elePriceInfo.setAttribute(XMLLiterals.CURRENCY, EMPTY_STRING);
		elePriceInfo.setAttribute(XMLLiterals.ENTERPRICE_CURRENCY, EMPTY_STRING);
		YFCElement eleShipments = eleOrder.createChild(XMLLiterals.SHIPMENTS);
		YFCElement eleShipment = eleShipments.createChild(XMLLiterals.SHIPMENT);
		eleShipment.setAttribute(XMLLiterals.HOLD_LOCATION, EMPTY_STRING);
		eleShipment.setAttribute(XMLLiterals.SHIPMENT_KEY, EMPTY_STRING);
		YFCElement eleShipmentLines = eleShipment.createChild(XMLLiterals.SHIPMENTS_LINES);
		YFCElement eleShipmentLine = eleShipmentLines.createChild(XMLLiterals.SHIPMENTS_LINE);
		eleShipmentLine.setAttribute(XMLLiterals.ORDER_LINE_KEY, EMPTY_STRING);
		return docGetOrderDetailsTemp;
	}

	/**
	 * 
	 * This method forms output for getOrderDetails output
	 * @return
	 */

	private YFCDocument getOrderDetailsOutput(YFCDocument docGetOrderDetailsOutput) {
		YFCDocument docTempGetOrderDetailsOutput = docGetOrderDetailsOutput;
		YFCElement eleOrder = docTempGetOrderDetailsOutput.getDocumentElement();
		YFCElement eleOrderLines = eleOrder.getChildElement(XMLLiterals.ORDER_LINES);
		// map to store OrderLineKey(from shipment line level),HoldLocation
		Map<String, String> mapOrderLineKey = new HashMap<>();
		// map to store OrderLineKey(from shipment line level),shipmentKey
		Map<String, String> mapShipmentKey = new HashMap<>();
		YFCIterable<YFCElement> eleShipmentIterable = eleOrder.getChildElement(XMLLiterals.SHIPMENTS)
				.getChildren(XMLLiterals.SHIPMENT);
		// to get the OrderLineKey and HoldLocation values
		for (YFCElement eleShipmentTemp : eleShipmentIterable) {
			String sHoldLocation = eleShipmentTemp.getAttribute(XMLLiterals.HOLD_LOCATION);
			String shipmentKey = eleShipmentTemp.getAttribute(XMLLiterals.SHIPMENT_KEY);
			YFCIterable<YFCElement> eleShipmentLine = eleShipmentTemp.getChildElement(XMLLiterals.SHIPMENT_LINES)
					.getChildren(XMLLiterals.SHIPMENT_LINE);
			for (YFCElement eleShipmentLine1 : eleShipmentLine) {
				String shiplineOrderLineKey = eleShipmentLine1.getAttribute(XMLLiterals.ORDER_LINE_KEY);
				mapShipmentKey.put(shiplineOrderLineKey, shipmentKey);
				if ((sHoldLocation != null)) {
					mapOrderLineKey.put(shiplineOrderLineKey, sHoldLocation);
				}
				else
					mapOrderLineKey.put(shiplineOrderLineKey, EMPTY_STRING);
			}
		}
		// setting the PickupArea and PickupBinNumber values at Shipment level
		YFCIterable<YFCElement> eleOrderLine = eleOrderLines.getChildren(XMLLiterals.ORDER_LINE);
		for (YFCElement eleOrderLinetemp : eleOrderLine) {
			String sOrderLineKey = eleOrderLinetemp.getAttribute(XMLLiterals.ORDER_LINE_KEY);
			YFCElement eleShipmentTemp = eleOrderLinetemp.createChild(XMLLiterals.SHIPMENT);
			if (mapOrderLineKey.containsKey(sOrderLineKey))
			{
				eleShipmentTemp.setAttribute(XMLLiterals.PICKUP_BIN_NUMBER, mapOrderLineKey.get(sOrderLineKey));
				if (mapShipmentKey.containsKey(sOrderLineKey)) {
					YFCDocument docShipmentDetails = invokeGetShipmentDetails(mapShipmentKey.get(sOrderLineKey));
					YFCElement eleShipmentFromShipmentDetails = docShipmentDetails.getDocumentElement()
							.getChildElement(XMLLiterals.ADDITIONAL_DATES);
					if (YFCObject.isVoid(eleShipmentFromShipmentDetails)) {
						eleShipmentTemp.createChild(XMLLiterals.ADDITIONAL_DATES);
					}
					else {
						YFCNode nAdditionalDates = eleShipmentTemp.importNode(eleShipmentFromShipmentDetails);
						eleShipmentTemp.appendChild(nAdditionalDates);
					}
				}
			}
			else {
				eleShipmentTemp.setAttribute(XMLLiterals.PICKUP_BIN_NUMBER, EMPTY_STRING);
			}
			eleShipmentTemp.setAttribute(XMLLiterals.PICKUP_AREA, getProperty(STAGING));
		}
		// remove the shipments element and (i.e,docTempGetOrderDetailsOutput)
		YFCNode nodeOrder =  eleOrder.getChildElement(XMLLiterals.SHIPMENTS).getParentNode();
		nodeOrder.removeChild(eleOrder.getChildElement(XMLLiterals.SHIPMENTS));
		return docGetOrderDetailsOutput;
	}

	/**
	 * 
	 * This method invokes getCompleteOrderDetails API
	 * @param orderNo
	 * @param enterpriseCode
	 * @param documentType
	 * @return
	 */

	private YFCDocument invokeGetCompleteOrderDetails(String sOrderNo, String sEnterpriseCode, String sDocumentType) {
		YFCDocument docGetOrderDetailsOutput = invokeYantraApi(XMLLiterals.GET_COMPLETE_ORDER_DETAILS,
				inputGetOrderDetails(sDocumentType, sEnterpriseCode, sOrderNo), getOrderDetailsTemplate());
		return getOrderDetailsOutput(docGetOrderDetailsOutput);
	}

	private YFCDocument invokeGetShipmentDetails(String sShipmentKey) {
		return invokeYantraApi(XMLLiterals.GET_SHIPMENT_DETAILS, inputGetShipmentDetails(sShipmentKey),
				getShipmentDetailsTemplate());
	}
}