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
 * 
 * 
 * @author BSG109
 *
 */
public class IndgManageInvDeltaSync extends AbstractCustomApi{

  private static final String POS_MOVEMENT = "CC";
  private static final String ADJUSTMENT_MOVEMENT = "Adjustment";
  private static final String INDG_INV_SYNC_CTRL_LIST = "IndgGetInvSyncCtrlList";
  private static final String INDG_INV_ADJ_LOG_CREATE ="IndgCreateInvAdjLog";
  private static final String INDG_INV_ADJ_LOG_LIST ="IndgGetInvAdjLogList";
  private static final String FLAG_YES = "Y";
  
  /**
   * 
   */
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    manageInvFeedBasedOnMovement(inXml);
    return inXml;
  }
  
  /**
   * 
   * 
   */
  private void managePOSInvFeed(YFCDocument inXml){
    YFCElement itemEle = inXml.getDocumentElement().getChildElement(XMLLiterals.ITEM);
    String itemID = itemEle.getAttribute(XMLLiterals.ITEM_ID);
    String shipNode = itemEle.getAttribute(XMLLiterals.SHIPNODE);
    if(canApplyInvAdjustment(getSyncCtrlList(itemID,shipNode),itemEle,XMLLiterals.GENERATION_DATE)) {
      System.out.println(inXml);
      inXml.getDocumentElement().getChildElement(XMLLiterals.ITEM).setAttribute(XMLLiterals.ADJUSTMENT_TYPE,
          XMLLiterals.ADJUSTMENT);
       invokeYantraApi(XMLLiterals.ADJUST_INVENTORY_API, inXml);
       insertIntoAdjusmentLogTable(itemEle);
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
    return invokeYantraService(INDG_INV_SYNC_CTRL_LIST, fullSyncInDoc);
  }
  
  
  /**
   * This method gets Movement type from Feed and 
   * calls different method to manage Inventory based
   * on the movement type
   * 
   * @param inXml
   */
  private void manageInvFeedBasedOnMovement(YFCDocument inXml){
    String movementType = XPathUtil.getXpathAttribute(inXml, "/Items/Item/@ReasonText");
    if(POS_MOVEMENT.equals(movementType) || ADJUSTMENT_MOVEMENT.equals(movementType)) {
      System.out.println("POS");
      managePOSInvFeed(inXml);
    } else {
      System.out.println("COUNT");
      manageCountSAPFeed(inXml);
    }
  }
  
  
  /**
   * 
   * 
   * 
   * @param fullSyncOpDoc
   * @return
   */
  private boolean canApplyInvAdjustment(YFCDocument fullSyncOpDoc,YFCElement itemEle,String dateType) {
    System.out.println(fullSyncOpDoc+"fullSyncOpDoc");
    if(fullSyncOpDoc.getDocumentElement().hasChildNodes()) {
      String generationDateFullSync = XPathUtil.getXpathAttribute(fullSyncOpDoc, 
          "/INDGInvSyncCtrlList/INDGInvSyncCtrl/@"+dateType+"");
      String generationDateItem = itemEle.getAttribute(dateType);
      System.out.println(generationDateFullSync+"____"+generationDateItem);
      if(!XmlUtils.isVoid(generationDateItem) && !XmlUtils.isVoid(generationDateFullSync)) {
        return IndgManageItemFeed.validateTimeDifference(generationDateItem,generationDateFullSync);
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
  private void insertIntoAdjusmentLogTable(YFCElement itemEle){
    YFCDocument inXml = YFCDocument.createDocument(XMLLiterals.INDG_INV_ADJUSTMENT_LOG);
    YFCElement inEle = inXml.getDocumentElement();
    inEle.setAttribute(XMLLiterals.ITEM_ID, itemEle.getAttribute(XMLLiterals.ITEM_ID));
    inEle.setAttribute(XMLLiterals.SHIPNODE, itemEle.getAttribute(XMLLiterals.SHIPNODE));
    inEle.setAttribute(XMLLiterals.QUANTITY, itemEle.getAttribute(XMLLiterals.QUANTITY));
    inEle.setAttribute(XMLLiterals.GENERATION_DATE, itemEle.getAttribute(XMLLiterals.GENERATION_DATE));
    inEle.setAttribute(XMLLiterals.TRANSACTION_DATE, itemEle.getAttribute(XMLLiterals.TRANSACTION_DATE));
    System.out.println(inXml+"insertIntoAdjusmentLogTable");
    invokeYantraService(INDG_INV_ADJ_LOG_CREATE, inXml);
  }
  
  
  /**
   * 
   * 
   * @param inXml
   */
  private void manageCountSAPFeed(YFCDocument inXml) {
    YFCElement inEle = inXml.getDocumentElement();
    YFCIterable<YFCElement> yfsItrator = inEle.getChildren(XMLLiterals.ITEM);
    for(YFCElement itemEle : yfsItrator) {
      String itemID = itemEle.getAttribute(XMLLiterals.ITEM_ID);
      String shipNode = itemEle.getAttribute(XMLLiterals.SHIPNODE);
      if(canApplyInvAdjustment(getSyncCtrlList(itemID,shipNode),itemEle,XMLLiterals.TRANSACTION_DATE)) {
        insertOrUpdateSyncCtrlTS(itemEle);
        itemEle.setAttribute(XMLLiterals.QUANTITY, itemEle.getDoubleAttribute(XMLLiterals.QUANTITY)
            + calculateAbsoluteQuantity(itemEle));
      }
      itemEle.setAttribute(XMLLiterals.ADJUSTMENT_TYPE, XMLLiterals.ABSOLUTE);
      itemEle.setAttribute(XMLLiterals.REMOVE_INV_NODE_CTRL, FLAG_YES);
    }
    System.out.println(inXml);
    invokeYantraApi(XMLLiterals.ADJUST_INVENTORY_API, inXml);
  }
  
  /**
   * 
   * 
   * @param inEle
   */
  private void insertOrUpdateSyncCtrlTS(YFCElement itemEle){
    YFCDocument syncCtrlListOIn = getInputDocForFullSyncCtrl(itemEle);
    YFCElement syncCtrlEle = syncCtrlListOIn.getDocumentElement();
    syncCtrlEle.setAttribute(XMLLiterals.TRANSACTION_DATE, 
        itemEle.getAttribute(XMLLiterals.TRANSACTION_DATE));
    syncCtrlEle.setAttribute(XMLLiterals.GENERATION_DATE, 
        itemEle.getAttribute(XMLLiterals.GENERATION_DATE));
    YFCDocument syncCtrlListOp = getSyncCtrlList(itemEle.getAttribute(XMLLiterals.ITEM_ID)
        ,XMLLiterals.SHIPNODE);
    if(syncCtrlListOp.hasChildNodes()) {
      invokeYantraService(XMLLiterals.UPDATE_FULL_SYNC, syncCtrlListOIn);
    } else {
      invokeYantraService(XMLLiterals.CREATE_FULL_SYNC, syncCtrlListOIn);
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
  private YFCDocument getAdjLogInventoryList(YFCElement itemEle){
    YFCDocument invAdjLogDoc = YFCDocument.createDocument(XMLLiterals.INDG_INV_ADJUSTMENT_LOG);
    YFCElement invAdjLogEle = invAdjLogDoc.getDocumentElement();
    invAdjLogEle.setAttribute(XMLLiterals.ITEM_ID, itemEle.getAttribute(XMLLiterals.ITEM_ID));
    invAdjLogEle.setAttribute(XMLLiterals.SHIPNODE, itemEle.getAttribute(XMLLiterals.SHIPNODE));
    return invokeYantraService(INDG_INV_ADJ_LOG_LIST, invAdjLogDoc);
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
      System.out.println(logTransactionDate+"-----"+itemTransactionDate);
      if(IndgManageItemFeed.validateTimeDifference(logTransactionDate,itemTransactionDate)) {
        totalAdjQty = totalAdjQty + logQty;
      }
    }
    System.out.println(totalAdjQty+"totalAdjQty");
    return totalAdjQty;
  }
}