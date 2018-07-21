package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * This code is used to Extend the Abandonment Time 
 * when LegacyOMS063 message reaches Sterling and
 * also frames the Input for LegaccyOMS064 message
 * 
 * @author Nikita Shukla
 *
 */


public class IndgExtendedAbandonment extends AbstractCustomApi {
	
	private static final String EMPTY_STRING = "";
	private static final String YES = "Y"; 
	private static final String NO = "N"; 
	private static final String STATUS1 = "STATUS1";
	private static final String STATUS2 = "STATUS2";
	private static final String MODIFY = "MODIFY";
	private static final String ABANDONMENT = "ABANDONMENT";
	private String setIsProcessed = "";
	private static final String SHIPPED_STATUS = "1400";
	YFCDocument docGetShipmentDetails = null;

/**
 * This is the main Invoke Method
 * 
 * Here, we also check the Status, if the Status is
 * Shipped, we stamp the status in the Input Doc and End the process.
 * Else, if it any other status, it continues to other methods.
 * 	
 */

	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCDocument docGetShipmentListOp = getShipmentListAPI(inXml);
		String status = docGetShipmentListOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.STATUS);
		if(!status.equals(SHIPPED_STATUS)) {
			checkStatusOfShipmentDetails(docGetShipmentListOp, inXml);
			docGetShipmentDetails = getShipmentDetailsAPI(docGetShipmentListOp);
			String customerPONo = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.LEGACY_OMS_ORDER_NO);
			docGetShipmentDetails.getDocumentElement().setAttribute(XMLLiterals.CUSTOMER_PO_NO, customerPONo);
			docGetShipmentDetails.getDocumentElement().setAttribute(XMLLiterals.IS_PROCESSED, setIsProcessed);
		return docGetShipmentDetails;
		}
		inXml.getDocumentElement().setAttribute(XMLLiterals.STATUS, SHIPPED_STATUS);
		return inXml;
		
	}
	
	/**
	 * This is getShipmentList Input method,
	 * Here we are trying to get the Status of the Order, 
	 * if the status is Ready For Customer Pickup, only then 
	 * we call changeShipment.
	 * 
	 * 
	 * @param inXml
	 * @return
	 */
	
	public YFCDocument docgetShipmentListInp(YFCDocument inXml) {
		YFCElement orderEle = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
		YFCDocument eleShipment = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		eleShipment.getDocumentElement().setAttribute(XMLLiterals.CUSTOMER_PO_NO, orderEle.getAttribute(XMLLiterals.LEGACY_OMS_ORDER_NO));
		eleShipment.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, orderEle.getAttribute(XMLLiterals.ENTERPRISE_CODE));
		eleShipment.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, orderEle.getAttribute(XMLLiterals.DOCUMENT_TYPE));
		YFCElement eleShipmentLines = eleShipment.getDocumentElement().createChild(XMLLiterals.SHIPMENT_LINES);
		YFCElement eleShipmentLine = eleShipmentLines.createChild(XMLLiterals.SHIPMENT_LINE);
		eleShipmentLine.setAttribute(XMLLiterals.ORDER_NO, orderEle.getAttribute(XMLLiterals.PARENT_LEGACY_OMS_ORDER_NO));
		return 	eleShipment;
	}
	
	/**
	 * This is the getShipmentList Template
	 * 
	 * @return
	 */
	public YFCDocument docgetShipmentListTemp() {
		YFCDocument docgetShipmentListTemp = YFCDocument.createDocument(XMLLiterals.SHIPMENTS);
		YFCElement eleShipment = docgetShipmentListTemp.getDocumentElement().createChild(XMLLiterals.SHIPMENT);
		eleShipment.setAttribute(XMLLiterals.SHIPMENT_KEY, EMPTY_STRING);
		eleShipment.setAttribute(XMLLiterals.SHIPMENT_NO, EMPTY_STRING);
		eleShipment.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		eleShipment.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
		eleShipment.setAttribute(XMLLiterals.ORDER_TYPE, EMPTY_STRING);
		eleShipment.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
		YFCElement eleShipmentLines = eleShipment.createChild(XMLLiterals.SHIPMENT_LINES);
		YFCElement eleShipmentLine = eleShipmentLines.createChild(XMLLiterals.SHIPMENT_LINE);
		eleShipmentLine.setAttribute(XMLLiterals.CUSTOMER_PO_NO, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		return docgetShipmentListTemp;
	}
	
	/**
	 * Method to invoke the API - getShipmentList
	 * 
	 * @param inXml
	 * @return
	 */
	
	private YFCDocument getShipmentListAPI(YFCDocument inXml) {
		return invokeYantraApi(XMLLiterals.GET_SHIPMENT_LIST, docgetShipmentListInp(inXml), docgetShipmentListTemp());
	}
	
	/**
	 * 
	 * This method checks for the status, if the status
	 * is beyond Ready For Customer Pickup, changeShipment is not invoked.
	 * Else, changeShipment API is invoked and the NewAbandonmentDate is stamped.
	 * 
	 * @param docGetShipmentListOp
	 * @param inXml
	 */
	
	private void checkStatusOfShipmentDetails(YFCDocument docGetShipmentListOp, YFCDocument inXml) {
		
		String status = docGetShipmentListOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.STATUS);
		if(status.equals(getProperty(STATUS1)) || status.equals(getProperty(STATUS2))) {
			invokeYantraApi(XMLLiterals.CHANGE_SHIPMENT, docChangeShipmentInp(inXml, docGetShipmentListOp), docChangeShipmentOpTemplate());
			setIsProcessed = YES;
		}
		else {
			setIsProcessed = NO;
		}
	}
	
	/**
	 * changeShipment API Input
	 * 
	 * @param inXml
	 * @param docGetShipmentListOp
	 * @return
	 */
	
	private YFCDocument docChangeShipmentInp(YFCDocument inXml, YFCDocument docGetShipmentListOp) {
		YFCDocument docChangeShipmentIn = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		docChangeShipmentIn.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE,
				docGetShipmentListOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.ENTERPRISE_CODE));
		docChangeShipmentIn.getDocumentElement().setAttribute(XMLLiterals.SELLER_ORGANIZATION_CODE,
				docGetShipmentListOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.ENTERPRISE_CODE));
		docChangeShipmentIn.getDocumentElement().setAttribute(XMLLiterals.SHIPMENT_NO,
				docGetShipmentListOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.SHIPMENT_NO));
		docChangeShipmentIn.getDocumentElement().setAttribute(XMLLiterals.CUSTOMER_PO_NO,
				inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.LEGACY_OMS_ORDER_NO));
		docChangeShipmentIn.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE,
				docGetShipmentListOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.SHIPNODE));
		YFCElement eleAdditionalDates = docChangeShipmentIn.getDocumentElement().createChild(XMLLiterals.ADDITIONAL_DATES);
		YFCElement eleAdditionalDate = eleAdditionalDates.createChild(XMLLiterals.ADDITIONAL_DATE);
		eleAdditionalDate.setAttribute(XMLLiterals.ACTION, MODIFY);
		eleAdditionalDate.setAttribute(XMLLiterals.DATE_TYPE_ID, ABANDONMENT);
		eleAdditionalDate.setAttribute(XMLLiterals.EXPECTED_DATE,
				inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.NEW_ABANDONMENT_TIME));
		return docChangeShipmentIn;
	}
	
	/**
	 * changeShipment API Template
	 * 
	 * @return
	 */
	
	private YFCDocument docChangeShipmentOpTemplate() {
		YFCDocument docShipment = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.ORDER_TYPE, EMPTY_STRING);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
		YFCElement shipmentLines = docShipment.getDocumentElement().createChild(XMLLiterals.SHIPMENT_LINES);
		YFCElement shipmentLine = shipmentLines.createChild(XMLLiterals.SHIPMENT_LINE);
		shipmentLine.setAttribute(XMLLiterals.CUSTOMER_PO_NO, EMPTY_STRING);
		shipmentLine.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		shipmentLine.setAttribute(XMLLiterals.QUANTITY, EMPTY_STRING);
		shipmentLine.setAttribute(XMLLiterals.SHIPMENT_KEY, EMPTY_STRING);
		YFCElement additionalDates = docShipment.getDocumentElement().createChild(XMLLiterals.ADDITIONAL_DATES);
		YFCElement additionalDate = additionalDates.createChild(XMLLiterals.ADDITIONAL_DATE);
		additionalDate.setAttribute(XMLLiterals.DATE_TYPE_ID, EMPTY_STRING);
		additionalDate.setAttribute(XMLLiterals.EXPECTED_DATE, EMPTY_STRING);
		return docShipment;
	}
	
	/**
	 * Irrespective of changeShipment API being invoked or not,
	 * We call getShipmentDetails to stamp the Abandonment Time and 
	 * frame the input for LegacyOMS064 message
	 * 
	 * @param docGetShipmentListOp
	 * @return
	 */
	
	private YFCDocument docgetShipmentDetailsInp(YFCDocument docGetShipmentListOp){
		YFCDocument docgetShipmentDetailsInp = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		docgetShipmentDetailsInp.getDocumentElement().setAttribute(XMLLiterals.SELLER_ORGANIZATION_CODE, 
				docGetShipmentListOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.ENTERPRISE_CODE));
		docgetShipmentDetailsInp.getDocumentElement().setAttribute(XMLLiterals.SHIPMENT_KEY, 
				docGetShipmentListOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.SHIPMENT_KEY));
		docgetShipmentDetailsInp.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, 
				docGetShipmentListOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).getAttribute(XMLLiterals.SHIPNODE));
		return docgetShipmentDetailsInp;
	}
	
	/**
	 * getShipmentDetails Template
	 * 
	 * @return
	 */
	
	private YFCDocument docgetShipmentDetailsTemplate() {
		YFCDocument docShipment = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, EMPTY_STRING);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.MODIFYTS, EMPTY_STRING);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.ORDER_TYPE, EMPTY_STRING);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.SELLER_ORGANIZATION_CODE, EMPTY_STRING);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		docShipment.getDocumentElement().setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
		YFCElement eleShipmentLines = docShipment.getDocumentElement().createChild(XMLLiterals.SHIPMENT_LINES);
		YFCElement eleShipmentLine = eleShipmentLines.createChild(XMLLiterals.SHIPMENT_LINE);
		eleShipmentLine.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
		eleShipmentLine.setAttribute(XMLLiterals.QUANTITY, EMPTY_STRING);
		YFCElement eleAdditionalDates = docShipment.getDocumentElement().createChild(XMLLiterals.ADDITIONAL_DATES);
		YFCElement eleAdditionalDate = eleAdditionalDates.createChild(XMLLiterals.ADDITIONAL_DATE);
		eleAdditionalDate.setAttribute(XMLLiterals.EXPECTED_DATE, EMPTY_STRING);
		return docShipment;
	}
	
	/**
	 * Method to invoke the API - getShipmentDetails 
	 * 
	 * @param docGetShipmentListOp
	 * @return
	 */
	
	private YFCDocument getShipmentDetailsAPI(YFCDocument docGetShipmentListOp) {
		return invokeYantraApi(XMLLiterals.GET_SHIPMENT_DETAILS, docgetShipmentDetailsInp(docGetShipmentListOp), docgetShipmentDetailsTemplate());
	}
	
}
