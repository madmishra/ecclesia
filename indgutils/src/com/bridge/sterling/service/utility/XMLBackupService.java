package com.bridge.sterling.service.utility;

import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.LoggerUtil;
import com.bridge.sterling.utils.XPathUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;

/**
 * This service can be used to backup the XML in a flow. This service will wrap the XML in <Backup>
 * node and will invoke an another service that is configured as backup service. Additionally this
 * service can add some xpath based reference data to XML being backed up. Sample XML is as follows
 * <Backup OrigServiceName="" > <OrigXML> </OrigXML> <BackupReferenceList> <BackupReference
 * ReferenceType="" ReferenceValue=""/> </BackupReferenceList> <Backup> (Refernces to be configured on
 * the service properties as ReferenceType_1 = "OrderNO" ReferenceValue_1="//Order/@OrderNo", etc)
 */

public class XMLBackupService extends AbstractCustomApi {

  private static YFCLogCategory logger = YFCLogCategory.instance(XMLBackupService.class);
  private static final String IS_ACTIVE = "IsActive";
  private static final String REF_TYPE_PARAM = "ReferenceType";
  private static final String REF_VAL_PARAM = "ReferenceValue";
  private static final String ORIGINAL_SERVICE_NAME = "OrigServiceName";
  private static final String BACKUP_SERVICE = "BackupService";

  public YFCDocument invoke(YFCDocument inXML) {
    if ("Y".equalsIgnoreCase(getProperty(IS_ACTIVE, "N"))) {
      String backupService = getProperty(BACKUP_SERVICE, true);
      YFCDocument backupOutDoc = invokeYantraService(backupService, prepareBackupDoc(inXML));
      LoggerUtil.verboseLog("Backup outDoc is", logger, backupOutDoc);
    } else {
      LoggerUtil.verboseLog("Backup service is not called because service is inactive", logger, "");
    }

    return inXML;
  }

  private YFCDocument prepareBackupDoc(YFCDocument inXML) {
    YFCDocument backupInDoc = YFCDocument.createDocument("Backup");
    YFCElement eleBackup = backupInDoc.getDocumentElement();
    eleBackup.setAttribute(ORIGINAL_SERVICE_NAME, getProperty(ORIGINAL_SERVICE_NAME));
    addOrigXML(eleBackup, inXML);
    addBackupReferences(eleBackup, inXML);
    LoggerUtil.verboseLog("Backup inDoc is", logger, backupInDoc);
    return backupInDoc;
  }

  private void addOrigXML(YFCElement eleBackup, YFCDocument inXML) {
    YFCElement eleOrigXML = eleBackup.createChild("OrigXML");
    eleOrigXML.importNode(inXML.getDocumentElement().cloneNode(true));
  }

  private void addBackupReferences(YFCElement eleBackup, YFCDocument inXML) {
    /*
     * Properties will be in pairs ReferenceType_1: "OrderNo", ReferenceValue_1:
     * "xml://Order/@OrderNo"
     */
    YFCElement eleBackupReferenceList = eleBackup.createChild("BackupReferenceList");
    for (Object oKey : getProperties().keySet()) {
      String sKey = String.valueOf(oKey);
      if (isReferenceType(sKey)) {
        YFCElement eleBackupReference = eleBackupReferenceList.createChild("BackupReference");
        eleBackupReference.setAttribute("ReferenceType", getReferenceTypeFor(sKey));
        eleBackupReference.setAttribute("ReferenceValue", getReferenceValueFor(sKey, inXML));
      }
    }
  }

  private boolean isReferenceType(String sKey) {
    return sKey.matches("^" + REF_TYPE_PARAM + ".*$");
  }

  private String getReferenceTypeFor(String sKey) {
    return getProperty(sKey);
  }

  private String getReferenceValueFor(String sKey, YFCDocument inXML) {
    String refValueKey = getReferenceValueKey(sKey);
    String refValue = getProperty(refValueKey);
    LoggerUtil.verboseLog("Reference Value for " + refValueKey + ": ", logger, refValue);
    if (XPathUtil.isValidXPath(refValue)) {
      LoggerUtil.verboseLog("Reference Value is xpath, getting xpath value", logger, refValue);
      refValue = XPathUtil.getXpathAttributeWithDefaultValue(inXML, refValue, "");
    }
    return refValue;
  }

  private String getReferenceValueKey(String sKey) {
    // sKey will be like ReferenceType_n. ReferenceValue key will be ReferenceValue_n
    return REF_VAL_PARAM + sKey.replaceFirst("^" + REF_TYPE_PARAM, "");
  }



}
