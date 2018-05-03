package com.bridge.sterling.service.workflow;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.bridge.sterling.utils.GenericUtil;
import com.bridge.sterling.utils.LoggerUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSException;


public class PipelineStatusChangeHelperForWave extends AbstractCustomApi {

  private static YFCLogCategory logger = YFCLogCategory
      .instance(PipelineStatusChangeHelperForWave.class);

  @Override
  public YFCDocument invoke(YFCDocument inXml) throws YFSException {
    YFCDocument outXml = null;
    LoggerUtil.startComponentLog(logger, "PipelineStatusChangeHelperForWave", "invoke", inXml);
    YFCElement eleFilters = GenericUtil.getFiltersNode(inXml);
    String sBaseDropStatus = eleFilters.getAttribute("BaseDropStatus");
    String sStatusChangerTransactionId = eleFilters.getAttribute("StatusChangerTransactionId");
    if (!SCUtil.isVoid(sStatusChangerTransactionId) && !SCUtil.isVoid(sBaseDropStatus)
        && !isWaveCancelled(inXml)) {
      YFCDocument docChangePipelineStatus =
          getXMLforChangePipelineStatus(inXml, sStatusChangerTransactionId, sBaseDropStatus);
      if (!SCUtil.isVoid(docChangePipelineStatus)) {
        outXml =
            invokeYantraApi(
                "changeWaveStatus",
                docChangePipelineStatus);
      }
    }
    String sRegisterProcessCompletionService =
        getProperty("RegisterProcessCompletionSuccessService",
            eleFilters.getAttribute("RegisterProcessCompletionSuccessService", ""));
    if (!SCUtil.isVoid(sRegisterProcessCompletionService)) {
      outXml = invokeYantraService(sRegisterProcessCompletionService, inXml);
    }
    LoggerUtil.endComponentLog(logger, "PipelineStatusChangeHelperForWave", "invoke", "");
    return outXml;
  }

  private boolean isWaveCancelled(YFCDocument inXml) {
    YFCElement eleRootIn = inXml.getDocumentElement();
    String dataType = eleRootIn.getAttribute("DataType", "");
    String dataKey = eleRootIn.getAttribute("DataKey", "");
    String waveKey = "";
    if ("WaveKey".equals(dataType)) {
      waveKey = dataKey;
    }
    if (!SCUtil.isVoid(waveKey)) {
      YFCDocument waveListInp = YFCDocument.getDocumentFor("<Wave WaveKey='" + waveKey + "'/>");
      YFCDocument waveListTemp =
          YFCDocument.getDocumentFor("<WaveList>" + "<Wave WaveKey=''>" + "<Status Status=''/>"
              + "</Wave>" + "</WaveList>");
      YFCDocument waveListOp = invokeYantraApi("getWaveList", waveListInp, waveListTemp);
      String status =
          waveListOp.getDocumentElement().getChildElement("Wave", true)
              .getChildElement("Status", true).getAttribute("Status", "");
      return "9000".equals(status);
    } else {
      // ERR0000161=Invalid Input XML
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_INPUT_XML);
    }
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
