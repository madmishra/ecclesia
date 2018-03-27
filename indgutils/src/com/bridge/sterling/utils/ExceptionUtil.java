package com.bridge.sterling.utils;

import java.util.Map;
import java.util.Map.Entry;

import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSException;

public class ExceptionUtil {

  // Methods for generating YFSException

  public static YFSException getYFSException(String errorCode) {
    YFCDocument errXml = ExceptionUtil.getYFSExceptionDocument(errorCode);
    return new YFSException(errXml.toString());
  }

  public static YFSException getYFSException(String errorCode, String errMsg) {
    YFCDocument errXml = ExceptionUtil.getYFSExceptionDocument(errorCode, errMsg);
    return new YFSException(errXml.toString());
  }

  public static YFSException getYFSException(String errorCode, String errMsg, String moreInfo) {
    YFCDocument errXml = ExceptionUtil.getYFSExceptionDocument(errorCode, errMsg, moreInfo);
    return new YFSException(errXml.toString());
  }

  public static YFSException getYFSException(String errorCode, Exception exception) {
    YFCDocument errXml = ExceptionUtil.getYFSExceptionDocument(errorCode, exception);
    return new YFSException(errXml.toString());
  }

  public static YFSException getYFSException(String errorCode, String moreInfo, Exception exception) {
    YFCDocument errXml = ExceptionUtil.getYFSExceptionDocument(errorCode, moreInfo, exception);
    return new YFSException(errXml.toString());
  }

  public static YFSException getYFSException(String errorCode, Map<String, Object> attrValMap) {
    YFCDocument errXml = ExceptionUtil.getYFSExceptionDocument(errorCode, attrValMap);
    return new YFSException(errXml.toString());
  }

  public static YFSException getYFSException(String errorCode, Exception exception,
      Map<String, Object> attrValMap) {
    YFCDocument errXml = ExceptionUtil.getYFSExceptionDocument(errorCode, exception, attrValMap);
    return new YFSException(errXml.toString());
  }

  // Methods for generating Document that can be passed as input to YFSException
  public static YFCDocument getYFSExceptionDocument(String errorCode) {
    String errorMsg = ErrorMessages.getInstance().getProperty(errorCode);
    return getYFSExceptionDocument(errorCode, errorMsg, "");
  }

  public static YFCDocument getYFSExceptionDocument(String errorCode, String errMsg) {
    return getYFSExceptionDocument(errorCode, errMsg, "");
  }

  public static YFCDocument getYFSExceptionDocument(String errorCode, String errMsg, String moreInfo) {
    YFCDocument errorDoc = YFCDocument.createDocument("Errors");
    YFCElement eleErrors = errorDoc.getDocumentElement();
    YFCElement eleError = eleErrors.createChild("Error");
    eleError.setAttribute("ErrorCode", errorCode);
    eleError.setAttribute("ErrorDescription", errMsg);
    eleError.setAttribute("ErrorRelatedMoreInfo", moreInfo);
    return errorDoc;
  }

  public static YFCDocument getYFSExceptionDocument(String errorCode, Exception exception) {
    YFCDocument errorDoc = getYFSExceptionDocument(errorCode, "", exception);
    return errorDoc;
  }

  public static YFCDocument getYFSExceptionDocument(String errorCode, String moreInfo,
      Exception exception) {
    String errorMsg = ErrorMessages.getInstance().getProperty(errorCode);
    YFCDocument errorDoc = getYFSExceptionDocument(errorCode, errorMsg, moreInfo);
    addStackToDocument(errorDoc, exception);
    return errorDoc;
  }


  public static YFCDocument getYFSExceptionDocument(String errorCode, Map<String, Object> attrValMap) {
    YFCDocument errorDoc = getYFSExceptionDocument(errorCode);
    addAttributesToDocument(errorDoc, attrValMap);
    return errorDoc;
  }

  public static YFCDocument getYFSExceptionDocument(String errorCode, Exception exception,
      Map<String, Object> attrValMap) {
    YFCDocument errorDoc = getYFSExceptionDocument(errorCode, attrValMap);
    addStackToDocument(errorDoc, exception);
    return errorDoc;
  }

  // private methods

  private static void addStackToDocument(YFCDocument errorDoc, Exception exception) {
    YFCElement eleError = errorDoc.getDocumentElement().getChildElement("Error", true);
    YFCElement eleStack = eleError.createChild("Stack");

    // This method can be used in the java1.4.1.
    String stackTrace = LoggerUtil.getStackTrace(exception, 0);
    eleStack.setNodeValue(stackTrace);
  }

  private static void addAttributesToDocument(YFCDocument errorDoc, Map<String, Object> attrValMap) {
    YFCElement eleError = errorDoc.getDocumentElement().getChildElement("Error", true);
    for (Entry<String, Object> entry : attrValMap.entrySet()) {
      YFCElement eleAttribute = eleError.createChild("Attribute");
      eleAttribute.setAttribute("Name", entry.getKey());
      eleAttribute.setAttribute("Value", entry.getValue().toString());
    }
  }

}
