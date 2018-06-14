package com.indigo.om.outbound.api;
/**
 * 
 * 
 * @author BSG170
 *
 */
import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;


public class BeforeChangeOrderUserExit extends AbstractCustomApi {
	private static final String EMPTY_STRING = "";
	private static final String YES="Y";
	private static final String MANUAL="MANUAL";
	private static final String CANCEL="CANCEL";
	 /**
	  * this method is the invoke point of the service.
	  * 
	  */
	 
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCElement eleInXml=inXml.getDocumentElement();
		
		if(XmlUtils.isVoid(eleInXml.getAttribute(XMLLiterals.ACTION))) {
		YFCIterable<YFCElement> yfsItrator = eleInXml.getChildElement(XMLLiterals.ORDER_LINES).getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderLine: yfsItrator) {
			if(!XmlUtils.isVoid(orderLine.getAttribute(XMLLiterals.ACTION))&& (orderLine.getAttribute(XMLLiterals.ACTION).
					equals(CANCEL)) && XmlUtils.isVoid(orderLine.getAttribute(XMLLiterals.MODIFICATION_REFRENCE_1)))
		
				orderLine.setAttribute(XMLLiterals.MODIFICATION_REFRENCE_1, MANUAL);
				invokeGetShipmentList(inXml);
		}
		
		}
		
		return inXml;

}
	/**
	 * this method forms input document for getShipmentList API
	 * @param inXml
	 * @return
	 */
	private YFCDocument inputXmlForGetShipmentList(YFCDocument inXml) {
	    YFCDocument getShipmentListDoc = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
	    YFCElement shipmentLineEle = getShipmentListDoc.getDocumentElement().createChild(XMLLiterals.SHIPMENT_LINES).createChild(XMLLiterals.SHIPMENT_LINE);
	    shipmentLineEle.setAttribute(XMLLiterals.ORDER_NO, inXml.getDocumentElement().getAttribute(XMLLiterals.ORDER_NO));
	    return getShipmentListDoc;
	  }
	/**
	 * this method forms template document for getShipmentList api
	 * @return
	 */
	
	public YFCDocument templateForGetShipmentList() {
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
	 * this method invokes getShipmentList api
	 * @param inXml
	 */
	private void invokeGetShipmentList(YFCDocument inXml)
	{
		YFCDocument docGetShipmentList=invokeYantraApi(XMLLiterals.GET_SHIPMENT_LIST, inputXmlForGetShipmentList(inXml),templateForGetShipmentList());
		if(docGetShipmentList.getDocumentElement().hasChildNodes()) {
		YFCElement eleOrderLines=inXml.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
		YFCIterable<YFCElement> yfsItrator=eleOrderLines.getChildren(XMLLiterals.ORDER_LINE);
		for(YFCElement orderLine: yfsItrator) {
			String sPrimeLineNo=orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO);
			isBackroomPickComplete(docGetShipmentList,sPrimeLineNo);
		}
		}
		
	}
	
	/**
	 *this method checks if isBackroomPickComplete attribute is set to 'Y' then it throws an exception stating order cannot
	 *be cancelled in this status else it invokes changeShipment method which invokes changeShipment api
	 * @param docGetShipmentList
	 * @param inXml
	 */
	private void isBackroomPickComplete(YFCDocument docGetShipmentList,String sPrimeLineNo)
	{
		YFCElement eleShipment=docGetShipmentList.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT)
				.getChildElement(XMLLiterals.SHIPMENT_LINES);
		YFCIterable<YFCElement> yfsItrator = eleShipment.getChildren(XMLLiterals.SHIPMENT_LINE);
		for(YFCElement shipmentLine: yfsItrator) {
			System.out.println(shipmentLine);
			if(shipmentLine.getAttribute(XMLLiterals.PRIME_LINE_NO).equals(sPrimeLineNo)) 
			if(!XmlUtils.isVoid(shipmentLine.getAttribute(XMLLiterals.BACKROOM_PICK_COMPLETE)) && shipmentLine.getAttribute(XMLLiterals.BACKROOM_PICK_COMPLETE).equals(YES))
			{
				throwException();
				break;
			}
			else
				changeShipment(docGetShipmentList,shipmentLine);  
			}
		
	}
	/**
	 * this method invokes changeShipment api
	 * @param docGetShipmentList
	 */
	private void changeShipment(YFCDocument docGetShipmentList, YFCElement shipmentLine) {
		YFCDocument docShipment=YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		YFCElement eleShipment=docShipment.getDocumentElement();
		YFCElement eleInputShipment=docGetShipmentList.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT);
		YFCElement eleInputShipmentLine=eleInputShipment.getChildElement(XMLLiterals.SHIPMENT_LINES).getChildElement(XMLLiterals.SHIPMENT_LINE);
		eleShipment.setAttribute( XMLLiterals.SELLER_ORGANIZATION_CODE, eleInputShipment.getAttribute(XMLLiterals.SELLER_ORGANIZATION_CODE));
		eleShipment.setAttribute(XMLLiterals.SHIPNODE, eleInputShipment.getAttribute(XMLLiterals.SHIPNODE));
		eleShipment.setAttribute(XMLLiterals.SHIPMENT_NO,eleInputShipment.getAttribute(XMLLiterals.SHIPMENT_NO));
		YFCElement eleShipmentLine=eleShipment.createChild(XMLLiterals.SHIPMENT_LINES).createChild(XMLLiterals.SHIPMENT_LINE);
		eleShipmentLine.setAttribute(XMLLiterals.ORDER_NO, shipmentLine.getAttribute(XMLLiterals.ORDER_NO));
		eleShipmentLine.setAttribute(XMLLiterals.ACTION, CANCEL);
		eleShipmentLine.setAttribute(XMLLiterals.SHIPMENT_LINE_NO, eleInputShipmentLine.getAttribute(XMLLiterals.SHIPMENT_LINE_NO));
		invokeYantraApi(XMLLiterals.CHANGE_SHIPMENT, docShipment);
	}
	private void throwException() {
		
		YFCDocument errorDoc = YFCDocument.createDocument("Errors");
        YFCElement eleErrors = errorDoc.getDocumentElement();
        YFCElement eleError = eleErrors.createChild("Error");
        eleError.setAttribute("ErrorCode", "ERRORCODE_ORDER_CANCEL_EXCEP");
        eleError.setAttribute("ErrorDescription", "Order cannot be cancelled in this status");
        
        throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_ORDER_CANCEL_EXCEP, errorDoc.toString()); 
	}
}