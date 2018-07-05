package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class InventoryShortReasonCode extends AbstractCustomApi
{
	 private static final String CANCEL = "Cancel";
	 private static final String ONE = "1";
	 /**
	  * This method is the invoke point of the service.
	  * 
	  */
	 
	@Override
	public YFCDocument invoke(YFCDocument inXml)
	{
		invokeChangeShipment(inXml);
		
		return null;

	}
	private void invokeChangeShipment(YFCDocument inXml)
	{
		invokeYantraApi(XMLLiterals.CHANGE_SHIPMENT, changeShipmentDoc(inXml));
	}
	private YFCDocument changeShipmentDoc(YFCDocument inXml)
	{
		YFCElement eleinpShipment = inXml.getDocumentElement();
		YFCDocument docChangeShipment = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
		YFCElement eleShipment = docChangeShipment.getDocumentElement();
		eleShipment.setAttribute(XMLLiterals.SELLER_ORGANIZATION_CODE,XMLLiterals.INDIGO_CA );
		eleinpShipment.setAttribute(XMLLiterals.SHIPMENT_KEY, eleinpShipment.getDateAttribute(XMLLiterals.SHIPMENT_KEY));
		YFCElement eleShipmentLine = eleinpShipment.createChild(XMLLiterals.SHIPMENT_LINES)
				.createChild(XMLLiterals.SHIPMENT_LINE);
		YFCIterable<YFCElement> yfsItrator1 = eleinpShipment.getChildElement(XMLLiterals.SHIPMENT_LINES).getChildren(XMLLiterals.SHIPMENT_LINE);
		for(YFCElement shipmentLine: yfsItrator1)
		{
			eleShipmentLine.setAttribute(XMLLiterals.ACTION, CANCEL);
			eleShipmentLine.setAttribute(XMLLiterals.SHIPMENT_LINE_KEY, shipmentLine.getAttribute(XMLLiterals.SHIPMENT_LINE_KEY));
			eleShipmentLine.setAttribute(XMLLiterals.SUB_LINE_NO, ONE);
		}
		return docChangeShipment;
		
		
	}
}
