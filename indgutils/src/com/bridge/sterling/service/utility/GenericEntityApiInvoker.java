package com.bridge.sterling.service.utility;

import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.bridge.sterling.utils.LoggerUtil;
import com.bridge.sterling.utils.XMLUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSException;

// Invoke Entity Api with inXML like <root ApiName=""><Input></Input><Template></Template></root>
// Similar to multi api
public class GenericEntityApiInvoker extends AbstractCustomApi {

  private static YFCLogCategory logger = YFCLogCategory.instance(GenericEntityApiInvoker.class);

  public YFCDocument invoke(YFCDocument inXML) throws YFSException {

    LoggerUtil.startComponentLog(logger, this.getClass().getName(), "invoke", inXML);
    YFCElement rootElem = XMLUtil.getRootElement(inXML);

    String sApiName = rootElem.getAttribute("ApiName");

    YFCDocument dataDoc = null;
    YFCDocument tempDoc = null;

    if (!SCUtil.isVoid(rootElem.getChildElement("Input"))) {
      dataDoc = getDataDocument(rootElem.getChildElement("Input"));
    }

    if (!SCUtil.isVoid(rootElem.getChildElement("Template"))) {
      tempDoc = getDataDocument(rootElem.getChildElement("Template"));
    }

    validateInput(sApiName, dataDoc, tempDoc);

    YFCDocument outXML = invokeEntityApi(sApiName, dataDoc, tempDoc);

    LoggerUtil.endComponentLog(logger, this.getClass().getName(), "invoke", outXML);
    return outXML;
  }

  private void validateInput(String sApiName, YFCDocument dataDoc, YFCDocument tempDoc) {
    if (SCUtil.isVoid(sApiName)) {
      throw ExceptionUtil.getYFSException("YCM0039", "ApiName has not been passed in the XML");
    }
    if (SCUtil.isVoid(dataDoc)) {
      throw ExceptionUtil.getYFSException("YCM0039",
          "Input Child Element has not been passed in the XML");
    }
  }

  private YFCDocument getDataDocument(YFCElement parent) {
    YFCDocument outDoc = null;
    if (!SCUtil.isVoid(parent)) {
      YFCElement elemData = parent.getFirstChildElement();
      if (!SCUtil.isVoid(elemData)) {
        outDoc = XMLUtil.getDocumentFor(elemData);
      }
    }
    return outDoc;
  }

}
