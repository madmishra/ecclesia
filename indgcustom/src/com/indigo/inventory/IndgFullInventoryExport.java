package com.indigo.inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * This method writes to the file for export of
 * inventory Dump
 * 
 * @author BSG109
 *
 */
public class IndgFullInventoryExport extends AbstractCustomApi {

  private static final String RTAM_FULL_UPLOAD_FILE_LOCATION = "RTAM_FULL_UPLOAD_FILE_LOCATION";
  private static OutputStream fileOutputStream = null;
  int inputMessageCount = 0;
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
      try {
        setFileOutputStream();
        YFCElement availabilityChanges = inXml.getDocumentElement();
        YFCIterable<YFCElement> yfcItrator = availabilityChanges.getChildren(XMLLiterals.AVAILABILITY_CHANGE);
        for(YFCElement availabilityChange:yfcItrator) {
          String sInventoryUpload = " "+availabilityChange.toString();
          if (sInventoryUpload.startsWith("<?xml ")) {
            sInventoryUpload = sInventoryUpload.substring(sInventoryUpload.indexOf("?>") + 2);
            fileOutputStream.write(sInventoryUpload.getBytes());
          }
        }
        if(inputMessageCount == 25) 
          fileOutputStream.flush();
        inputMessageCount++;
      } catch (Exception exp) {
        throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_RTAM_UPLOAD, exp);
      }
    return inXml;
  }
  
  /**
   * This method opens the steam in case of Stream if null
   * 
   * @return
   * @throws FileNotFoundException
   */
  private void setFileOutputStream() throws FileNotFoundException {
    if(null == fileOutputStream) {
      fileOutputStream = new FileOutputStream(new File(getProperty(RTAM_FULL_UPLOAD_FILE_LOCATION)),true);
    }
  }
}