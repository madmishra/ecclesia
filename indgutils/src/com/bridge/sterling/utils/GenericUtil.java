package com.bridge.sterling.utils;

import org.w3c.dom.Document;

import com.bridge.sterling.consts.CommonLiterals;
import com.bridge.sterling.consts.ExceptionLiterals;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;

public class GenericUtil {

  /**
   * Validates typical service inputs (Document, env) and throws error if any is null
   * 
   * @param YFSEnvironment oEnv
   * @param DOcument inXML
   * @return boolean true if neither is null
   */
  public static boolean validateServiceInput(YFSEnvironment env, Document inXml) {
    if (SCUtil.isVoid(env)) {
      // ERR9900001: Sterling Service Environment Null
      throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_SERVICE_ENV_NULL);
    }
    if (SCUtil.isVoid(inXml)) {
      // ERR9900017: Input XML is Null
      throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_XML_NULL);
    }
    return true;
  }

  /**
   * Validates service inputs (YFCDocument, env) and throws error if any is null
   * 
   * @param YFSEnvironment oEnv
   * @param YFCDOcument inXML
   * @return boolean true if neither is null
   */
  public static boolean validateServiceInput(YFSEnvironment env, YFCDocument inXml) {
    return validateServiceInput(env, XMLUtil.getDocument(inXml));
  }

  /**
   * Parses a string to a double. In case string is not corresponding to a double value, return 0.0
   * 
   * @param String sDouble
   * @return Double the double value of input string
   */
  public static Double parseDouble(String sDouble) {
    Double dbl = 0.0;
    if (!SCUtil.isVoid(sDouble)) {
      try {
        dbl = Double.parseDouble(sDouble.trim());
      } catch (NumberFormatException ex) {
        // Ignore error
      }
    }
    return dbl;
  }

  public static YFCElement getFiltersNode(YFCDocument inDoc) {
    YFCElement eleFilters = null;

    if (SCUtil.isVoid(inDoc)) {
      // ERR9900017: Input XML is Null
      throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_XML_NULL);
    }
    YFCElement eleRoot = inDoc.getDocumentElement();

    if (!SCUtil.isVoid(eleRoot)) {
      eleFilters = eleRoot.getChildElement(CommonLiterals.TRNSACTION_FILTERS);
    }

    if (SCUtil.isVoid(eleFilters)) {
      // ERR0000161: Invalid Input XML
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_INPUT_XML);
    }
    return eleFilters;
  }

  public static String getEntitynameFromTaskQDocument(String dataType) {
    /*
     * gets Entity name from the Task Q XML for eg.: in case of wave, DataType will be WaveKey. So
     * method will return Wave. Similarly for Order it will be Order
     */
    String entityName = "";
    if (!SCUtil.isVoid(dataType)) {
      entityName = dataType.replaceFirst("Key$", "");
      if (!SCUtil.isVoid(entityName) && entityName.contains("Header")) {
        entityName = entityName.replaceFirst("Header$", "");
      }
    }
    return entityName.trim();
  }

  public static YFCDocument prepareExecuteXML(YFCDocument inDoc) {
    /*
     * converts the XML to context spcific. for eg.: in case of wave it will be <Wave WaveKey><!--
     * original inXML--></Wave>. This way the apis can be created for WaveKey and can be called
     * outside taskQ too
     */

    YFCDocument outDoc = null;

    YFCElement eleRootIn = inDoc.getDocumentElement();
    String dataType = eleRootIn.getAttribute("DataType");
    String dataKey = eleRootIn.getAttribute("DataKey");

    if (!SCUtil.isVoid(dataType)) {
      String outRootName = getEntitynameFromTaskQDocument(dataType);
      outDoc = YFCDocument.createDocument(outRootName);
      YFCElement eleRootOut = outDoc.getDocumentElement();
      eleRootOut.setAttribute(dataType, dataKey);
      eleRootOut.importNode(eleRootIn);
    } else {
      outDoc = inDoc;
    }
    return outDoc;
  }
}
