package com.bridge.sterling.agent;

import com.bridge.sterling.consts.CommonLiterals;
import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.framework.agent.AbstractCustomTaskAgentForWave;
import com.bridge.sterling.utils.ExceptionUtil;
import com.bridge.sterling.utils.GenericUtil;
import com.bridge.sterling.utils.LoggerUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;

public class CustomTaskAgentForWave extends AbstractCustomTaskAgentForWave {
  private static YFCLogCategory logger = YFCLogCategory.instance(CustomTaskAgentForWave.class);

  public YFCDocument executeTask(YFCDocument inDoc) {
    LoggerUtil.startComponentLog(logger, this.getClass().getName(), "executeTask", inDoc);

    YFCDocument outDoc = null;

    YFCElement eleFilters = GenericUtil.getFiltersNode(inDoc);
    String sExecuteTaskServiceName = eleFilters.getAttribute(CommonLiterals.EXECUTE_JOB_SERVICE);
    String sPostExecuteJobServiceName = eleFilters.getAttribute(CommonLiterals.POST_EXECUTE_JOB_SERVICE);
    String sPreExecuteJobServiceName = eleFilters.getAttribute(CommonLiterals.PRE_EXECUTE_JOB_SERVICE);

    validateServices(sExecuteTaskServiceName, sPostExecuteJobServiceName, sPreExecuteJobServiceName);

    try {
      if (!SCUtil.isVoid(sPreExecuteJobServiceName)) {
        invokeYantraService(sPreExecuteJobServiceName, inDoc);
      }

      outDoc = invokeYantraService(sExecuteTaskServiceName, GenericUtil.prepareExecuteXML(inDoc));

      if (!SCUtil.isVoid(sPostExecuteJobServiceName)) {
        invokeYantraService(sPostExecuteJobServiceName, inDoc);
      }

    } catch (Exception exp) {
      // ERR9990002: Error in Execute Jobs
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_EXECUTE_JOBS, exp);
    }
    LoggerUtil.endComponentLog(logger, this.getClass().getName(), "executeTask", inDoc);
    return outDoc;
  }

  private void validateServices(String sExecuteTaskServiceName, String sPostExecuteServiceName,
      String sPreExecuteServiceName) {
    logger.debug("Pre Execute service Name is " + sPreExecuteServiceName);
    logger.debug("Execution Service name is " + sExecuteTaskServiceName);
    logger.debug("Post Execute Service Name is " + sPostExecuteServiceName);
    
    if (SCUtil.isVoid(sExecuteTaskServiceName)) {
      // ERR9990003=No Execute Task Service was specified
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_NO_EXECUTE_SERVICE);
    }
  }
}
