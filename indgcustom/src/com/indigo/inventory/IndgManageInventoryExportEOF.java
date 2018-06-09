package com.indigo.inventory;

import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * 
 * This service is invoked on EOF of RTAM FULL Sync
 * 
 * @author BSG109
 *
 */
public class IndgManageInventoryExportEOF extends AbstractCustomApi{
  
  private static final String SCRIPT_PATH = "SCRIPT_PATH";
  private static OutputStream fileOutputStream ;
  private static GZIPOutputStream gzipOS ;
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    String myShellScript = getProperty(SCRIPT_PATH);
    try{
    if(null != fileOutputStream) {
      gzipOS.close();
      fileOutputStream.flush();
      fileOutputStream.close();
      }
	  System.out.println("EOF Done");
    fileOutputStream = null;
    gzipOS = null;
    Runtime.getRuntime().exec(myShellScript);
    } 
    catch(Exception exp) {
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_RTAM_UPLOAD, exp);
    }
    return inXml;
  }
}
