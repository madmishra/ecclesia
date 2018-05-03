package com.bridge.sterling.service.utility;

import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.LoggerUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSException;

// Invoke Entity Api where Api Name and template is configured on property
public class EntityApiInvoker extends AbstractCustomApi {

  private static YFCLogCategory logger = YFCLogCategory.instance(EntityApiInvoker.class);

  public YFCDocument invoke(YFCDocument inXML) throws YFSException {
    LoggerUtil.startComponentLog(logger, this.getClass().getName(), "invoke", inXML);
    String sApiName = getProperty("ApiName", true).trim();
    String sTemplate = getProperty("Template");

    LoggerUtil.debugLog("Api name is ", logger, sApiName);
    LoggerUtil.debugLog("Tempate is ", logger, sTemplate);
    YFCDocument tempXml = buildTemplateXml(sTemplate);
    LoggerUtil.debugLog("Tempate XML is ", logger, tempXml);

    YFCDocument outXML = invokeEntityApi(sApiName, inXML, tempXml);

    LoggerUtil.endComponentLog(logger, this.getClass().getName(), "invoke", outXML);
    return outXML;
  }

  private YFCDocument buildTemplateXml(String sTemplate) {
    YFCDocument tempXml = null;
    try {
      if (!SCUtil.isVoid(sTemplate)) {
        tempXml = YFCDocument.parse(sTemplate);
      }
    } catch (Exception e) {
      // Eating up the exception
      System.out.println("Exception in parsing template");
      e.printStackTrace();
      LoggerUtil.debugLog("Incorrect template XML value", logger, sTemplate);
      LoggerUtil.debugLog("Exception information: ", logger, e);
    }
    return tempXml;
  }
}
