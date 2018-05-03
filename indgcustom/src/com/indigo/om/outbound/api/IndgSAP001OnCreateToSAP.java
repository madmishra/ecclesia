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
 * This service will invoked on On success of Create Order to
 * publish the Child Order information to SAP. 
 * 
 * @author BSG109
 *
 */
public class IndgSAP001OnCreateToSAP extends AbstractCustomApi{
  Map<String,List<YFCElement>> orderLineMap = new HashMap<>();
  private static final String DROP_MESSAGE_TO_SAP ="INDG_DropCreateMsgToSAP";
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    String inputDocString = inXml.toString();
    YFCDocument inputDocForSAP = YFCDocument.getDocumentFor(inputDocString);
    getOrderLinesGroupedByShipNode(inputDocForSAP);
    createChildOrderForSAP(inputDocForSAP);
    return inXml;
  }
  
  /**
   * 
   * This method iterate throw the OrderLines and add
   * them to the Map with list of OrderLine Elements. 
   * 
   */
  private void getOrderLinesGroupedByShipNode(YFCDocument inputDocForSAP){
    YFCElement orderElement = inputDocForSAP.getDocumentElement().getChildElement(XMLLiterals.ORDER_LINES);
    YFCIterable<YFCElement> yfsItrator = orderElement.getChildren(XMLLiterals.ORDER_LINE);
    for(YFCElement orderLine: yfsItrator) {
      List<YFCElement> orderLineList;
      String shipNode = orderLine.getAttribute(XMLLiterals.SHIPNODE);
      if(XmlUtils.isVoid(orderLineMap.get(shipNode))) {
        orderLineList = new ArrayList<>();
        orderLineList.add(orderLine);
        orderLineMap.put(shipNode,orderLineList);
      } else {
        orderLineList = orderLineMap.get(shipNode);
        orderLineList.add(orderLine);
        orderLineMap.put(shipNode,orderLineList);
      }
      YFCNode parent = orderLine.getParentNode();
      parent.removeChild(orderLine);
    }
  }
  
  /**
   * 
   * This method forms input for SAP and invoke the 
   * service to drop the message
   * 
   * @param inXml
   */
  private void createChildOrderForSAP(YFCDocument inputDocForSAP) {
    for (Entry<String, List<YFCElement>> entry : orderLineMap.entrySet()) {
      List<YFCElement> orderLineList = orderLineMap.get(entry.getKey());
      String orderDocString = inputDocForSAP.toString();
      YFCDocument orderDocForSAP = YFCDocument.getDocumentFor(orderDocString);
      YFCElement orderLinesEle = orderDocForSAP.getDocumentElement()
          .getChildElement(XMLLiterals.ORDER_LINES);
      for(YFCElement lineEle : orderLineList) {
        orderLinesEle.importNode(lineEle);
      }
      invokeYantraService(DROP_MESSAGE_TO_SAP, orderDocForSAP);
    }
  }

}
