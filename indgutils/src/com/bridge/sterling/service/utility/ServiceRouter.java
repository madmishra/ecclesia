package com.bridge.sterling.service.utility;

import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.LoggerUtil;
import com.bridge.sterling.utils.XPathUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;

/**
 * This class helps to route service calls based on the values of any XPath attribute. Calls service
 * configured as DefaultService if no service is configured for the value of xPath
 * 
 */

public class ServiceRouter extends AbstractCustomApi {

  private static final String XPATH = "XPath";
  private static final String DEFAULT_SERVICE = "DefaultService";

  private static YFCLogCategory logger = YFCLogCategory.instance(ServiceRouter.class);

  public YFCDocument invoke(YFCDocument inXml) {
    LoggerUtil.startComponentLog(logger, this.getClass().getName(), "invoke", inXml);
    YFCDocument outXML = null;

    String xPathValue = getXPathValue(inXml);
    String sServiceName = getServiceName(xPathValue);
    if (!SCUtil.isVoid(sServiceName)) {
      outXML = invokeYantraService(sServiceName, inXml);
    }

    LoggerUtil.endComponentLog(logger, this.getClass().getName(), "invoke", inXml);

    return outXML;
  }

  private String getXPathValue(YFCDocument inXml) {
    String xpathString = getProperty(XPATH, true);
    LoggerUtil.debugLog("ServiceRouter xPath String: " + xpathString, logger, inXml);
    String xpathValue = XPathUtil.getXpathAttribute(inXml, xpathString);
    LoggerUtil.debugLog("ServiceRouter xPath Value: " + xpathValue, logger, inXml);
    return xpathValue;
  }

  private String getServiceName(String xpathValue) {
    String sServiceName = null;

    if (!SCUtil.isVoid(xpathValue)) {
      sServiceName = getServiceFor(xpathValue);
    }
    if (SCUtil.isVoid(sServiceName)) {
      sServiceName = getDefaultServiceName();
    }

    LoggerUtil.debugLog("ServiceRouter: Service to call is " + sServiceName, logger, xpathValue);
    return sServiceName;
  }

  private String getDefaultServiceName() {
    String defaultServiceName;
    defaultServiceName = getProperty(DEFAULT_SERVICE);
    if (!SCUtil.isVoid(defaultServiceName)) {
      defaultServiceName = defaultServiceName.trim();
    }
    return defaultServiceName;
  }


  private String getServiceFor(String xPathValue) {
    LoggerUtil.startComponentLog(logger, this.getClass().getName(), "getServiceFor", xPathValue);
    String sServiceName = getProperty(xPathValue.trim());
    LoggerUtil.endComponentLog(logger, this.getClass().getName(), "getServiceFor", sServiceName);
    return sServiceName.trim();
  }
}
