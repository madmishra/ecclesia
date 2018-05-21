package com.indigo.om.outbound.api;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.LoggerUtil;
import com.bridge.sterling.utils.XMLUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
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
	 
	 /**
	  * This method is creating a new Node List where all the OrderLines with created status is being published and creating the alert 
	  * 
	  */
	 
	  private YFCDocument getOrderDetailsGroupedByShipNode(YFCDocument inXml){
		  
		  YFCNodeList<YFCElement> ordlineList = inXml.getElementsByTagName(XMLLiterals.ORDER_LINE);
		  ArrayList<YFCElement> alertOrderLineList = new ArrayList<>();
		  for (YFCElement curOrdLineElement : ordlineList) {
			  String shipNode = curOrdLineElement.getChildElement(XMLLiterals.ORDER_STATUSES,true).getChildElement(XMLLiterals.ORDER_STATUS,true).getAttribute(XMLLiterals.STATUS);
			  System.out.println("-----shipNode---"+shipNode);
		      LoggerUtil.verboseLog("=================ShipNode========================", logger, shipNode);
		      if(XmlUtils.isVoid(orderLineMap.get(shipNode)) && shipNode.equals(STATUS)) {
		    		  alertOrderLineList.add(curOrdLineElement);
					  System.out.println("---------------ALERT----------");
		      }
		}
		  
		  if(!XmlUtils.isVoid(alertOrderLineList)) {
			  YFCDocument inDocClone = inXml.getCopy();
			  YFCElement ordLinesEle = inDocClone.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES,true);
			  inDocClone.removeChild(ordLinesEle);
			  ordLinesEle = inDocClone.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES,true);			  
			  for(int i=0; i<alertOrderLineList.size() ; i++) {
				 XMLUtil.importNode(ordLinesEle,alertOrderLineList.get(i));
			  }
			  invokeYantraService(getProperty(ORDER_MONITOR), inXml);			  
		  }
		     return inXml;
		 }
	}
