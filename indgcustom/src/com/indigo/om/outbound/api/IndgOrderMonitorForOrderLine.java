package com.indigo.om.outbound.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;

/**
 * 
 * This service will be called on Monitor Event to
 * split Order by Ship Node
 * 
 * @author BSG109
 *
 */
public class IndgOrderMonitorForOrderLine extends AbstractCustomApi{
  Map<String,List<YFCElement>> orderLineMap = new HashMap<>();
  private static final String ORDER_MONITOR="ORDER_MONITOR";
  private static final String STATUS = "1100";
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    String inputDocString = inXml.toString();
    YFCDocument inputDocForMonitor = YFCDocument.getDocumentFor(inputDocString);
    getOrderLinesGroupedByShipNode(inputDocForMonitor);
    createChildOrderForMonitor(inputDocForMonitor);
    return inXml;
  }
  
  /**
   * 
   * This method iterate throw the OrderLines and add
   * them to the Map with list of OrderLine Elements. 
   * 
   */
  private void getOrderLinesGroupedByShipNode(YFCDocument inputDocForMonitor){
    YFCElement orderLineListEle = inputDocForMonitor.getDocumentElement().getChildElement(XMLLiterals.ORDER).
    		getChildElement(XMLLiterals.ORDER_LINES);
    YFCIterable<YFCElement> yfsItrator = orderLineListEle.getChildren(XMLLiterals.ORDER_LINE);
    for(YFCElement orderLine: yfsItrator) {
      List<YFCElement> orderLineList;
      String shipNode = orderLine.getAttribute(XMLLiterals.SHIPNODE);
      String status = orderLine.getChildElement(XMLLiterals.ORDER_STATUSES).getChildElement(XMLLiterals.ORDER_STATUS)
          .getAttribute(XMLLiterals.STATUS);
      if (STATUS.equals(status)) {
        if(XmlUtils.isVoid(orderLineMap.get(shipNode))) {
          orderLineList = new ArrayList<>();
          orderLineList.add(orderLine);
          orderLineMap.put(shipNode,orderLineList);
        } else {
          orderLineList = orderLineMap.get(shipNode);
          orderLineList.add(orderLine);
          orderLineMap.put(shipNode,orderLineList);
        }
      }
      YFCNode parent = orderLine.getParentNode();
      parent.removeChild(orderLine);
  }
}
  
  /**
   * 
   * This method forms input for Monitor alert and invoke the 
   * service to drop the message
   * 
   * @param inXml
   */
  private void createChildOrderForMonitor(YFCDocument inputDocForMonitor) {
    for (Entry<String, List<YFCElement>> entry : orderLineMap.entrySet()) {
      List<YFCElement> orderLineList = orderLineMap.get(entry.getKey());
      String orderDocString = inputDocForMonitor.toString();
      YFCDocument outputDocForMonitor = YFCDocument.getDocumentFor(orderDocString);
      YFCElement orderLinesEle = outputDocForMonitor.getDocumentElement().getChildElement(XMLLiterals.ORDER)
          .getChildElement(XMLLiterals.ORDER_LINES);
      for(YFCElement lineEle : orderLineList) {
        orderLinesEle.importNode(lineEle);
      }
      invokeYantraService(getProperty(ORDER_MONITOR), outputDocForMonitor);
    }
  }
}
