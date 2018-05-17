package com.indigo.inventory;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.indigo.masterupload.itemfeedupload.IndgManageItemFeed;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * This is an custom service to manage Delta
 * inventory feed from the Different Source and
 * manages the Inventory sync between systems
 * 
 * @author BSG109
 *
 */
public class IndgManageInvDeltaSync extends AbstractCustomApi {

  private static final String FLAG_YES = "Y";
  private static final String POS_SALES="POS Sales";
  private static final String DEFAULT_UNIT_OF_MEASURE = "EACH";
  private static final String INDIGO_CA = "Indigo_CA";
  private static final String ONHAND = "ONHAND";
  
  /**
   * This is the invoke point for the service
   * 
   */
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    manageInvFeedBasedOnMovement(inXml);
    return inXml;
  }
  
  /**
   * This method call adjust inventory for non cycle count
   * adjustment if the transaction date matches
   * 
   */
  private void manageAdjustmentInvFeed(YFCDocument inXml) {
    YFCElement itemsEle = inXml.getDocumentElement();
    YFCIterable<YFCElement> yfsItrator = itemsEle.getChildren(XMLLiterals.ITEM);
    for(YFCElement itemEle : yfsItrator) {
      String itemID = itemEle.getAttribute(XMLLiterals.ITEM_ID);
      String shipNode = itemEle.getAttribute(XMLLiterals.SHIPNODE);
      if(canApplyInvAdjustment(getSyncCtrlList(itemID,shipNode),itemEle,XMLLiterals.TRANSACTION_DATE)) {
        applyAdjustInvForDelta(itemEle);
      }
    }
  }
  
  /**
   * This method gets Full SYNC Information for Item and Ship Node
   * passed from INDG_INV_FULL_SYNC table
   * 
   * @param itemID
   * @param shipNode
   * @return
   */
  private YFCDocument getSyncCtrlList(String itemID,String shipNode) {
    YFCDocument fullSyncInDoc = YFCDocument.createDocument(XMLLiterals.INDG_INV_SYNC_CTRL);
    YFCElement fullSyncEle = fullSyncInDoc.getDocumentElement();
    fullSyncEle.setAttribute(XMLLiterals.ITEM_ID, itemID);
    fullSyncEle.setAttribute(XMLLiterals.SHIPNODE, shipNode);
    return invokeYantraService(XMLLiterals.INV_FULL_SYNC_LIST_FLOW, fullSyncInDoc);
  }
  
  
  /**
   * This method gets Movement type from Feed and 
   * calls different method to manage Inventory based
   * on the movement type
   * 
   * @param inXml
   */
  private void manageInvFeedBasedOnMovement(YFCDocument inXml) {
    String movementType = XPathUtil.getXpathAttribute(inXml, "/Items/Item/@AdjustmentType");
    if(inXml.getDocumentElement().hasChildNodes()) {
      if(XMLLiterals.ABSOLUTE.equalsIgnoreCase(movementType)) {
        manageCycleCountSAPFeed(inXml);
      } else {
        manageAdjustmentInvFeed(inXml);
      }
    }
  }
  
  
  /**
   * 
   * This method validates the Generation and Transaction Date for Cycle Count
   * Feed.
   * 
   * @param fullSyncOpDoc
   * @return
   */
  private boolean canApplyInvAdjustment(YFCDocument fullSyncOpDoc,YFCElement itemEle,String dateType) {
    if(fullSyncOpDoc.getDocumentElement().hasChildNodes()) {
      String generationDateFullSync = XPathUtil.getXpathAttribute(fullSyncOpDoc, 
          "/INDGInvSyncCtrlList/INDGInvSyncCtrl/@"+dateType+"");
      String generationDateItem = itemEle.getAttribute(dateType);
      if(!XmlUtils.isVoid(generationDateItem) && !XmlUtils.isVoid(generationDateFullSync)) {
        return IndgManageItemFeed.validateTimeDifferenceWithMS(generationDateItem,generationDateFullSync);
      }
    }
    return true;
  }
  
  /**
   * This method insert the adjusted information into
   * custom INDG_INV_ADJUSMENT_LOG table audit purpose
   * 
   * @param itemEle
   */
  private void insertIntoAdjusmentLogTable(YFCElement itemEle) {
    YFCDocument inXml = YFCDocument.createDocument(XMLLiterals.INDG_INV_ADJUSTMENT_LOG);
    YFCElement inEle = inXml.getDocumentElement();
    inEle.setAttribute(XMLLiterals.ITEM_ID, itemEle.getAttribute(XMLLiterals.ITEM_ID));
    inEle.setAttribute(XMLLiterals.SHIPNODE, itemEle.getAttribute(XMLLiterals.SHIPNODE));
    inEle.setAttribute(XMLLiterals.QUANTITY, itemEle.getAttribute(XMLLiterals.QUANTITY));
    inEle.setAttribute(XMLLiterals.GENERATION_DATE, itemEle.getAttribute(XMLLiterals.GENERATION_DATE));
    inEle.setAttribute(XMLLiterals.TRANSACTION_DATE, itemEle.getAttribute(XMLLiterals.TRANSACTION_DATE));
    invokeYantraService(XMLLiterals.INDG_INV_ADJ_LOG_CREATE, inXml);
  }
  
  
  /**
   * 
   * This method manages Cycle Count SAP FEED
   * 
   * @param inXml
   */
  private void manageCycleCountSAPFeed(YFCDocument inXml) {
    YFCElement inEle = inXml.getDocumentElement();
    YFCIterable<YFCElement> yfsItrator = inEle.getChildren(XMLLiterals.ITEM);
    for(YFCElement itemEle : yfsItrator) {
      String itemID = itemEle.getAttribute(XMLLiterals.ITEM_ID);
      String shipNode = itemEle.getAttribute(XMLLiterals.SHIPNODE);
      YFCDocument syncCtrlListDoc = getSyncCtrlList(itemID,shipNode);
      if(canApplyInvAdjustment(syncCtrlListDoc,itemEle,XMLLiterals.TRANSACTION_DATE) && 
          canApplyInvAdjustment(syncCtrlListDoc,itemEle,XMLLiterals.GENERATION_DATE) ) {
        applyAdjustInvForCycleCount(itemEle);
      } else {
        removeNodeControl(shipNode,itemID);
      }
    }
  }
  
  /**
   * Insert or Update Sync control table for time stamp
   * 
   * @param inEle
   */
  private void insertOrUpdateSyncCtrlTS(YFCElement itemEle) {
    YFCDocument syncCtrlListOIn = getInputDocForFullSyncCtrl(itemEle);
    YFCElement syncCtrlEle = syncCtrlListOIn.getDocumentElement();
    syncCtrlEle.setAttribute(XMLLiterals.TRANSACTION_DATE, 
        itemEle.getAttribute(XMLLiterals.TRANSACTION_DATE));
    syncCtrlEle.setAttribute(XMLLiterals.GENERATION_DATE, 
        itemEle.getAttribute(XMLLiterals.GENERATION_DATE));
    YFCDocument syncCtrlListOp = getSyncCtrlList(itemEle.getAttribute(XMLLiterals.ITEM_ID)
        ,itemEle.getAttribute(XMLLiterals.SHIPNODE));
    if(syncCtrlListOp.getDocumentElement().hasChildNodes()) {
      invokeYantraService(XMLLiterals.UPDATE_FULL_SYNC_CTRL, syncCtrlListOIn);
    } else {
      invokeYantraService(XMLLiterals.CREATE_FULL_SYNC_CTRL, syncCtrlListOIn);
    }
  }
  
  /**
   * This method gets the FUll Sync Info from the
   * Sync control table for checking the time stamp
   * 
   * @return
   */
  private YFCDocument getInputDocForFullSyncCtrl(YFCElement itemEle) {
    YFCDocument syncCtrlDoc = YFCDocument.createDocument(XMLLiterals.INDG_INV_FULL_SYNC);
    YFCElement syncCtrlEle = syncCtrlDoc.getDocumentElement();
    syncCtrlEle.setAttribute(XMLLiterals.ITEM_ID, itemEle.getAttribute(XMLLiterals.ITEM_ID));
    syncCtrlEle.setAttribute(XMLLiterals.SHIPNODE, itemEle.getAttribute(XMLLiterals.SHIPNODE));
   return syncCtrlDoc;
  }
  
  /**
   * 
   * This method get the Inventory log for the item and ShipNode 
   * combination 
   * 
   */
  private YFCDocument getAdjLogInventoryList(YFCElement itemEle) {
    YFCDocument invAdjLogDoc = YFCDocument.createDocument(XMLLiterals.INDG_INV_ADJUSTMENT_LOG);
    YFCElement invAdjLogEle = invAdjLogDoc.getDocumentElement();
    invAdjLogEle.setAttribute(XMLLiterals.ITEM_ID, itemEle.getAttribute(XMLLiterals.ITEM_ID));
    invAdjLogEle.setAttribute(XMLLiterals.SHIPNODE, itemEle.getAttribute(XMLLiterals.SHIPNODE));
    return invokeYantraService(XMLLiterals.INDG_INV_ADJ_LOG_LIST, invAdjLogDoc);
  }
  
  /**
   * This method gets Adjustment log and calculate final
   * absolute quantity for Count Sync.
   * 
   * @param inEle
   */
  private double calculateAbsoluteQuantity(YFCElement inEle) {
    double totalAdjQty = 0.00;
    YFCDocument invAdjLogListDoc = getAdjLogInventoryList(inEle);
    YFCElement invAdjLoglistEle = invAdjLogListDoc.getDocumentElement();
    YFCIterable<YFCElement> yfcItrator = invAdjLoglistEle.getChildren(XMLLiterals.INDG_INV_ADJUSTMENT_LOG);
    for(YFCElement invAdjLog:yfcItrator) {
      String logTransactionDate = invAdjLog.getAttribute(XMLLiterals.TRANSACTION_DATE);
      String itemTransactionDate = inEle.getAttribute(XMLLiterals.TRANSACTION_DATE);
      double logQty = invAdjLog.getDoubleAttribute(XMLLiterals.QUANTITY);
      if(IndgManageItemFeed.validateTimeDifferenceWithMS(logTransactionDate,itemTransactionDate)) {
        totalAdjQty = totalAdjQty + logQty;
      }
    }
    return totalAdjQty;
  }
  
  /**
   * This method removes Node Control for the Node and Item 
   * combination. 
   * 
   * @param shipNode
   * @param Item
   */
  private void removeNodeControl(String shipNode, String itemID) {
    YFCDocument inXml = YFCDocument.createDocument(XMLLiterals.INVENTORY_NODE_CONTROL);
    inXml.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, shipNode);
    inXml.getDocumentElement().setAttribute(XMLLiterals.ITEM_ID, itemID);
    inXml.getDocumentElement().setAttribute(XMLLiterals.INVENTORY_PICTURE_CORRECT, FLAG_YES);
  }
  
  /**
   * 
   * This method adjust inventory for Delta Feeds
   * 
   * @param itemEle
   */
  private void applyAdjustInvForDelta(YFCElement itemEle) {
    YFCDocument adjDoc= YFCDocument.createDocument(XMLLiterals.ITEMS);
    YFCElement adjEle = adjDoc.getDocumentElement();
    
    if(XmlUtils.isVoid(itemEle.getAttribute(XMLLiterals.REASON_TEXT)) &&
        XmlUtils.isVoid(itemEle.getAttribute(XMLLiterals.REASON_CODE))) {
      itemEle.setAttribute(XMLLiterals.REASON_CODE,POS_SALES);
      itemEle.setAttribute(XMLLiterals.REASON_TEXT,POS_SALES);
    }
    itemEle.setAttribute(XMLLiterals.ADJUSTMENT_TYPE,
        XMLLiterals.ADJUSTMENT);
    itemEle.setAttribute(XMLLiterals.UNIT_OF_MEASURE, DEFAULT_UNIT_OF_MEASURE);
    itemEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, INDIGO_CA);
    itemEle.setAttribute(XMLLiterals.SUPPLY_TYPE, ONHAND);
    adjEle.importNode(itemEle);
    invokeYantraApi(XMLLiterals.ADJUST_INVENTORY_API, adjDoc);
    insertIntoAdjusmentLogTable(itemEle);
  }
  
  /**
   * This method adjust inventory for Cycle count delta Feeds
   * 
   * @param itemEle
   */
  private void applyAdjustInvForCycleCount(YFCElement itemEle) {
    YFCDocument adjDoc= YFCDocument.createDocument(XMLLiterals.ITEMS);
    YFCElement adjEle = adjDoc.getDocumentElement();
    insertOrUpdateSyncCtrlTS(itemEle);
    itemEle.setAttribute(XMLLiterals.QUANTITY, itemEle.getDoubleAttribute(XMLLiterals.QUANTITY)
        + calculateAbsoluteQuantity(itemEle));
    itemEle.setAttribute(XMLLiterals.ADJUSTMENT_TYPE, XMLLiterals.ABSOLUTE);
    itemEle.setAttribute(XMLLiterals.REMOVE_INV_NODE_CTRL, FLAG_YES);
    adjEle.importNode(itemEle);
    invokeYantraApi(XMLLiterals.ADJUST_INVENTORY_API, adjDoc);
  }
}