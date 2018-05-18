package com.indigo.om.outbound.api;


import java.util.HashMap;
import java.util.Map;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.LoggerUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.*;



/**
 * 
 * 
 * @author Nikita Shukla
 *
 */

public class IndgOrderMonitorForOrderLine extends AbstractCustomApi {
	private static final String EMPTY_STRING="";
	Map<String,String> orderLineMap = new HashMap<>();
	private static final String ORDER_MONITOR="IndgOrderMonitor";
	private static final String STATUS = "Created";	
	
	/**
	 * Logger for IndgOrderMonitorForOrderLine Instance 
	 * 
	 */
	private static YFCLogCategory logger = YFCLogCategory.instance(IndgOrderMonitorForOrderLine.class);
	
	/**
	 * This is the main invoke method
	 */
	
	 public YFCDocument invoke(YFCDocument inXml)  {
		 YFCElement inXmlEle=inXml.getDocumentElement();
		 String orderNo=inXmlEle.getChildElement(XMLLiterals.ORDER).getAttribute(XMLLiterals.ORDER_NO);
		 String SellerOrganizationCode=inXmlEle.getChildElement(XMLLiterals.SELLER_ORGANIZATION_CODE).getAttribute(XMLLiterals.SELLER_ORGANIZATION_CODE);
		 String documentType=inXmlEle.getChildElement(XMLLiterals.DOCUMENT_TYPE).getAttribute(XMLLiterals.DOCUMENT_TYPE);
		 YFCDocument orderDetailDoc = invokeYantraApi(XMLLiterals.GET_ORDER_DETAILS, 
				 getOrderDetailsinput(orderNo,SellerOrganizationCode,documentType), getOrderDetailsTemplate());
		 getOrderDetailsGroupedByShipNode(orderDetailDoc);
		return inXml;
	 }
	 
	 /**
	  * This method is used to get all the attribute values from the input
	  * 
	  * @param orderNo
	  * @param enterpriseCode
	  * @param documentType
	  * @return
	  */
	 
	 public YFCDocument getOrderDetailsinput(String orderNo,String enterpriseCode,String documentType ) {
		 YFCDocument getOrderDetailsinput=YFCDocument.createDocument(XMLLiterals.ORDER);
		 YFCElement orderEle=getOrderDetailsinput.getDocumentElement();
		 orderEle.setAttribute(XMLLiterals.ORDER_NO,orderNo);
		 orderEle.setAttribute(XMLLiterals.DOCUMENT_TYPE,enterpriseCode);
		 orderEle.setAttribute(XMLLiterals.ENTERPRISE_CODE,documentType);
		 LoggerUtil.verboseLog("=================getOrderDetailsinput========================", logger, getOrderDetailsinput);
		 return getOrderDetailsinput;
	 }
	 
	 /**
	  * This method is used for creating the getOrderDetails template
	  * 
	  * @return
	  */
	 
	 public YFCDocument getOrderDetailsTemplate() {
		 YFCDocument getOrderDetailsTempDoc=YFCDocument.createDocument(XMLLiterals.ORDER);
		 YFCElement orderEle=getOrderDetailsTempDoc.getDocumentElement();
		 orderEle.setAttribute(XMLLiterals.ORDER_NO,EMPTY_STRING);
		 YFCElement orderLineEle=orderEle.createChild(XMLLiterals.ORDER_LINES).createChild(XMLLiterals.ORDER_LINE);
		 orderLineEle.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
		 orderLineEle.setAttribute(XMLLiterals.PRIME_LINE_NO, EMPTY_STRING);
		 orderLineEle.setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
		 YFCElement extnEle=orderLineEle.createChild(XMLLiterals.EXTN);
		 extnEle.setAttribute(XMLLiterals.EXTN_LEGACY_OMS_CHILD_ORDERNO, EMPTY_STRING);
		 LoggerUtil.verboseLog("=================getOrderDetailsTempDoc========================", logger, getOrderDetailsTempDoc);
		 return getOrderDetailsTempDoc;
		 
	 }
	 
	 
	 /**
	   * 
	   * This method iterate throw the OrderLines and add
	   * them to the Map with list of OrderLine Elements. 
	   * 
	   */
	  private void getOrderDetailsGroupedByShipNode(YFCDocument inputDocForOrderMonitor){
	    YFCElement orderElement = inputDocForOrderMonitor.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
	    YFCIterable<YFCElement> yfsItrator = orderElement.getChildren(XMLLiterals.ORDER_LINE);
	    for(YFCElement orderLine: yfsItrator) {
	      String shipNode = orderLine.getAttribute(XMLLiterals.SHIPNODE);
	      LoggerUtil.verboseLog("=================ShipNode========================", logger, shipNode);
	      if(XmlUtils.isVoid(orderLineMap.get(shipNode))) {
	    	  orderLineMap.put(shipNode,shipNode);
	    	  if(shipNode.equals(STATUS)) {
	    		  invokeYantraService(getProperty(ORDER_MONITOR), inputDocForOrderMonitor);
	    	  }
	      }
	    }
	      
	  }
}
