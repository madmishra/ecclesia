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
 * @author BGS168
 *
 * Custom API to consume LegacyOMS051 message and cancel the given lines 
 * The lines will be grouped by ReasonCode and send it to ChangeOrder API.
 * 
 */
public class IndgLegacy051ToCancel extends AbstractCustomApi{
	 private static final String SUBLINE_VALUE = "1";
	 private static final String ACTION_VALUE = "CANCEL";
	 private static final String EMPTY_STRING = "";
	 private static final String YES = "Y";
	 private static final String NO = "N";
	 private String orderNo = "";
	 private String orderType = "";
	 private String documentType = "";
	 private String enterpriseCode = "";
	 private String legacyOmsOrderNo = "";
	 YFCDocument docLegacy051Input = null;
	 YFCDocument docInputXml = null;
	 YFCDocument docLegacy051 = null;
	 private static final String CANCELLATION_TYPE = "LEGACY";
	 private static final String REASON_CODE = "03";
	 private static final String CALL_LEGACYOMS051_SERVICE = "CALL_LEGACYOMS051_SERVICE";	 
	 private static final String CUSTOMER_PONO = "CustomerPoNo";
	 private static final String CANCEL = "Cancel";
	 
	 /**
	  * This method is the invoke point of the service.
	  * 
	  */
	 
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		orderNo = inXml.getDocumentElement().getAttribute(XMLLiterals.ORDER_NO);
		enterpriseCode = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).
				getAttribute(XMLLiterals.ENTERPRISE_CODE);
	    documentType = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).
	    		getAttribute(XMLLiterals.DOCUMENT_TYPE);
	    orderType = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).
	    		getAttribute(XMLLiterals.ORDER_TYPE);
		String inputDocString = inXml.toString();
	    docLegacy051Input = YFCDocument.getDocumentFor(inputDocString);
	    docInputXml = YFCDocument.getDocumentFor(inputDocString);
	    YFCDocument shipmentListApiOp = getShipmentList();
	    getShipmentLinesStatus(shipmentListApiOp);
	    docSetAttributesForCancel(inXml);
		return inXml;
	}
	
	/**
	 * This method forms the Input XML for getShipmentListAPI
	 * 
	 * @return
	 */
	
	public YFCDocument inputXmlForGetShipmentList() {
	    YFCDocument getShipmentListDoc = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
	    YFCElement shipmentLinesEle = getShipmentListDoc.getDocumentElement().createChild(XMLLiterals.SHIPMENT_LINES);
	    YFCElement shipmentLineEle = shipmentLinesEle.createChild(XMLLiterals.SHIPMENT_LINE);
	    shipmentLineEle.setAttribute(XMLLiterals.ORDER_NO, orderNo);
	    return getShipmentListDoc;
	  }
	
	/**
	  * This method forms template for the getShipmentListAPI
	  * 
	  * @return
	  */
	
	public YFCDocument inputTemplateForGetShipmentList() {
	    YFCDocument getShipmentListTemp = YFCDocument.createDocument(XMLLiterals.SHIPMENTS);
	    YFCElement shipmentEle = getShipmentListTemp.getDocumentElement().createChild(XMLLiterals.SHIPMENT);
	    YFCElement shipmentLinesEle = shipmentEle.createChild(XMLLiterals.SHIPMENT_LINES);
	    YFCElement shipmentLineEle = shipmentLinesEle.createChild(XMLLiterals.SHIPMENT_LINE);
	    shipmentLineEle.setAttribute(XMLLiterals.BACKROOM_PICK_COMPLETE, EMPTY_STRING);
	    shipmentLineEle.setAttribute(XMLLiterals.BACKROOM_PICK_QUANTITY, EMPTY_STRING);
	    shipmentLineEle.setAttribute(XMLLiterals.ORDER_NO, EMPTY_STRING);
	    shipmentLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
	    shipmentLineEle.setAttribute(XMLLiterals.SHIPMENT_LINE_NO, EMPTY_STRING);
	    YFCElement orderLineEle = shipmentLineEle.createChild(XMLLiterals.ORDER_LINE);
	    orderLineEle.setAttribute(XMLLiterals.DELIVERY_METHOD, EMPTY_STRING);
	    orderLineEle.setAttribute(XMLLiterals.DEPARTMENT_CODE, EMPTY_STRING);
	    return getShipmentListTemp;
	  }
	
	/**
	 * This method calls getShipmentList API
	 * 
	 * @return
	 */
	
	public YFCDocument getShipmentList(){
	    return  invokeYantraApi(XMLLiterals.GET_SHIPMENT_LIST, inputXmlForGetShipmentList(), inputTemplateForGetShipmentList());
	 }
	
	/**
	 * This method fetches the shipNode and cancellationCode and passes
	 * it to another method where order lines can be grouped based on it
	 * 
	 * @param shipmentListApiOp
	 */
	
	private void getShipmentLinesStatus(YFCDocument shipmentListApiOp){
		if(!XmlUtils.isVoid(shipmentListApiOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT))) {
			orderLineIsPickedStatus(shipmentListApiOp);
			YFCElement orderLineEle = docLegacy051.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
			if(orderLineEle.hasChildNodes()) {
				callLegacyOMS052opQueue(docLegacy051);
			}
		}
	 }
	
	/**
	 * this method is used to check if the order Lines are picked 
	 * or not. If it is already picked it will not be sent to get
	 * cancelled.
	 * 
	 * @param shipmentListApiOp
	 */
	
	private void orderLineIsPickedStatus(YFCDocument shipmentListApiOp) {
		legacyOmsOrderNo = shipmentListApiOp.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT).
				getAttribute(CUSTOMER_PONO);
		docLegacy051 = YFCDocument.createDocument(XMLLiterals.ORDER);
		YFCElement orderLines = docLegacy051.getDocumentElement().createChild(XMLLiterals.ORDER_LINES);
		YFCElement orderLinesEle = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
		YFCIterable<YFCElement> yfsItratorShipNode = orderLinesEle.getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderLineEle : yfsItratorShipNode) {
			docSetAttributes(orderLines, orderLineEle, shipmentListApiOp);
		}
		docSetAttributesToLeg051();
	}
	
	/**
	 * This code checks for the BackroomPickAttribute and if the value
	 * is Y, it picks the element and appends it to another doc.
	 * 
	 * @param orderLines
	 * @param orderLineEle
	 * @param shipmentListApiOp
	 */
	
	private void docSetAttributes(YFCElement orderLines, YFCElement orderLineEle, YFCDocument shipmentListApiOp) {
		String shipNode = orderLineEle.getAttribute(XMLLiterals.SHIPNODE);
		String primeLineNo = orderLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO);
		YFCElement shipmentEle = XPathUtil.getXPathElement(shipmentListApiOp, "/Shipments/Shipment[@ShipNode=\""+shipNode+"\"]");
		if(!XmlUtils.isVoid(shipmentEle)) {
			orderLineEle.setAttribute(XMLLiterals.MODIFYTS, shipmentEle.getAttribute(XMLLiterals.MODIFYTS));
			orderLineEle.setAttribute(XMLLiterals.LEGACY_OMS_ORDER_NO, legacyOmsOrderNo);
			orderLineEle.setAttribute(XMLLiterals.IS_PROCESSED, NO);
			YFCIterable<YFCElement> yfsItratorPrimeLine = shipmentEle.getChildElement(XMLLiterals.SHIPMENT_LINES)
					.getChildren(XMLLiterals.SHIPMENT_LINE);
			for(YFCElement shipmentLineEle : yfsItratorPrimeLine) {
				if(primeLineNo.equals(shipmentLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO)) && 
					(!XmlUtils.isVoid(shipmentLineEle.getAttribute(XMLLiterals.BACKROOM_PICK_COMPLETE)))) {
					if(shipmentLineEle.getAttribute(XMLLiterals.BACKROOM_PICK_COMPLETE).equals(YES)) {
						orderLines.importNode(orderLineEle);
						YFCNode parent = orderLineEle.getParentNode();
					    parent.removeChild(orderLineEle);
					}
				}
				else if(primeLineNo.equals(shipmentLineEle.getAttribute(XMLLiterals.PRIME_LINE_NO)) && 
						(XmlUtils.isVoid(shipmentLineEle.getAttribute(XMLLiterals.BACKROOM_PICK_COMPLETE)))) {
					String sellerOrganizationCode = shipmentEle.getAttribute(XMLLiterals.SELLER_ORGANIZATION_CODE);
					String node = shipmentEle.getAttribute(XMLLiterals.SHIPNODE);
					String shipmentNo = shipmentEle.getAttribute(XMLLiterals.SHIPMENT_NO);
					changeShipment(shipmentLineEle,sellerOrganizationCode,node,shipmentNo);
				}
			}
		}
	}
	
	/**
	 * this code sets the header attributes to the doc
	 * 
	 * @param docLegacy051
	 */
	
	private void docSetAttributesToLeg051() {
		docLegacy051.getDocumentElement().setAttribute(XMLLiterals.ORDER_NO, orderNo);
		docLegacy051.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCode);
		docLegacy051.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, documentType);
		docLegacy051.getDocumentElement().setAttribute(XMLLiterals.ORDER_TYPE, orderType);	
	}
	
	/**
	 * This method appends the attributes to for the input for
	 * changeOrder API
	 * 
	 * @param docChangeOrderApiInput
	 * @param docChangeOrderInputLines
	 */
	
	private void docSetAttributesForCancel(YFCDocument inXml) {
		YFCDocument docChangeOrderApiInput = YFCDocument.createDocument(XMLLiterals.ORDER);
		String reasonCode = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).
				getChildElement(XMLLiterals.ORDER_LINES).getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.CANCELLATION_REASON_CODE);
		String reasonText = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER).
				getChildElement(XMLLiterals.ORDER_LINES).getChildElement(XMLLiterals.ORDER_LINE).getAttribute(XMLLiterals.CANCELLATION_TEXT);
		docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.MODIFICATION_REASON_CODE, reasonCode);
		docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.MODIFICATION_REASON_TEXT, reasonText);
		docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.ORDER_NO, orderNo);
		docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, enterpriseCode);
		docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.DOCUMENT_TYPE, documentType);
		docChangeOrderApiInput.getDocumentElement().setAttribute(XMLLiterals.MODIFICATION_REFRENCE_1, CANCELLATION_TYPE);
		docInputChangeOrderApi(docChangeOrderApiInput);
	}
	
	/**
	 * This method appends the attributes for ChangeOrder API and
	 * calls the API.
	 * 
	 * @param docChangeOrderApiInput
	 * @param inputEle
	 * @param orderLinesElement
	 */
	
	private void docInputChangeOrderApi(YFCDocument docChangeOrderApiInput) {
		 YFCElement orderLinesElement = docChangeOrderApiInput.getDocumentElement().createChild(XMLLiterals.ORDER_LINES);
		 YFCIterable<YFCElement> yfsItrator = docLegacy051Input.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).
				  getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES).getChildren(XMLLiterals.ORDER_LINE);
		 for(YFCElement orderLine : yfsItrator) {
			String primeLineNo = orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO);
			YFCElement odrLineQtyEle = XPathUtil.getXPathElement(docInputXml, "/OrderMessage/MessageBody/Order/OrderLines/"
					+ "OrderLine[@PrimeLineNo=\""+primeLineNo+"\"]");
			String orderedQty = odrLineQtyEle.getAttribute(XMLLiterals.QUANTITY_AFTER_CANCELLATION);
			String cancellationReqId = odrLineQtyEle.getAttribute(XMLLiterals.CANCELLATION_REQUEST_ID);
			YFCElement orderLineEle = orderLinesElement.createChild(XMLLiterals.ORDER_LINE);
			orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, primeLineNo);
			orderLineEle.setAttribute(XMLLiterals.SUB_LINE_NO, SUBLINE_VALUE);
			orderLineEle.setAttribute(XMLLiterals.ACTION, ACTION_VALUE);
			orderLineEle.setAttribute(XMLLiterals.CONDITION_VARIABLE_1, cancellationReqId);
			orderLineEle.setAttribute(XMLLiterals.CONDITION_VARIABLE_2, REASON_CODE);
			orderLineEle.setAttribute(XMLLiterals.ORDERED_QTY, orderedQty);
		}
		 invokeYantraApi(XMLLiterals.CHANGE_ORDER_API, docChangeOrderApiInput);  
	}
	
	/**
	 * This code sends the doc to Legacy052 queue
	 * @param doc
	 */
	
	private void callLegacyOMS052opQueue(YFCDocument doc) {
	     invokeYantraService(getProperty(CALL_LEGACYOMS051_SERVICE), doc);
	}
	
	/**
	 * This method invokes changeOrder API 
	 */
	private void changeShipment(YFCElement shipmentLineEle, String sellerOrganizationCode, String node, String shipmentNo)
	{
		YFCDocument docShipment=YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		YFCElement eleShipment=docShipment.getDocumentElement();
		eleShipment.setAttribute( XMLLiterals.SELLER_ORGANIZATION_CODE, sellerOrganizationCode);
		eleShipment.setAttribute(XMLLiterals.SHIPNODE, node);
		eleShipment.setAttribute(XMLLiterals.SHIPMENT_NO,shipmentNo);
		YFCElement eleShipmentLine=eleShipment.createChild(XMLLiterals.SHIPMENT_LINES).createChild(XMLLiterals.SHIPMENT_LINE);
		eleShipmentLine.setAttribute(XMLLiterals.ORDER_NO, shipmentLineEle.getAttribute(XMLLiterals.ORDER_NO));
		eleShipmentLine.setAttribute(XMLLiterals.ACTION, CANCEL);
		eleShipmentLine.setAttribute(XMLLiterals.SHIPMENT_LINE_NO, shipmentLineEle.getAttribute(XMLLiterals.SHIPMENT_LINE_NO));
		invokeYantraApi(XMLLiterals.CHANGE_SHIPMENT, docShipment);
	}
}
