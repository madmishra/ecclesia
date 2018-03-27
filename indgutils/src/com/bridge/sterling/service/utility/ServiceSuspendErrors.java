package com.bridge.sterling.service.utility;

import java.util.Map;

import com.bridge.sterling.consts.CommonLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.LoggerUtil;
import com.bridge.sterling.utils.StringUtil;
import com.bridge.sterling.utils.SuspendErrorUtil;
import com.bridge.sterling.utils.XMLUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSException;


public class ServiceSuspendErrors extends AbstractCustomApi {
  private static YFCLogCategory logger = YFCLogCategory.instance(ServiceSuspendErrors.class);

  public YFCDocument invoke(YFCDocument inXML) throws YFSException {
    LoggerUtil.startComponentLog(logger, this.getClass().getName(), "invoke", inXML);

    YFCDocument outXML = YFCDocument.createDocument("ErrorCodes");
    YFCElement eleErrorCodes = outXML.getDocumentElement();

    int numOfRcrds = 0;

    for (Map.Entry<Object, Object> me : getProperties().entrySet()) {
      String sKey = StringUtil.getStringValue(me.getKey());
      String sValue = StringUtil.getStringValue(me.getValue());

      if (!SCUtil.isVoid(sKey) && !SCUtil.isVoid(sValue) && SuspendErrorUtil.isValidMatchType(sKey)) {
        YFCElement eleErrorCode = outXML.createElement("ErrorCode");
        eleErrorCode.setAttribute(CommonLiterals.MATCH_TYPE, sKey);
        eleErrorCode.setAttribute(CommonLiterals.MATCHER, sValue);
        eleErrorCodes.appendChild(eleErrorCode);
      }

      numOfRcrds++;
    }
    eleErrorCodes.setAttribute(CommonLiterals.RECORDCOUNT, String.valueOf(numOfRcrds));
    loadSuspendErrorMap(inXML, outXML);
    LoggerUtil.endComponentLog(logger, this.getClass().getName(), "invoke", outXML);

    return outXML;
  }

  private void loadSuspendErrorMap(YFCDocument inXML, YFCDocument errorDoc) {
    YFCElement eleRoot = XMLUtil.getRootElement(inXML);
    String loadErrors = eleRoot.getAttribute("LoadSuspendErrors");
    if (!SCUtil.isVoid(loadErrors) && loadErrors.trim().equalsIgnoreCase(CommonLiterals.YES)) {
      SuspendErrorUtil.loadSuspendErrorMap(errorDoc);
    }
  }
}
