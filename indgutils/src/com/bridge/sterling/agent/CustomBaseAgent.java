package com.bridge.sterling.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bridge.sterling.consts.CommonLiterals;
import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.framework.agent.AbstractCustomBaseAgent;
import com.bridge.sterling.utils.ExceptionUtil;
import com.bridge.sterling.utils.GenericUtil;
import com.bridge.sterling.utils.LoggerUtil;
import com.bridge.sterling.utils.XMLUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;

public class CustomBaseAgent extends AbstractCustomBaseAgent {
  private static YFCLogCategory logger = YFCLogCategory.instance(CustomBaseAgent.class);

  public List<YFCDocument> getJobs(YFCDocument docInXML, YFCDocument lastMessage) {
    LoggerUtil.startComponentLog(logger, this.getClass().getName(), "getJobs", docInXML);

    List<YFCDocument> jobList = new ArrayList<YFCDocument>();

    YFCElement rootInXml = XMLUtil.getRootElement(docInXML);
    String sGetJobsServiceName = rootInXml.getAttribute(CommonLiterals.GET_JOBS_SERVICE);
    String sIdentifier = rootInXml.getAttribute(CommonLiterals.IDENTIFIER);

    validateGetJobService(sGetJobsServiceName);

    try {
      int iMaxRecords = getMaxRecords(rootInXml);
      Map<String, String> transactionAttrs = rootInXml.getAttributes();
      addLastMessageToInXML(rootInXml, lastMessage);

      YFCDocument docJobs = invokeYantraService(sGetJobsServiceName, docInXML);
      LoggerUtil.verboseLog("Output of GetJobsServiceName", logger, docJobs);

      addDocumentsToJobList(jobList, docJobs, sIdentifier, iMaxRecords, transactionAttrs);
    } catch (Exception exp) {
      // ERR9990001=Error in Get Jobs
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_GET_JOBS, exp);
    }

    LoggerUtil.endComponentLog(logger, this.getClass().getName(), "getJobs", docInXML);
    return jobList;
  }

  public void executeJob(YFCDocument docInXML) {
    YFCElement eleFilters = null;

    String sExecuteJobServiceName = null;
    String sPostExecuteJobServiceName = null;
    String sPreExecuteJobServiceName = null;

    LoggerUtil.startComponentLog(logger, this.getClass().getName(), "executeJob", docInXML);
    try {
      if (SCUtil.isVoid(docInXML) || SCUtil.isVoid(docInXML.getDocumentElement())) {
        throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_INPUT_XML);
      }
      eleFilters = GenericUtil.getFiltersNode(docInXML);

      sExecuteJobServiceName = eleFilters.getAttribute(CommonLiterals.EXECUTE_JOB_SERVICE);
      sPostExecuteJobServiceName = eleFilters.getAttribute(CommonLiterals.POST_EXECUTE_JOB_SERVICE);
      sPreExecuteJobServiceName = eleFilters.getAttribute(CommonLiterals.PRE_EXECUTE_JOB_SERVICE);

      // Should we remove Transaction Filters element?

      validateServices(sExecuteJobServiceName, sPostExecuteJobServiceName,
          sPreExecuteJobServiceName);

      if (!SCUtil.isVoid(sPreExecuteJobServiceName)) {
        docInXML = invokeYantraService(sPreExecuteJobServiceName, docInXML);
      }

      docInXML = invokeYantraService(sExecuteJobServiceName, docInXML);

      if (!SCUtil.isVoid(sPostExecuteJobServiceName)) {
        invokeYantraService(sPostExecuteJobServiceName, docInXML);
      }

    } catch (Exception exp) {
      // ERR9990002=Error in Execute Jobs
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_EXECUTE_JOBS);
    }
    LoggerUtil.endComponentLog(logger, this.getClass().getName(), "executeJob", docInXML);
  }

  private void validateGetJobService(String sGetJobsServiceName) {
    logger.debug("Get Job Service Name" + sGetJobsServiceName);
    if (SCUtil.isVoid(sGetJobsServiceName)) {
      // ERR9990004=No Get Job Service was specified
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_NO_GET_JOB_SERVICE);
    }
  }

  private void addLastMessageToInXML(YFCElement rootInXML, YFCDocument lastMessage) {
    if (!SCUtil.isVoid(rootInXML) && !SCUtil.isVoid(lastMessage)) {
      YFCElement rootLastMessage = lastMessage.getDocumentElement();
      if (!SCUtil.isVoid(rootLastMessage)) {
        rootInXML.createChild(CommonLiterals.LAST_MESSAGE).importNode(rootLastMessage);
      }
    }
  }

  private void addDocumentsToJobList(List<YFCDocument> jobList, YFCDocument docJobs,
      String sIdentifier, int iNum, Map<String, String> transactionAttrs) {
    int count = 0;
    for (YFCElement eleJob : getJobElements(docJobs.getDocumentElement(), sIdentifier)) {
      if (SCUtil.isVoid(eleJob)) {
        continue;
      }
      YFCDocument tempDoc = XMLUtil.getDocumentFor(eleJob);
      YFCElement rootEle = tempDoc.getDocumentElement();
      rootEle.createChild(CommonLiterals.TRNSACTION_FILTERS).setAttributes(transactionAttrs);
      jobList.add(tempDoc);
      count++;
      if (count >= iNum) {
        break;
      }
    }
  }

  private YFCIterable<YFCElement> getJobElements(YFCElement eleJobs, String sIdentifier) {
    return SCUtil.isVoid(sIdentifier) ? eleJobs.getChildren() : eleJobs.getChildren(sIdentifier);
  }

  private int getMaxRecords(YFCElement rootInXml) {
    String sMaxRecords = rootInXml.getAttribute(CommonLiterals.NUM_RECORDS_TO_BUFFER);
    int iMaxRecords = GenericUtil.parseDouble(sMaxRecords).intValue();
    if (iMaxRecords <= 0) {
      iMaxRecords = 5000;
      rootInXml.setIntAttribute(CommonLiterals.NUM_RECORDS_TO_BUFFER, iMaxRecords);
    }
    return iMaxRecords;
  }

  private void validateServices(String sExecuteJobServiceName, String sPostExecuteServiceName,
      String sPreExecuteServiceName) {
    logger.debug("Pre Execute service Name is " + sPreExecuteServiceName);
    logger.debug("Execution Service name is " + sExecuteJobServiceName);
    logger.debug("Post Execute Service Name is " + sPostExecuteServiceName);

    if (SCUtil.isVoid(sExecuteJobServiceName)) {
      // ERR9990003=No Execute Task Service was specified
      throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_NO_EXECUTE_SERVICE);
    }
  }
}
