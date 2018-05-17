package com.indigo.inventory;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * This is an Custom service to insert log record for
 * inventory changes in Sterling 
 * 
 * @author BSG109
 *
 */
public class IndgInsertAdjtLogForConfirmShip extends AbstractCustomApi{

  private static final String NEGATIVE_SYMBOL = "-";
  private static final String SALES_ORDER_DOC_TYPE = "0001";
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    YFCElement shipmentEle = inXml.getDocumentElement().getChildElement(XMLLiterals.SHIPMENT_LINES);
    String shipNode = inXml.getDocumentElement().getAttribute(XMLLiterals.SHIPNODE);
    if(SALES_ORDER_DOC_TYPE.equals(inXml.getDocumentElement()
        .getAttribute(XMLLiterals.DOCUMENT_TYPE))) {
      YFCIterable<YFCElement> yfcItrator = shipmentEle.getChildren(XMLLiterals.SHIPMENT_LINE);
      for(YFCElement ShipmentLineEle:yfcItrator) {
        insertInvAdjLog(ShipmentLineEle,shipNode);
      }
    }
    return inXml;
  }

  /**
   * 
   * This method insert the record for Inventory log table
   * 
   * @param inXml
   * @param shipNode
   */
  private void insertInvAdjLog(YFCElement shipmentLineEle,String shipNode) {
    YFCDocument invAdjLogDoc = YFCDocument.createDocument(XMLLiterals.INDG_INV_ADJUSTMENT_LOG);
    YFCElement invAdjLogEle = invAdjLogDoc.getDocumentElement();
    invAdjLogEle.setAttribute(XMLLiterals.ITEM_ID, shipmentLineEle
        .getAttribute(XMLLiterals.ITEM_ID));
    invAdjLogEle.setAttribute(XMLLiterals.SHIPNODE, shipNode);
    invAdjLogEle.setAttribute(XMLLiterals.QUANTITY, NEGATIVE_SYMBOL+shipmentLineEle
        .getAttribute(XMLLiterals.QUANTITY));
    invokeYantraService(XMLLiterals.INDG_INV_ADJ_LOG_CREATE, invAdjLogDoc);
  }
}
