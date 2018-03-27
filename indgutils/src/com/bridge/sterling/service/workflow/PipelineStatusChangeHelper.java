package com.bridge.sterling.service.workflow;

import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.GenericUtil;
import com.bridge.sterling.utils.LoggerUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSException;


public class PipelineStatusChangeHelper extends AbstractCustomApi {

  private static YFCLogCategory logger = YFCLogCategory.instance(PipelineStatusChangeHelper.class);

  @Override
  public YFCDocument invoke(YFCDocument inXml) throws YFSException {
    YFCDocument outXml = null;
    LoggerUtil.startComponentLog(logger, "PipelineStatusChangeHelper", "invoke", inXml);
    YFCElement eleFilters = GenericUtil.getFiltersNode(inXml);
    String sBaseDropStatus = eleFilters.getAttribute("BaseDropStatus");
    String sStatusChangerTransactionId = eleFilters.getAttribute("StatusChangerTransactionId");
    if (!SCUtil.isVoid(sStatusChangerTransactionId) && !SCUtil.isVoid(sBaseDropStatus)) {
      YFCDocument docChangePipelineStatus =
          getXMLforChangePipelineStatus(inXml, sStatusChangerTransactionId, sBaseDropStatus);
      if (!SCUtil.isVoid(docChangePipelineStatus)) {
        outXml =
            invokeYantraApi(
                "change" + GenericUtil.getEntitynameFromTaskQDocument(getDataType(inXml))
                    + "Status", docChangePipelineStatus);
      }
    }
    String sRegisterProcessCompletionService =
        getProperty("RegisterProcessCompletionSuccessService",
            eleFilters.getAttribute("RegisterProcessCompletionSuccessService", ""));
    if (!SCUtil.isVoid(sRegisterProcessCompletionService)) {
      outXml = invokeYantraService(sRegisterProcessCompletionService, inXml);
    }
    LoggerUtil.endComponentLog(logger, "PipelineStatusChangeHelper", "invoke", "");
    return outXml;
  }

  private String getDataType(YFCDocument inXml) {
    YFCElement eleRootIn = inXml.getDocumentElement();
    return eleRootIn.getAttribute("DataType", "");
  }

  private YFCDocument getXMLforChangePipelineStatus(YFCDocument inXml,
      String sStatusChangerTransactionId, String sBaseDropStatus) {
    YFCDocument inpDoc = null;
    inpDoc = GenericUtil.prepareExecuteXML(inXml);
    if (!SCUtil.isVoid(inpDoc)) {
      YFCElement rootElement = inpDoc.getDocumentElement();
      rootElement.setAttribute("BaseDropStatus", sBaseDropStatus);
      rootElement.setAttribute("TransactionId", sStatusChangerTransactionId);
    }
    return inpDoc;
  }
}
