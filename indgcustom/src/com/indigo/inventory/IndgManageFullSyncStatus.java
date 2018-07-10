package com.indigo.inventory;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * Custom service to keep track of EOF for Full
 * Sync.
 * 
 * @author BSG109
 *
 */
public class IndgManageFullSyncStatus extends AbstractCustomApi {

  private static final String EMPTY_STRING = "";
  private static final String INDG_FULL_SYNC_STATUS_LIST_FLOW= "IndgGetSyncStatusList";
  private static final String INITIAL_SLEEP_TIME = "InitialSleepTime";
  private static final String DELTA_SLEEP_TIME = "DeltaSleepTime";
  private static final String SCRIPT_PATH = "SCRIPT_PATH";
  private static final String RTAM_CRITERIA_ID = "RTAM_CRITERIA_ID";
  private static final String TRANSACTION_ID = "REALTIME_ATP_MONITOR";

/**
 * This method is the invoke point of the class. This method
 * checks for the Full Sync Status record. If the record exist
 * it will sleep specified time and rechecks until all the
 * records are removed. Once no record on the list method will
 * trigger RTAM FULL SYNC agent
 * 
 */
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    int initialSleepTime = Integer.parseInt(getProperty(INITIAL_SLEEP_TIME));
    try {
      Thread.sleep(initialSleepTime);
      sleepTillEOF();
    } catch (Exception exp) {
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_SYNC_EXP, exp);
    }
   return inXml;
  }
  
  /**
   * This method gets the List of Full Sync Status Table records
   * 
   * @return
   */
  private YFCDocument isFullSyncCompleted() {
    YFCDocument inXml = YFCDocument.createDocument(XMLLiterals.INDG_FULL_SYNC_STATUS);
    YFCDocument tempXml = YFCDocument.createDocument(XMLLiterals.INDG_FULL_SYNC_STATUS_LIST);
    tempXml.getDocumentElement().createChild(XMLLiterals.INDG_FULL_SYNC_STATUS).setAttribute(XMLLiterals
        .INDG_FULL_SYNC_STATUS_KEY, EMPTY_STRING);
    return invokeYantraService(INDG_FULL_SYNC_STATUS_LIST_FLOW, inXml);
  }
  
  /**
   * 
   * This is an recursive method to check for
   * EOF inventory Full Sync
   * 
   */
  private void sleepTillEOF() {
    int deltaSleepTime = Integer.parseInt(getProperty(DELTA_SLEEP_TIME));
    String myShellScript = getProperty(SCRIPT_PATH);
    try {
      Thread.sleep(deltaSleepTime);
      YFCDocument fullSyncStatusList = isFullSyncCompleted();
     if(fullSyncStatusList.getDocumentElement().hasChildNodes()){
        sleepTillEOF();
      }
     Runtime.getRuntime().exec(myShellScript);
     triggerFullSync(); 
    } catch (Exception exp) {
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_SYNC_EXP, exp);
    }
  }
  
  /**
   * This method triggers RTAM for FULL SYNC 
   * RTAM 
   * 
   */
  private void triggerFullSync() {
    YFCDocument triggerXml = YFCDocument.createDocument(XMLLiterals.TRIGGER_AGENT);
    triggerXml.getDocumentElement().setAttribute(XMLLiterals.CRITERIA_ID, getProperty(RTAM_CRITERIA_ID));
    triggerXml.getDocumentElement().setAttribute(XMLLiterals.BASE_TRANSACTION_ID, TRANSACTION_ID);
    invokeYantraApi("triggerAgent", triggerXml);
  }
}
