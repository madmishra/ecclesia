package com.indigo.om.outbound.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.bridge.sterling.utils.XPathUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * @author BSG168	
 * 
 * Custom API to consume LegacyOMS005 message and fetch the RequestedDeliveryTime
 * from the input and set it to a certain abandonment time when the order is
 * readyForCustomerPickup status.
 *
 */

public class IndgAbandonmentTimeLeg005 extends AbstractCustomApi {
	
	private static final String TIME = "T";
	private static final String START_TIME = "00:00:00-00:00";
	private static final String CREATE = "Create"; 
	private static final String ABANDONMENT = "ABANDONMENT";
	private String finalDate = "";
	private static final String EMPTY_STRING = "";
	private String shipNode = "";
	private static final int ABANDONMENT_DAYS = 15;
	
	/**
	 * This method is the invoke point of the service.
	 * 
	 */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		shipNode = inXml.getDocumentElement().getAttribute(XMLLiterals.SHIPNODE);
		try {
			setAbandonmentTimeAttr(inXml);
		} catch (ParseException e) {
			throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_INVALID_DATE, e);
		}
		YFCDocument docChangeShipmentOp = docChangeShipmentInp(inXml);
		System.out.println(docChangeShipmentOp + "saghgd");
		YFCDocument docGetOrderLineListOp = docGetOrderLineListInp(docChangeShipmentOp);
		System.out.println(docGetOrderLineListOp + "sxajhdd");
		return setAttrToReturnDoc(docChangeShipmentOp, docGetOrderLineListOp);
	}
	
	/**
	 * This method consumes the input document and fetched the 
	 * ReqDeliveryDate attribute and adds x number of days to the current date.
	 * 
	 * @param inXml
	 * @throws ParseException
	 */
	
	private void setAbandonmentTimeAttr(YFCDocument inXml) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		String reqDeliveryDate = inXml.getDocumentElement().getAttribute(XMLLiterals.REQUESTED_DELIVERY_DATE);
		System.out.println(reqDeliveryDate + "dsjkhd");
		if(!XmlUtils.isVoid(reqDeliveryDate)) {
			String[] segments = reqDeliveryDate.split(TIME);
			String date = segments[0];
			Date d = sdf.parse(date);
			c.setTime(d);
			c.add(Calendar.DATE, ABANDONMENT_DAYS);
			String output = sdf.format(c.getTime());
			finalDate = output.concat(TIME).concat(START_TIME);
			System.out.println(finalDate + "sjahdg");
		}
	}
	
	/**
	 * This method forms the input for ChangeShipment API and
	 * invokes the API. 
	 * 
	 * @param inXml
	 * @return
	 */
	
	private YFCDocument docChangeShipmentInp(YFCDocument inXml) {
		String shipmentKey = inXml.getDocumentElement().getAttribute(XMLLiterals.SHIPMENT_KEY);
		YFCDocument docChangeShipment = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		docChangeShipment.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, shipNode);
		docChangeShipment.getDocumentElement().setAttribute(XMLLiterals.SHIPMENT_KEY, shipmentKey);
		YFCElement additionalDates = docChangeShipment.getDocumentElement().createChild(XMLLiterals.ADDITIONAL_DATES);
		YFCElement additionalDate = additionalDates.createChild(XMLLiterals.ADDITIONAL_DATE);
		additionalDate.setAttribute(XMLLiterals.ACTION, CREATE);
		additionalDate.setAttribute(XMLLiterals.DATE_TYPE_ID, ABANDONMENT);
		additionalDate.setAttribute(XMLLiterals.EXPECTED_DATE, finalDate);
		System.out.println(docChangeShipment + "sjakhdja");
		return invokeYantraApi(XMLLiterals.CHANGE_SHIPMENT, docChangeShipment, docChangeShipmentOpTemplate());
	}
	
	/**
	 * This method forms the template for ChangeShipment API. 
	 * 
	 * @return
	 */
	
	private YFCDocument docChangeShipmentOpTemplate() {
		YFCDocument docShipment = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		YFCElement shipmentLines = docShipment.getDocumentElement().createChild(XMLLiterals.SHIPMENT_LINES);
		YFCElement shipmentLine = shipmentLines.createChild(XMLLiterals.SHIPMENT_LINE);
		shipmentLine.setAttribute(XMLLiterals.CUSTOMER_PO_NO, EMPTY_STRING);
		shipmentLine.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		shipmentLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		shipmentLine.setAttribute(XMLLiterals.QUANTITY, EMPTY_STRING);
		shipmentLine.setAttribute(XMLLiterals.SHIPMENT_KEY, EMPTY_STRING);
		YFCElement additionalDates = docShipment.getDocumentElement().createChild(XMLLiterals.ADDITIONAL_DATES);
		YFCElement additionalDate = additionalDates.createChild(XMLLiterals.ADDITIONAL_DATE);
		additionalDate.setAttribute(XMLLiterals.DATE_TYPE_ID, EMPTY_STRING);
		additionalDate.setAttribute(XMLLiterals.EXPECTED_DATE, EMPTY_STRING);
		System.out.println(docShipment + "sajhsss");
		return docShipment;
	}
	
	/**
	 * This method forms the input for getOrderLineList API and
	 * invokes the API.
	 * 
	 * @param docChangeShipmentOp
	 * @return
	 */
	
	private YFCDocument docGetOrderLineListInp(YFCDocument docChangeShipmentOp) {
		String orderNo = docChangeShipmentOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINES).
				getChildElement(XMLLiterals.SHIPMENT_LINE).getAttribute(XMLLiterals.ORDER_NO);
		YFCDocument docGetOrderLineListInp = YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
		docGetOrderLineListInp.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, shipNode);
		YFCElement order = docGetOrderLineListInp.getDocumentElement().createChild(XMLLiterals.ORDER);
		order.setAttribute(XMLLiterals.ORDER_NO, orderNo);
		System.out.println(docGetOrderLineListInp + "sajksa");
		return invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST, docGetOrderLineListInp, docGetOrderLineListTemplate());
	}
	
	/**
	 * This method forms the template for getOrderLineList API. 
	 * 
	 * @return
	 */
	
	private YFCDocument docGetOrderLineListTemplate() {
		YFCDocument docApiOutput = YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
		YFCElement orderLine = docApiOutput.getDocumentElement().createChild(XMLLiterals.ORDER_LINE);
		orderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		orderLine.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		orderLine.setAttribute(XMLLiterals.CUSTOMER_PO_NO, EMPTY_STRING);
		YFCElement item = orderLine.createChild(XMLLiterals.ITEM);
		item.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
		YFCElement order = orderLine.createChild(XMLLiterals.ORDER);
		order.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		order.setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
		order.setAttribute(XMLLiterals.ORDER_TYPE, EMPTY_STRING);
		System.out.println(docApiOutput + "ajshaus");
		return docApiOutput;
	}
	
	/**
	 * This method appends the necessary attributes to the return doc.
	 * 
	 * @param docChangeShipmentOp
	 * @param docGetOrderLineListOp
	 * @return
	 */
	
	private YFCDocument setAttrToReturnDoc(YFCDocument docChangeShipmentOp, YFCDocument docGetOrderLineListOp) {
		String modifyTs = docGetOrderLineListOp.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
				getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.MODIFYTS);
		String orderType = docGetOrderLineListOp.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
				getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.ORDER_TYPE);
		String customerPoNo = docGetOrderLineListOp.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
				getAttribute(XMLLiterals.CUSTOMER_PO_NO);
		YFCIterable<YFCElement> yfsItrator = docChangeShipmentOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINES).
				  getChildren(XMLLiterals.SHIPMENT_LINE);
		 for(YFCElement shipmentLineEle : yfsItrator) {
			 System.out.println(shipmentLineEle.toString() + "xshjdh");
			 String primeLineNo = shipmentLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO);
			 System.out.println(primeLineNo + "dsabhjh");
			 YFCElement odrLineEle = XPathUtil.getXPathElement(docGetOrderLineListOp, "/OrderLineList/OrderLine/[@PrimeLineNo=\""+primeLineNo+"\"]");
			 System.out.println(odrLineEle.toString() + "ajksghja");
			 String itemId = odrLineEle.getChildElement(XMLLiterals.ITEM).getAttribute(XMLLiterals.ITEM_ID);
			 shipmentLineEle.createChild(XMLLiterals.ITEM).setAttribute(XMLLiterals.ITEM_ID, itemId);
			 System.out.println(shipmentLineEle.toString() + "ashdgd");
		 }
		docChangeShipmentOp.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, modifyTs);
		docChangeShipmentOp.getDocumentElement().setAttribute(XMLLiterals.ORDER_TYPE, orderType);
		docChangeShipmentOp.getDocumentElement().setAttribute(XMLLiterals.CUSTOMER_PO_NO, customerPoNo);
		System.out.println(docChangeShipmentOp + "saghygdt");
		return docChangeShipmentOp;
	}
}
