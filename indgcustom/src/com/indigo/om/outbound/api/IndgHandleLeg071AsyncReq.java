package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
/**
 * 
 * @author Training02
 * 
 * Custom code consumes inXml and invokes getOrderLineList API if the output of API does not has ChildNodes then it will
 * invoke createAsyncReq API. If it has ChildNodesthen it will check for the status of the order line and if status is shipped then the message will be consumed 
 *  createAsyncReq API
 *
 */
public class IndgHandleLeg071AsyncReq extends AbstractCustomApi{
	
	 private static final String DOCUMENT_TYPE = "0001";
	 private static final String ORDER_STATUS = "3700";
	 private static final String ABANDONMENT_STATUS = "9000.100";
	 private static final String EMPTY_STRING = " ";
	 private static final String YES = "Y";
	 private static final String INDG_CREATE_RETURN_SYNC="Indg_CreateReturn_SYNC";
	 private static final String  INDG_LEG071_ASYNC_REQ= "Indg_Leg071_ASyncReq";
	 private  String IS_SHIPPED= "N";
	 /**
	  * This method is the invoke point of the service.
	  * 
	  */
	 
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCElement eleroot = inXml.getDocumentElement();
		if(!YFCObject.isVoid(eleroot.getAttribute(XMLLiterals.MESSAGE_TYPE_ID))) {
		YFCElement eleOrder = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER)
				.getChildElement(XMLLiterals.ORDER_LINES).getChildElement(XMLLiterals.ORDER_LINE);
		YFCDocument docGetOrderList = invokeYantraApi(XMLLiterals.GET_ORDER_LINE_LIST, invokegetOrderLineList(eleOrder, inXml), getOrderLineListTemplate());
		YFCElement eleOrderLine = docGetOrderList.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE);
		if(eleOrderLine != null) {
			checkOrderstatus(docGetOrderList,inXml);
		}
		else
		{
			invokeCreateAsynRequestAPI(inXml);
		}
		}
		
		return inXml;
	}
	
	
	private YFCDocument invokegetOrderLineList(YFCElement orderLine, YFCDocument inXml)
	{
		YFCElement eleOrder = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
		YFCDocument docOrderLine = YFCDocument.createDocument(XMLLiterals.ORDER_LINE);
		YFCElement eleOrderLine = docOrderLine.getDocumentElement();
		eleOrderLine.setAttribute(XMLLiterals.PRIME_LINE_NO, orderLine.getAttribute(XMLLiterals.PRIME_LINE_NO) );
		YFCElement eleOrderInput = eleOrderLine.createChild(XMLLiterals.ORDER);
		eleOrderInput.setAttribute(XMLLiterals.ORDER_NO, eleOrder.getAttribute(XMLLiterals.PARENT_LEGACY_OMS_ORDER_NO));
		eleOrderInput.setAttribute(XMLLiterals.DOCUMENT_TYPE, DOCUMENT_TYPE);
		eleOrderInput.setAttribute(XMLLiterals.ENTERPRISE_CODE, eleOrder.getAttribute(XMLLiterals.ENTERPRISE_CODE));
		return docOrderLine;
	}
	
	private YFCDocument getOrderLineListTemplate()
	{
		YFCDocument docOrder = YFCDocument.createDocument(XMLLiterals.ORDER_LINE_LIST);
		YFCElement eleOrderList = docOrder.getDocumentElement();
		YFCElement eleOrder =  eleOrderList.createChild(XMLLiterals.ORDER_LINE);
		YFCElement eleOrderStatus = eleOrder.createChild(XMLLiterals.ORDER_STATUSES).createChild(XMLLiterals.ORDER_STATUS);
		eleOrderStatus.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
		eleOrderStatus.setAttribute(XMLLiterals.STATUS_QTY, EMPTY_STRING);
		return docOrder;
	}
	
	private void checkOrderstatus(YFCDocument docGetOrderList, YFCDocument inXml)
	{
		YFCIterable<YFCElement>  eleOrderStatus = docGetOrderList.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINE).
				getChildElement(XMLLiterals.ORDER_STATUSES).getChildren(XMLLiterals.ORDER_STATUS);
				for(YFCElement orderStatus : eleOrderStatus)
				{	
					if(orderStatus.getAttribute(XMLLiterals.STATUS).contains(ORDER_STATUS) || 
							orderStatus.getAttribute(XMLLiterals.STATUS).equals(ABANDONMENT_STATUS)  ){
						IS_SHIPPED = YES;
						break;
					}
			}
					if(IS_SHIPPED.equals(YES))
					invokeYantraService(INDG_CREATE_RETURN_SYNC, inXml);
					else
					invokeCreateAsynRequestAPI(inXml);
	}

	
	
	

	
	private void invokeCreateAsynRequestAPI(YFCDocument inXml) {
		YFCDocument docCreateAsyncRequest = YFCDocument.createDocument(XMLLiterals.CREATE_ASYNC_REQUEST);
		YFCElement eleAPI = docCreateAsyncRequest.getDocumentElement().createChild(XMLLiterals.API);
		eleAPI.setAttribute(XMLLiterals.IS_SERVICE, YES);
		eleAPI.setAttribute(XMLLiterals.NAME, INDG_LEG071_ASYNC_REQ);
		YFCElement ele = eleAPI.createChild(XMLLiterals.INPUT);
		YFCElement eleRoot = inXml.getDocumentElement();
		YFCElement eleImport = docCreateAsyncRequest.importNode(eleRoot, true);
		ele.appendChild(eleImport);
		invokeYantraApi(XMLLiterals.CREATE_ASYNC_REQUEST, docCreateAsyncRequest);
	}
}
	
