package com.bridge.sterling.utils;

import java.io.InputStream;
import java.util.Properties;

import com.bridge.sterling.utils.LoggerUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.log.YFCLogCategory;

public final class ErrorMessages extends Properties {
  private static ErrorMessages singleton = null;

  /**
   * YFCLogCategory extends org.apache.log4j.Logger
   */
  private static YFCLogCategory mLog = YFCLogCategory.instance(ErrorMessages.class);
  private static final long serialVersionUID = 42L;

  /**
   * Loads the ErrorMessages.properties into the java.util.Properties object.
   * 
   * @param filename String
   */
  private ErrorMessages(String filename) {
    loadPropertyFile(filename);
    String customErrorMessageFile = super.getProperty("Custom_Error_File");
    System.out.println("custom file is " + customErrorMessageFile);
    if (!SCUtil.isVoid(customErrorMessageFile)) {
      loadPropertyFile(customErrorMessageFile);
    }
  }

  private void loadPropertyFile(String filename) {
    try {
      InputStream in = this.getClass().getResourceAsStream(filename);
      this.load(in);
      in.close();
    } catch (Exception e) {
      LoggerUtil.errorDtlLog("Exception while loading the properties from " + filename, mLog, e);
    }
  }

  /**
   * Returns the singleton instance of the ErrorMessages.
   * 
   * @return ErrorMessages
   */
  public static synchronized ErrorMessages getInstance() {
    if (singleton == null) {
      singleton = new ErrorMessages("ErrorMessages.properties");
    }

    return singleton;
  }

  /**
   * @param propName : error code
   * @return String : error message
   */
  public String getProperty(String propName) {
    if (propName != null) {
      return super.getProperty(propName);
    }

    return "";
  }

  /**
   * @param propName : error object
   * @return Object : error Object
   */
  public Object get(Object propName) {
    if (propName != null) {
      return super.get(propName);
    }
    return null;
  }
}
