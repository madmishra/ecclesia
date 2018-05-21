package com.indigo.om.outbound.api;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.bridge.sterling.consts.ValueConstants;
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
	Map<String,ArrayList<YFCElement>> orderLineMap = new HashMap<>();
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
		getOrderDetailsGroupedByShipNode(inXml);
		return inXml;
	}

	/**
	 * This method is creating a new Node List where all the OrderLines with created status is being published and creating the alert 
	 * 
	 */

	private void getOrderDetailsGroupedByShipNode(YFCDocument inXml){

		YFCNodeList<YFCElement> ordlineList = inXml.getElementsByTagName(XMLLiterals.ORDER_LINE);

		for (YFCElement curOrdLineElement : ordlineList) {
			String shipNode =curOrdLineElement.getAttribute(XMLLiterals.SHIP_NODE,ValueConstants.EMPTY_STRING);
			YFCNodeList<YFCElement> ordStatusEleList = curOrdLineElement.getChildElement(XMLLiterals.ORDER_STATUSES,true).getChildElement(XMLLiterals.ORDER_STATUS,true).getElementsByTagName(XMLLiterals.ORDER_STATUS);
			for (YFCElement curOrdStatusEle : ordStatusEleList) {
				String status = curOrdStatusEle.getAttribute(XMLLiterals.STATUS,ValueConstants.EMPTY_STRING);
				if(STATUS.equals(status)) {
					if(orderLineMap.containsKey(shipNode)) {
						orderLineMap.get(shipNode).add(curOrdLineElement);
					}else {
						ArrayList<YFCElement> alertOrderLineList = new ArrayList<>();
						alertOrderLineList.add(curOrdLineElement);
						orderLineMap.put(shipNode, alertOrderLineList);
					}
				}
			}

			System.out.println(orderLineMap.toString());

			for (ArrayList<YFCElement> ordLineList : orderLineMap.values()) {
				 YFCDocument inDocClone = inXml.getCopy();
				  YFCElement ordLinesEle = inDocClone.getDocumentElement().getChildElement(XMLLiterals.ORDER,true).getChildElement(XMLLiterals.ORDER_LINES,true);
				  inDocClone.removeChild(ordLinesEle);
				  ordLinesEle = inDocClone.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES,true);			  
				  for(int i=0; i<ordLineList.size() ; i++) {
					 XMLUtil.importNode(ordLinesEle,ordLineList.get(i));
				  }
				  System.out.println(ordLineList.toString());
				  invokeYantraService(getProperty(ORDER_MONITOR), inDocClone);		
			}
		}

	}

}
