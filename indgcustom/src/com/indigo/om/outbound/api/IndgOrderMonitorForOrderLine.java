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
	Map<String,String> orderLineMap = new HashMap<>();
	private static final String ORDER_MONITOR="IndgOrderMonitor";
	private static final String STATUS = "1100";	
	
	/**
	 * Logger for IndgOrderMonitorForOrderLine Instance 
	 * 
	 */
	private static YFCLogCategory logger = YFCLogCategory.instance(IndgOrderMonitorForOrderLine.class);
	
	/**
	 * This is the main invoke method
	 */
	
	 public YFCDocument invoke(YFCDocument inXml)  {
			 YFCDocument outXML=getOrderDetailsGroupedByShipNode(inXml);
			return outXML;
		 }
	 
	  private YFCDocument getOrderDetailsGroupedByShipNode(YFCDocument inXml){
		    YFCElement orderElement = inXml.getDocumentElement().getChildElement(XMLLiterals.ORDER).getChildElement(XMLLiterals.ORDER_LINES);
			System.out.println("-----orderElement---"+orderElement);
		    YFCIterable<YFCElement> yfsItrator = orderElement.getChildren();
		    for(YFCElement orderLine: yfsItrator) {
		      String shipNode = orderLine.getChildElement(XMLLiterals.ORDER_STATUSES).getChildElement(XMLLiterals.ORDER_STATUS).getAttribute(XMLLiterals.STATUS);
			  System.out.println("-----shipNode---"+shipNode);
		     // LoggerUtil.verboseLog("=================ShipNode========================", logger, shipNode);
		      if(XmlUtils.isVoid(orderLineMap.get(shipNode))) {
		    	  //orderLineMap.put(shipNode,shipNode);
		    	  if(shipNode.equals(STATUS)) {
		    		  invokeYantraService(getProperty(ORDER_MONITOR), inXml);
					  System.out.println("---------------ALERT----------");
		    	  }
		      }
		    }
		     return inXml;
		  }
	}
