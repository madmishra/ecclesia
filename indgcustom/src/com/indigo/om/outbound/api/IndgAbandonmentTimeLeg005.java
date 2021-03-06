package com.indigo.om.outbound.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.joda.time.DateTimeZone;
import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.yantra.yfc.core.YFCObject;
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
	private static final String START_TIME = "00:00:00";
	private static final String CREATE = "Create"; 
	private static final String ABANDONMENT = "ABANDONMENT";
	private String finalDate = "";
	private static final String EMPTY_STRING = "";
	private String shipNode = "";
	private String abandonmentDays = "ABANDONMENT_DAYS";
	private String localeCode = "";
	private String finalDateUTC = "";
	
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
		YFCDocument shipNodeListApiOp = getShipNodeList();
		localeCode = shipNodeListApiOp.getDocumentElement().getChildElement(XMLLiterals.SHIPNODE).getAttribute(XMLLiterals.LOCALE_CODE);
		YFCDocument localeListApiOp = getLocaleList();
		String sTimeZone = localeListApiOp.getDocumentElement().getChildElement(XMLLiterals.LOCALE).getAttribute(XMLLiterals.TIME_ZONE);
		getUTCTimeForTimeZone(sTimeZone);
		YFCDocument docChangeShipmentOp = docChangeShipmentInp(inXml);
		return setAttrToReturnDoc(docChangeShipmentOp, inXml);
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
		String reqDeliveryDate = inXml.getDocumentElement().getAttribute(XMLLiterals.STATUS_DATE);
		if(!YFCObject.isVoid(reqDeliveryDate)) {
			String[] segments = reqDeliveryDate.split(TIME);
			String date = segments[0];
			Date d = sdf.parse(date);
			c.setTime(d);
			int days = Integer.parseInt(getProperty(abandonmentDays));
			c.add(Calendar.DATE, days);
			String output = sdf.format(c.getTime());
			finalDate = output.concat(TIME).concat(START_TIME);
		}
	}
	
	
	/**
	 * This method forms the UTC time zone based on localeCode.
	 * 
	 * @param sTimeZone
	 */
	
	private void getUTCTimeForTimeZone(String sTimeZone) {
		DateTime dt = new DateTime();
		DateTime timeStamp = dt.withZone(DateTimeZone.forID(sTimeZone));
		String date = timeStamp.toString();
		String substr = date.substring(date.length() - 6);
		finalDateUTC = finalDate.concat(substr);
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
		additionalDate.setAttribute(XMLLiterals.EXPECTED_DATE, finalDateUTC);
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
		shipmentLine.setAttribute(XMLLiterals.CUSTOMER_PONO, EMPTY_STRING);
		shipmentLine.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		shipmentLine.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		shipmentLine.setAttribute(XMLLiterals.QUANTITY, EMPTY_STRING);
		shipmentLine.setAttribute(XMLLiterals.SHIPMENT_KEY, EMPTY_STRING);
		YFCElement eleItem = shipmentLine.createChild(XMLLiterals.ITEM);
		eleItem.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
		YFCElement additionalDates = docShipment.getDocumentElement().createChild(XMLLiterals.ADDITIONAL_DATES);
		YFCElement additionalDate = additionalDates.createChild(XMLLiterals.ADDITIONAL_DATE);
		additionalDate.setAttribute(XMLLiterals.DATE_TYPE_ID, EMPTY_STRING);
		additionalDate.setAttribute(XMLLiterals.EXPECTED_DATE, EMPTY_STRING);
		return docShipment;
	}
	
	/**
	 * This method forms the Input XML for getShipNodeListAPI
	 * 
	 * @return
	 */
	
	public YFCDocument formInputXmlForGetStoreList() {
	    YFCDocument getStoreListDoc = YFCDocument.createDocument(XMLLiterals.SHIPNODE);
	    getStoreListDoc.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, shipNode);
	    return getStoreListDoc;
	 }
	 
	 /**
	  * This method forms template for the getShipNodeList
	  * 
	  * @return
	  */
		
	public YFCDocument formTemplateXmlForgetShipNodeList() {
	    YFCDocument getShipNodeListTemp = YFCDocument.createDocument(XMLLiterals.SHIPNODE_LIST);
	    YFCElement shipNodeEle = getShipNodeListTemp.getDocumentElement().createChild(XMLLiterals.SHIPNODE);
	    shipNodeEle.setAttribute(XMLLiterals.SHIP_NODE_CODE, EMPTY_STRING);
	    shipNodeEle.setAttribute(XMLLiterals.LOCALE_CODE, EMPTY_STRING);
	    
	    return getShipNodeListTemp;
	 }
	
	/**
	 * This method calls getShipNodeList API
	 * 
	 * @return
	 */
		
	public YFCDocument getShipNodeList(){
	    return  invokeYantraApi(XMLLiterals.GET_SHIP_NODE_LIST, formInputXmlForGetStoreList(),formTemplateXmlForgetShipNodeList());
	 }
	
	/**
	 * This method forms the input document for getLoacleList API.
	 * 
	 * @return
	 */
	
	public YFCDocument formInputXmlForGetLocaleList() {
	    YFCDocument getLocaleDoc = YFCDocument.createDocument(XMLLiterals.LOCALE);
	    getLocaleDoc.getDocumentElement().setAttribute(XMLLiterals.LOCALE_CODE, localeCode);
	    return getLocaleDoc;
	 }
	 
	 /**
	  * This method forms template for the getLocaleListTemp
	  * 
	  * @return
	  */
		
	public YFCDocument formTemplateXmlForgetLocaleList() {
	    YFCDocument getLocaleListTemp = YFCDocument.createDocument(XMLLiterals.LOCALE_LIST);
	    YFCElement localeEle = getLocaleListTemp.getDocumentElement().createChild(XMLLiterals.LOCALE);
	    localeEle.setAttribute(XMLLiterals.TIME_ZONE, EMPTY_STRING);
	    return getLocaleListTemp;
	 }
	
	/**
	 * This method calls getLocaleList API
	 * 
	 * @return
	 */
		
	public YFCDocument getLocaleList(){
	    return  invokeYantraApi(XMLLiterals.GET_LOCALE_LIST, formInputXmlForGetLocaleList(),formTemplateXmlForgetLocaleList());
	 }
	
	/**
	 * This method appends the necessary attributes to the return doc.
	 * 
	 * @param docChangeShipmentOp
	 * @param docGetOrderLineListOp
	 * @return
	 */
	
	private YFCDocument setAttrToReturnDoc(YFCDocument docChangeShipmentOp, YFCDocument inXml) {
		docChangeShipmentOp.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, inXml.getDocumentElement().getAttribute(XMLLiterals.MODIFYTS));
		docChangeShipmentOp.getDocumentElement().setAttribute(XMLLiterals.ORDER_TYPE, inXml.getDocumentElement().getAttribute(XMLLiterals.ORDER_TYPE));
		docChangeShipmentOp.getDocumentElement().setAttribute(XMLLiterals.CUSTOMER_PONO, docChangeShipmentOp.getDocumentElement().
				getChildElement(XMLLiterals.SHIPMENT_LINES).getChildElement(XMLLiterals.SHIPMENT_LINE).getAttribute(XMLLiterals.CUSTOMER_PONO));
		YFCElement shipmentLineEle = docChangeShipmentOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINES).getChildElement(XMLLiterals.SHIPMENT_LINE);
		shipmentLineEle.createChild(XMLLiterals.ITEM).setAttribute(XMLLiterals.ITEM_ID, docChangeShipmentOp.getDocumentElement().
				getChildElement(XMLLiterals.SHIPMENT_LINES).getChildElement(XMLLiterals.SHIPMENT_LINE).getChildElement(XMLLiterals.ITEM).getAttribute(XMLLiterals.ITEM_ID));
		return docChangeShipmentOp;
	}
}
