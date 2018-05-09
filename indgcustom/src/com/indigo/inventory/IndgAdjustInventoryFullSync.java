package com.indigo.inventory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * Custom API to updates the full sync information to
 * Custom Inventory Sync table and adjust the inventory according
 * the input from the queue for Full Sync. 
 * 
 * @author BSG109
 *
 */
public class IndgAdjustInventoryFullSync extends AbstractCustomApi{

  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    invokeYantraApi(XMLLiterals.ADJUST_INVENTORY_API, 
        getInputForAdjustInventoryMisMatch(inXml));
    return inXml;
  }
  
  /**
   * This method forms input XML for the inventoryAdjustment from the
   * mismatch output and updates Sync Table
   * 
   * @param inXml
   * @return
   */
  private YFCDocument getInputForAdjustInventoryMisMatch(YFCDocument inXml){
    
    YFCDocument adjustInventoryDoc = YFCDocument.createDocument(XMLLiterals.ITEMS);
    YFCElement inputEle = inXml.getDocumentElement();
    YFCIterable<YFCElement> yfsItator = inputEle.getChildren(XMLLiterals.ITEM);
    String shipNode = inputEle.getChildElement(XMLLiterals.ITEM).getAttribute(XMLLiterals.SHIPNODE);
    for(YFCElement itemEle : yfsItator) {
      if(itemEle.hasAttribute(XMLLiterals.QUANTITY)) {
        itemEle.setAttribute(XMLLiterals.ADJUSTMENT_TYPE, XMLLiterals.ADJUSTMENT);
        adjustInventoryDoc.getDocumentElement().importNode(itemEle);
      }
      try{
        updateInventorySyncTable(itemEle);
      }
      catch(Exception exp) {
        throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_SQL_EXP, exp);
      }
    }
    deleteFullSyncStatusRecord(inputEle.getAttribute(XMLLiterals.INDG_FULL_SYNC_STATUS_KEY),shipNode);
    return adjustInventoryDoc;
  }
  
  /**
   * 
   * 
   * 
   * @param itemEle
   * @throws SQLException 
   */
  public void updateInventorySyncTable(YFCElement itemEle) throws SQLException {
    String itemID = itemEle.getAttribute(XMLLiterals.ITEM_ID);
    String transactionDate = itemEle.getAttribute(XMLLiterals.TRANSACTION_DATE);
    String generationDate = itemEle.getAttribute(XMLLiterals.GENERATION_DATE);
    String shipNode = itemEle.getAttribute(XMLLiterals.SHIPNODE);
    String selectQry = "SELECT * FROM indg_inv_sync_ctrl WHERE "
        + "ITEM_ID = '"+itemID+"' AND SHIP_NODE='"+shipNode+"'";
    Statement stmt = null;
    ResultSet rset = null;
    Connection conn = getDBConnection();
    try{
      stmt = conn.createStatement();
      rset = stmt.executeQuery(selectQry);
        if(!rset.isBeforeFirst()) {
          manageInvSyncTable(itemID,shipNode,transactionDate,generationDate,XMLLiterals.CREATE_FULL_SYNC);
        } else {
          manageInvSyncTable(itemID,shipNode,transactionDate,generationDate,XMLLiterals.UPDATE_FULL_SYNC);
        }
    }  catch(Exception exp) {
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_SQL_EXP, exp);
    } finally {
      if(stmt!=null) {
        stmt.close();
      }
      if(rset!=null) {
        rset.close();
      }
      conn.close();
    }
  }
  
  /**
   * 
   * 
   * @param itemID
   * @param ShipNode
   * @param syncTS
   */
  private void manageInvSyncTable(String itemID, String shipNode,String transactionDate,String generationDate,String serviceName){
    YFCDocument inXml = YFCDocument.createDocument(XMLLiterals.INDG_INV_FULL_SYNC);
    YFCElement inputEle = inXml.getDocumentElement();
    inputEle.setAttribute(XMLLiterals.ITEM_ID, itemID);
    inputEle.setAttribute(XMLLiterals.TRANSACTION_DATE, transactionDate);
    inputEle.setAttribute(XMLLiterals.GENERATION_DATE, generationDate);
    inputEle.setAttribute(XMLLiterals.SHIPNODE, shipNode);
    inputEle.setAttribute(XMLLiterals.SUPPLY_TYPE, XMLLiterals.ON_HAND);
    invokeYantraService(serviceName, inXml);
  }
  
  
  /**
   * This method delete record from Full Sync status table
   * 
   * 
   * @param invSyncEOFKey
   * @param shipNode
   */
  private void deleteFullSyncStatusRecord(String fullSyncStatusKey,String shipNode){
    YFCDocument inXml = YFCDocument.createDocument(XMLLiterals.INDG_FULL_SYNC_STATUS);
    YFCElement inEle = inXml.getDocumentElement();
    inEle.setAttribute(XMLLiterals.INDG_FULL_SYNC_STATUS_KEY,fullSyncStatusKey);
    inEle.setAttribute(XMLLiterals.SHIPNODE,shipNode);
    invokeYantraService(XMLLiterals.INDG_FULL_SYNC_STATUS_DELETE, inXml);
  }
}
