package com.indigo.inventory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

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
  private static final String MAX_MESSAGE_COUNT = "MAX_MESSAGE_COUNT";
  private static FileOutputStream fileOutputStream = null;
  
  private static int inputMessageCount = 0;
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    
      try {
        setFileOutputStream();
        GZIPOutputStream gzipOS = new GZIPOutputStream(fileOutputStream,true);
        YFCElement availabilityChanges = inXml.getDocumentElement();
        YFCIterable<YFCElement> yfcItrator = availabilityChanges.getChildren(XMLLiterals.AVAILABILITY_CHANGE);
        for(YFCElement availabilityChange:yfcItrator) {
          availabilityChange = IndgDeltaInventoryExport. dateFormatChangeForInv(availabilityChange);
          String sInventoryUpload = availabilityChange.toString();
          if (sInventoryUpload.startsWith("<?xml ")) {
            sInventoryUpload = sInventoryUpload.substring(sInventoryUpload.indexOf("?>") + 2);
            System.out.println(sInventoryUpload);
          }
          gzipOS.write(sInventoryUpload.getBytes());
          int maxMessageCount = Integer.parseInt(getProperty(MAX_MESSAGE_COUNT));
            if(inputMessageCount == maxMessageCount) {
                gzipOS.close();
                fileOutputStream.close();
                fileOutputStream=null;
                gzipOS= null;
                setFileOutputStream();
                inputMessageCount = 0;
          }
        }
        gzipOS.close();
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
   * @throws IOException 
   */
  private void setFileOutputStream() throws IOException {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
    format.format(new Date());
    if(null == fileOutputStream) {
      fileOutputStream = new FileOutputStream(new File(getProperty(RTAM_FULL_UPLOAD_FILE_LOCATION)
          +""+format.format(new Date())+"_RTAM.xml.gz"),true);
    }
  }
}