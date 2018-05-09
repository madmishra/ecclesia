package com.indigo.inventory;

import java.sql.Connection;
import java.sql.ResultSet;
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

  private static final String FULL_SYNC_QUEUE_FLOW = "FULL_SYNC_QUEUE_FLOW";
  private static final String TRUNCATE_LOG_QUERY = "TRUNCATE TABLE INDG_INV_ADJUSTMENT_LOG";
  private static final int INITAL_ITRATOR_COUNT = 1;
  private static final int MAX_ITEM_ELEMENT_COUNT = 3;
  private static final String EMPTY_STRING = "";
  private static final String INDG = "INDG";
  
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
    } catch (Exception exp) {
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_SQL_EXP, exp);
    }
    YFCDocument mismatchDoc = getInventoryMisMatch(inXml);
    String shipNode = inXml.getDocumentElement().getChildElement(XMLLiterals.SHIPNODE)
        .getAttribute(XMLLiterals.SHIPNODE);
    formInputForAdjust(inXml,mismatchDoc);
    formInputForFullAdjustInventory(mismatchDoc,shipNode);
    return inXml;
  }
  
  /**
   * This method calls getInventoryMismatch API to get the
   * mismatch between the Input FEED and OMS inventory
   * 
   * @param inXml
   */
  private YFCDocument getInventoryMisMatch(YFCDocument inXml){
    return invokeYantraApi("getInventoryMismatch", inXml);
  }
  
  /**
   * 
   * This method splits mismatch output document to 100 per document
   * and calls InventoryFullSyncQ for adjust inventory.
   * 
   * @param inXml
   * @param mismatchDoc
   */
  private void formInputForFullAdjustInventory(YFCDocument mismatchDoc,String shipNode) {
    YFCDocument adjustInventoryDoc = YFCDocument.createDocument(XMLLiterals.ITEMS);
    String manageInventoryService = getProperty(FULL_SYNC_QUEUE_FLOW);
    YFCIterable<YFCElement> yfcItrator = mismatchDoc.getDocumentElement()
        .getChildren(XMLLiterals.ITEM);
    int itratorCount = INITAL_ITRATOR_COUNT;
    Date dateKey = new Date();
    String invSyncStatusKey =INDG+dateKey.getTime();
    adjustInventoryDoc.getDocumentElement().setAttribute(XMLLiterals.INDG_FULL_SYNC_STATUS_KEY,
        invSyncStatusKey);
    addRecordToManageStatusTable(invSyncStatusKey,shipNode);
    for(YFCElement itemEle : yfcItrator) {
      if(itratorCount == MAX_ITEM_ELEMENT_COUNT) {
        dateKey = new Date();
        invSyncStatusKey = INDG+dateKey.getTime();
        addRecordToManageStatusTable(invSyncStatusKey,shipNode);
        invokeYantraService(FULL_SYNC_QUEUE_FLOW, adjustInventoryDoc);
        adjustInventoryDoc = YFCDocument.createDocument(XMLLiterals.ITEMS);
        adjustInventoryDoc.getDocumentElement().setAttribute(XMLLiterals.INDG_FULL_SYNC_STATUS_KEY,
            invSyncStatusKey);
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
      ResultSet rset = null;
      try{
          Connection conn = getDBConnection();
          stmt = conn.createStatement();
          rset = stmt.executeQuery(TRUNCATE_LOG_QUERY);
      }  catch(Exception exp) {
        throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_SQL_EXP, exp);
      } finally {
        if(stmt!=null) {
          stmt.close();
        }
        if(rset!=null) {
          rset.close();
        }
      }
  }
  
  /**
   * This method forms input for Inventory Adjustment 
   * 
   * @param inXml
   * @param misMatchDoc
   */
  private void formInputForAdjust(YFCDocument inXml, YFCDocument misMatchDoc){
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
}
