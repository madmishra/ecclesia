package com.indigo.inventory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.bridge.sterling.utils.XPathUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSException;

/**
 * 
 * Custom API to get the mismatch and process the input for
 * adjust location inventory.
 * 
 * @author BSG109
 *
 */
public class IndgManageInventoryMismatch extends AbstractCustomApi {

  private static final String FULL_SYNC_QUEUE_FLOW = "IndgFullSyncQ";
  private static final String TRUNCATE_LOG_QUERY = "DELETE FROM INDG_INV_ADJUSTMENT_LOG";
  private static final int INITAL_ITRATOR_COUNT = 1;
  private static final String MAX_ITEM_ELEMENT_COUNT = "MAX_ITEM_ELEMENT_COUNT";
  private static final String EMPTY_STRING = "";
  private static final String INDG = "INDG";
  private static final String FLAG_NO = "N";
  private static final String FLAG_YES = "Y";
  private static final String SCRIPT_PATH = "SCRIPT_PATH";
  
  /**
   * 
   * This is the stating point for the custom API
   * @throws YFSException 
   * 
   */
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    try{
    truncateInventoryLogTable();
    String shellScript = getProperty(SCRIPT_PATH);
    Runtime.getRuntime().exec(shellScript);
    } catch (Exception exp) {
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_SQL_EXP, exp);
    }
    setDefaultComponents(inXml);
    YFCDocument mismatchDoc = getInventoryMisMatch(inXml);
    String shipNode = inXml.getDocumentElement().getChildElement(XMLLiterals.SHIPNODE)
        .getAttribute(XMLLiterals.SHIPNODE);
    getDocumentWithDates(inXml,mismatchDoc);
    pushInputForAdjustInventory(mismatchDoc,shipNode);
    return inXml;
  }
  
  /**
   * This method calls getInventoryMismatch API to get the
   * mismatch between the Input FEED and OMS inventory
   * 
   * @param inXml
   */
  private YFCDocument getInventoryMisMatch(YFCDocument inXml){
    return invokeYantraApi(XMLLiterals.GET_INVENTORY_MISMATCH, inXml);
  }
  
  /**
   * 
   * This method splits mismatch output document to 100 per document
   * and calls InventoryFullSyncQ for adjust inventory.
   * 
   * @param inXml
   * @param mismatchDoc
   */
  private void pushInputForAdjustInventory(YFCDocument mismatchDoc,String shipNode) {
    int maxItemElementCount = Integer.parseInt(getProperty(MAX_ITEM_ELEMENT_COUNT));
    YFCDocument adjustInventoryDoc = YFCDocument.createDocument(XMLLiterals.ITEMS);
    String manageInventoryService = FULL_SYNC_QUEUE_FLOW;
    YFCIterable<YFCElement> yfcItrator = mismatchDoc.getDocumentElement()
        .getChildren(XMLLiterals.ITEM);
    int itratorCount = INITAL_ITRATOR_COUNT;
    setInvSyncStatusKey(adjustInventoryDoc,shipNode);
    for(YFCElement itemEle : yfcItrator) {
      if(itratorCount == maxItemElementCount) {
        invokeYantraService(FULL_SYNC_QUEUE_FLOW, adjustInventoryDoc);
        adjustInventoryDoc = YFCDocument.createDocument(XMLLiterals.ITEMS);
        setInvSyncStatusKey(adjustInventoryDoc,shipNode);
        itratorCount = INITAL_ITRATOR_COUNT;
      }
      itemEle.setAttribute(XMLLiterals.ADJUSTMENT_TYPE, XMLLiterals.ADJUSTMENT);
      adjustInventoryDoc.getDocumentElement().importNode(itemEle);
      itratorCount++;
    }
    invokeYantraService(manageInventoryService, adjustInventoryDoc);
  }
  
  /**
   * This method truncate the data from the 
   * INDG_INVENTORY_LOG table to start Full sync and mismatch process
   * @throws SQLException 
   * 
   */
  private void truncateInventoryLogTable() throws SQLException {
      Statement stmt = null;
      try{
          Connection conn = getDBConnection();
          stmt = conn.createStatement();
          stmt.execute(TRUNCATE_LOG_QUERY);
      }  catch(Exception exp) {
        throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_SQL_EXP, exp);
      } finally {
        if(stmt!=null) {
          stmt.close();
        }
      }
  }
  
  /**
   * This method forms input for Inventory Adjustment 
   * 
   * @param inXml
   * @param misMatchDoc
   */
  private void getDocumentWithDates(YFCDocument inXml, YFCDocument misMatchDoc){
    YFCElement shipNodeEle = inXml.getDocumentElement().getChildElement(XMLLiterals.SHIPNODE);
    String shipNode = shipNodeEle.getAttribute(XMLLiterals.SHIPNODE);
    YFCIterable<YFCElement> yfsItrator = shipNodeEle.getChildren(XMLLiterals.ITEM);
    for(YFCElement itemEle:yfsItrator) {
      String itemID = itemEle.getAttribute(XMLLiterals.ITEM_ID);
      String generationDate = itemEle.getAttribute(XMLLiterals.GENERATION_DATE,EMPTY_STRING);
      String transactionDate = itemEle.getAttribute(XMLLiterals.TRANSACTION_DATE,EMPTY_STRING);
      YFCElement misMatchItemEle = XPathUtil.getXPathElement(misMatchDoc, "/Items/Item[@ItemID=\""+itemID+"\"]");
      if(XmlUtils.isVoid(misMatchItemEle)) {
        YFCElement tempEle = misMatchDoc.getDocumentElement().createChild(XMLLiterals.ITEM);
        tempEle.setAttribute(XMLLiterals.ITEM_ID,itemID);
        tempEle.setAttribute(XMLLiterals.GENERATION_DATE,generationDate);
        tempEle.setAttribute(XMLLiterals.TRANSACTION_DATE,generationDate);
        tempEle.setAttribute(XMLLiterals.SHIPNODE, shipNode);
      } else {
        misMatchItemEle.setAttribute(XMLLiterals.GENERATION_DATE,generationDate);
        misMatchItemEle.setAttribute(XMLLiterals.TRANSACTION_DATE,transactionDate);
        misMatchItemEle.setAttribute(XMLLiterals.ADJUSTMENT_TYPE, XMLLiterals.ADJUSTMENT);
        misMatchItemEle.setAttribute(XMLLiterals.SHIPNODE, shipNode);
      }
    }
  }
  
  
  /**
   * This method inserts record into Status manage table to
   * control End of File.
   * 
   * @param shipNode
   */
  private void addRecordToManageStatusTable(String invSyncStatusKey,String shipNode){
    YFCDocument inXml = YFCDocument.createDocument(XMLLiterals.INDG_FULL_SYNC_STATUS);
    YFCElement inEle = inXml.getDocumentElement();
    inEle.setAttribute(XMLLiterals.INDG_FULL_SYNC_STATUS_KEY,invSyncStatusKey);
    inEle.setAttribute(XMLLiterals.SHIPNODE, shipNode);
    invokeYantraService(XMLLiterals.INDG_FULL_SYNC_STATUS_CREATE, inXml);
  }
  
  /**
   * 
   * This method gets and sets Sync Status Key
   * @param adjustInventoryDoc
   */
  private void setInvSyncStatusKey(YFCDocument adjustInventoryDoc,String shipNode) {
    Date dateKey = new Date();
    String invSyncStatusKey =INDG+dateKey.getTime();
    adjustInventoryDoc.getDocumentElement().setAttribute(XMLLiterals.INDG_FULL_SYNC_STATUS_KEY,
        invSyncStatusKey);
    addRecordToManageStatusTable(invSyncStatusKey,shipNode);
  }
  
  /**
   * This method set the default parameters for
   * Adjust Inventory
   * 
   * @param inXml
   */
  private void setDefaultComponents(YFCDocument inXml) {
    YFCElement inEle = inXml.getDocumentElement();
    inEle.setAttribute(XMLLiterals.APPLY_DIFFERENCE, FLAG_NO);
    YFCElement shipNodeEle = inEle.getChildElement(XMLLiterals.SHIPNODE);
    shipNodeEle.setAttribute(XMLLiterals.COMPLETE_INVENTORY_FLAG, FLAG_YES);
    shipNodeEle.setAttribute(XMLLiterals.VALIDATE_ITEMS, FLAG_YES);
  }
}
