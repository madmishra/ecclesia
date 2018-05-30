package com.indigo.om.outbound.api;


import java.util.HashMap;
import java.util.Map;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * 
 * @author Nikita Shukla
 *
 */

public class IndgOrderMonitorForStatus extends AbstractCustomApi {
	private static final String EMPTY_STRING="";
	Map<String,String> orderLineMap = new HashMap<>();
	private static final String ORDER_MONITOR="ORDER_MONITOR";
	private static final String STATUS = "Created";	
	/**
	 * This is the main invoke method
	 */
	
	 public YFCDocument invoke(YFCDocument inXml)  {
		 YFCElement inXmlEle=inXml.getDocumentElement();
		 String orderNo=inXmlEle.getAttribute(XMLLiterals.ORDER_NO);
		 String enterpriseCode=inXmlEle.getAttribute(XMLLiterals.ENTERPRISE_CODE);
		 String documentType=inXmlEle.getAttribute(XMLLiterals.DOCUMENT_TYPE);
		 YFCDocument orderDetailDoc = invokeYantraApi(XMLLiterals.GET_ORDER_DETAILS, 
				 getOrderDetailsinput(orderNo,enterpriseCode,documentType), getOrderDetailsTemplate());
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
		 orderEle.getAttribute(XMLLiterals.ORDER_NO);
		 orderEle.getAttribute(XMLLiterals.DOCUMENT_TYPE);
		 orderEle.getAttribute(XMLLiterals.ENTERPRISE_CODE);
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
	      if(XmlUtils.isVoid(orderLineMap.get(shipNode))) {
	    	  orderLineMap.put(shipNode,shipNode);
	    	  if(shipNode.equals(STATUS)) {
	    		  invokeYantraService(getProperty(ORDER_MONITOR), inputDocForOrderMonitor);
	    	  }
	      }
	    }
	      
	  }
}
