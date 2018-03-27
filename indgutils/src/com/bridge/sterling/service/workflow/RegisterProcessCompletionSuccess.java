package com.bridge.sterling.service.workflow;

import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.LoggerUtil;
import com.bridge.sterling.utils.XMLUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSException;


public class RegisterProcessCompletionSuccess extends AbstractCustomApi {

  private static YFCLogCategory logger = YFCLogCategory
      .instance(RegisterProcessCompletionSuccess.class);

  @Override
  public YFCDocument invoke(YFCDocument inXml) throws YFSException {
    YFCDocument outXml = null;
    LoggerUtil.startComponentLog(logger, "RegisterProcessCompletionSuccess", "invoke", inXml);
    YFCDocument docRPC = getXMLforRPC(inXml);
    if (!SCUtil.isVoid(docRPC)) {
      outXml = invokeYantraApi("registerProcessCompletion", docRPC);
    }
    LoggerUtil.endComponentLog(logger, "RegisterProcessCompletionSuccess", "invoke", "");
    return outXml;
  }

  private YFCDocument getXMLforRPC(YFCDocument inDoc) {
    YFCDocument docRPC = null;
    String taskQKey = XMLUtil.getRootElement(inDoc).getAttribute("TaskQKey");
    if (!SCUtil.isVoid(taskQKey)) {
      docRPC = YFCDocument.createDocument("RegisterProcessCompletionInput");
      YFCElement eleRPC = docRPC.getDocumentElement();
      eleRPC.setAttribute("KeepTaskOpen", false);
      YFCElement eleCurrTask = eleRPC.createChild("CurrentTask");
      eleCurrTask.setAttribute("TaskQKey", taskQKey);
    }
    return docRPC;
  }

}
