package com.indigo.inventory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
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
  private static final String SCRIPT_PATH = "SCRIPT_PATH";
  private static final String SOF = "SOF";
  private static final String INDG_CUSTOM_INVENTORY_MISMATCH = "IndgCustomInventoryMismatch";
  
  /**
   * 
   * This is the stating point for the custom API
   * @throws YFSException 
   * 
   */
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    String rootEleName = inXml.getDocumentElement().getNodeName();
    if(SOF.equals(rootEleName)) {
      manageDeltaServer();
      return inXml;
    }
	inXml.getDocumentElement().getChildElement("ShipNode")
	.setAttribute("CompareAllInventoryLotsFlag","N");
    getDocumentWithDates(inXml);
    return inXml;
  }
  
  /**
   * This method calls getInventoryMismatch API to get the
   * mismatch between the Input FEED and OMS inventory
   * 
   * @param inXml
   */
  private void getInventoryMisMatch(YFCDocument inXml,Map<String,YFCElement> mp){
	System.out.println("*******************Mis Match Started****************");
    //YFCDocument misMatchDoc = invokeYantraApi(XMLLiterals.GET_INVENTORY_MISMATCH, inXml);
	YFCDocument misMatchDoc = invokeYantraService(INDG_CUSTOM_INVENTORY_MISMATCH, inXml);
	System.out.println("*******************Mis Match Completed****************"+inXml);
    YFCElement misMatchEle = misMatchDoc.getDocumentElement();
    YFCIterable<YFCElement> yfsItratorMis = misMatchEle.getChildren(XMLLiterals.ITEM);
    for(YFCElement itemEle:yfsItratorMis) {
      String itemIdMis = itemEle.getAttribute(XMLLiterals.ITEM_ID);
      mp.put(itemIdMis, itemEle);
    }
  }
  
  /**
   * 
   * This method splits mismatch output document to 100 per document
   * and calls InventoryFullSyncQ for adjust inventory.
   * 
   * @param inXml
   * @param mismatchDoc
   */
  private void pushInputForAdjustInventory(YFCIterable<YFCElement> yfsItrator,String shipNode,
      Map<String,YFCElement> mp) {
    int maxItemElementCount = Integer.parseInt(getProperty(MAX_ITEM_ELEMENT_COUNT));
    int itratorCount = INITAL_ITRATOR_COUNT;
    YFCDocument adjustInventoryDoc = YFCDocument.createDocument(XMLLiterals.ITEMS);
    setInvSyncStatusKey(adjustInventoryDoc,shipNode);
    for(YFCElement itemEle:yfsItrator) {
      YFCElement misMatchEle = addTransactionDates(itemEle,mp,shipNode);
      if(itratorCount == maxItemElementCount) {
        invokeYantraService(FULL_SYNC_QUEUE_FLOW, adjustInventoryDoc);
        adjustInventoryDoc = YFCDocument.createDocument(XMLLiterals.ITEMS);
        setInvSyncStatusKey(adjustInventoryDoc,shipNode);
        itratorCount = INITAL_ITRATOR_COUNT;
      }
      itemEle.setAttribute(XMLLiterals.ADJUSTMENT_TYPE, XMLLiterals.ADJUSTMENT);
      adjustInventoryDoc.getDocumentElement().importNode(misMatchEle);
      itratorCount++;
    }
    invokeYantraService(FULL_SYNC_QUEUE_FLOW, adjustInventoryDoc);
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
  private void getDocumentWithDates(YFCDocument inXml){
    Map<String,YFCElement> mp = new HashMap<>();
    getInventoryMisMatch(inXml,mp);
    YFCElement shipNodeEle = inXml.getDocumentElement().getChildElement(XMLLiterals.SHIPNODE);
    String shipNode = shipNodeEle.getAttribute(XMLLiterals.SHIPNODE);
    YFCIterable<YFCElement> yfsItrator = shipNodeEle.getChildren(XMLLiterals.ITEM);
    pushInputForAdjustInventory(yfsItrator,shipNode,mp);
    mp.clear();
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
   * This method adds Transaction and Generation date for each
   * item Element 
   * 
   */
  private YFCElement addTransactionDates(YFCElement itemEle, Map<String,YFCElement> mp,String shipNode){
    String itemID = itemEle.getAttribute(XMLLiterals.ITEM_ID);
    String generationDate = itemEle.getAttribute(XMLLiterals.GENERATION_DATE,EMPTY_STRING);
    String transactionDate = itemEle.getAttribute(XMLLiterals.TRANSACTION_DATE,EMPTY_STRING);
    YFCElement misMatchItemEle = mp.get(itemID);
    if(XmlUtils.isVoid(misMatchItemEle)) {
      YFCElement tempEle = YFCDocument.createDocument(XMLLiterals.ITEM).getDocumentElement();
      tempEle.setAttribute(XMLLiterals.ITEM_ID,itemID);
      tempEle.setAttribute(XMLLiterals.GENERATION_DATE,generationDate);
      tempEle.setAttribute(XMLLiterals.TRANSACTION_DATE,generationDate);
      tempEle.setAttribute(XMLLiterals.SHIPNODE, shipNode);
      return tempEle;
    } else {
      misMatchItemEle.setAttribute(XMLLiterals.GENERATION_DATE,generationDate);
      misMatchItemEle.setAttribute(XMLLiterals.TRANSACTION_DATE,transactionDate);
      misMatchItemEle.setAttribute(XMLLiterals.ADJUSTMENT_TYPE, XMLLiterals.ADJUSTMENT);
      misMatchItemEle.setAttribute(XMLLiterals.SHIPNODE, shipNode);
      return misMatchItemEle;
    }
  }
  
  
  
  /**
   * 
   * This method starts or stops the server based on the
   * SOF/EOF file
   * @param inXml
   */
  private void manageDeltaServer() {
      try{
        String shellScript = getProperty(SCRIPT_PATH);
        Runtime.getRuntime().exec(shellScript);
        truncateInventoryLogTable();
        } catch (Exception exp) {
          throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_SQL_EXP, exp);
        }
  }
}
