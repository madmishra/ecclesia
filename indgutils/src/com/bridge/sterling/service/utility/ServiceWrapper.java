package com.bridge.sterling.service.utility;

import com.bridge.sterling.consts.CommonLiterals;
import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.framework.service.ServiceInvoker;
import com.bridge.sterling.framework.service.ServiceInvokerManager;
import com.bridge.sterling.utils.ExceptionUtil;
import com.bridge.sterling.utils.LoggerUtil;
import com.bridge.sterling.utils.SuspendErrorUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.interop.japi.ServiceSuspendException;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class ServiceWrapper extends AbstractCustomApi {

  private static YFCLogCategory logger = YFCLogCategory
      .instance(ServiceWrapper.class);

  /** ServiceName to execute */
  public static final String SERVICE_NAME = "ServiceName";
  public static final String SUSPEND_ON_SERVICE_ERROR = "SuspendOnServiceError";

  /** ON_SUCCESS Service information */
  public static final String ON_SUCCESS_SERVICE_NAME = "OnSuccessServiceName";
  public static final String SUSPEND_ON_SUCCESS_SERVICE_ERROR = "SuspendOnSuccessServiceError";
  public static final String INCLUDE_INPUT_XML_SUCCESS = "IncludeInputXMLForSuccessMessage";

  /** ON_FAILURE Service information */
  public static final String ON_FAILURE_SERVICE_NAME = "OnFailureServiceName";
  public static final String SUSPEND_ON_FAILURE_SERVICE_ERROR = "SuspendOnFailureServiceError";
  public static final String INCLUDE_INPUT_XML_FAILURE = "IncludeInputXMLForFailureMessage";

  public static final String SUSPENDABLE_ERRORS_LOAD_SERVICE = "SuspendableErrorsLookupService";

  private String servicename = null; // Original service name
  private String suspendOnServiceError = null; // Y/N/C -- action on error from original service
  // Y: Always raise a ServiceSuspendException
  // N: Do not raise a ServiceSuspendException
  // C: Raise ServiceSuspendException if error matches suspendable errors

  private String onSuccessServiceName = null; // service name if original service is successful
  private String suspendOnSuccessServiceError = null; // Y/N/C: action if error from success service
  private String includeInXMLForSuccess = null; // Y/N: include orig inXML for success service

  private String onFailureServiceName = null; // service name if original service is failure
  private String suspendOnFailureServiceError = null; // Y/N/C: action if error from failure service
  private String includeInXMLForFailure = null;// Y/N: include orig inXML for failure service

  private String suspendableErrorsLoadService = null; // Service to load suspendable errors


  /**
   * Method: invoke This api will execute the given service and invoke success/failure service
   * depending on the outcome of the original service.
   * 
   * @param inXML : Document
   * 
   * @return Document
   * 
   * @throws java.lang.Exception : YFSException
   */
  public YFCDocument invoke(YFCDocument inXML) throws YFSException {
    LoggerUtil.startComponentLog(logger, this.getClass().getName(), "invoke", inXML);
    YFCDocument outXML = null;
    try {
      setVariables();
      outXML = invokeYantraService(servicename, inXML);
      invokeSuccessService(outXML, inXML);
    } catch (ServiceSuspendException sse) {
      LoggerUtil.errorLog("Caught ServiceSuspendException ", logger, sse);
      throw sse;
    } catch (Exception ex) {
      LoggerUtil.errorLog("Caught Exception - " + ex.getClass().getName(), logger, ex);
      invokeFailureService(inXML, ex);
      // ERR9900010 =Generic Exception
      throw ExceptionUtil.getYFSException("ERR9900010", ex);
    } finally {
      LoggerUtil.endComponentLog(logger, this.getClass().getName(), "invoke", outXML);
    }
    return outXML;
  }

  private void setVariables() {
    servicename = getProperty(SERVICE_NAME, true);
    suspendOnServiceError = getProperty(SUSPEND_ON_SERVICE_ERROR);

    onSuccessServiceName = getProperty(ON_SUCCESS_SERVICE_NAME);
    suspendOnSuccessServiceError = getProperty(SUSPEND_ON_SUCCESS_SERVICE_ERROR);
    includeInXMLForSuccess = getProperty(INCLUDE_INPUT_XML_SUCCESS, CommonLiterals.NO);

    onFailureServiceName = getProperty(ON_FAILURE_SERVICE_NAME);
    suspendOnFailureServiceError = getProperty(SUSPEND_ON_FAILURE_SERVICE_ERROR);
    includeInXMLForFailure = getProperty(INCLUDE_INPUT_XML_FAILURE, CommonLiterals.NO);

    suspendableErrorsLoadService = getProperty(SUSPENDABLE_ERRORS_LOAD_SERVICE);

    if (YFCLogUtil.isVerboseEnabled()) {
      LoggerUtil.verboseLog(SERVICE_NAME, logger, servicename);
      LoggerUtil.verboseLog(ON_SUCCESS_SERVICE_NAME, logger, onSuccessServiceName);
      LoggerUtil.verboseLog(ON_FAILURE_SERVICE_NAME, logger, onFailureServiceName);
      LoggerUtil.verboseLog(SUSPEND_ON_SUCCESS_SERVICE_ERROR, logger, suspendOnSuccessServiceError);
      LoggerUtil.verboseLog(SUSPEND_ON_FAILURE_SERVICE_ERROR, logger, suspendOnFailureServiceError);
      LoggerUtil.verboseLog(INCLUDE_INPUT_XML_SUCCESS, logger, includeInXMLForSuccess);
      LoggerUtil.verboseLog(INCLUDE_INPUT_XML_FAILURE, logger, includeInXMLForFailure);
      LoggerUtil.verboseLog(SUSPENDABLE_ERRORS_LOAD_SERVICE, logger, suspendableErrorsLoadService);
    }
  }

  private void invokeSuccessService(YFCDocument outXML, YFCDocument inXML) {
    YFCDocument successXML = generateSuccessXML(outXML, inXML);
    invokeBackupService(successXML, onSuccessServiceName, suspendOnSuccessServiceError);
  }

  private void invokeFailureService(YFCDocument inXML, Exception ex) {
    if (!SCUtil.isVoid(onFailureServiceName)) {
      YFCDocument failureXML = generateFailureXML(ex, inXML);
      invokeBackupService(failureXML, onFailureServiceName, suspendOnFailureServiceError);
    }
    if (suspendOnException(suspendOnServiceError)) {
      throwServiceSuspendException(ex, suspendOnServiceError);
    }
  }

  private void invokeBackupService(YFCDocument backupXML, String backupServieName,
      String suspendOnBackupService) {
    if (SCUtil.isVoid(backupServieName)) {
      return;
    }
    ServiceInvoker oSI = null;
    try {
      //TODO: Buggy code: Needs rework
      oSI = ServiceInvokerManager.getInstance().getServiceInvoker();
      YFSEnvironment newEnv = oSI.createNewEnvironment("system", "system");
      oSI.setYFSEnvironment(newEnv);
      oSI.invokeYantraService(backupServieName, backupXML);
      LoggerUtil.debugLog("Service invoked successfully: " + backupServieName, logger, backupXML);
    } catch (Exception ex) {
      LoggerUtil.errorLog("Unable to invoke " + backupServieName, logger, backupXML);
      LoggerUtil.errorLog("Caught Exception : " + ex.getClass().getName(), logger, ex);
      if (suspendOnException(suspendOnBackupService)) {
        throwServiceSuspendException(ex, suspendOnBackupService);
      }
    } finally {
      if (!SCUtil.isVoid(oSI) && oSI.hasEnvironment()) {
        oSI.releaseYFSEnvironment();
      }
    }
  }

  private YFCDocument generateSuccessXML(YFCDocument outXML, YFCDocument inXML) {
    return generateXML(outXML, inXML, includeInXMLForSuccess);
  }

  private YFCDocument generateFailureXML(Exception ex, YFCDocument inXML) {
    YFCDocument errorDoc = getErrorDocFromException(ex);
    return generateXML(errorDoc, inXML, includeInXMLForFailure);
  }

  private YFCDocument generateXML(YFCDocument outXML, YFCDocument inXML, String includeInputXML) {
    if (!SCUtil.isVoid(outXML) && (includeInXML(includeInputXML))) {
      YFCElement rootOutXML = outXML.getDocumentElement();
      YFCElement rootInXML = inXML.getDocumentElement();
      if (!SCUtil.isVoid(rootOutXML) && !SCUtil.isVoid(rootInXML)) {
        YFCElement eleInXML = rootOutXML.createChild(CommonLiterals.INPUTXML);
        eleInXML.appendChild(outXML.importNode(rootInXML, true));
      }
    } else if (SCUtil.isVoid(outXML)) {
      outXML = inXML;
    }
    return outXML;
  }

  private boolean includeInXML(String includeInXML) {
    return !SCUtil.isVoid(includeInXML) && includeInXML.equalsIgnoreCase(CommonLiterals.YES);
  }

  private void throwServiceSuspendException(Exception ex, String suspendIndicator) {
    if (suspendOnException(suspendIndicator)) {
      loadSuspendErrorsIfRequired();
      String errorString = LoggerUtil.generateLogMessage(ex.getClass().getName(), ex);
      if (CommonLiterals.YES.equalsIgnoreCase(suspendIndicator)
          || SuspendErrorUtil.isServiceSuspendError(errorString)) {
        throw new ServiceSuspendException(errorString);
      }
    }
  }

  private boolean suspendOnException(String suspendIndicator) {
    return !SCUtil.isVoid(suspendIndicator)
        && (CommonLiterals.YES.equalsIgnoreCase(suspendIndicator) || CommonLiterals.CHECK
            .equalsIgnoreCase(suspendIndicator));
  }

  private void loadSuspendErrorsIfRequired() {
    if (!SuspendErrorUtil.suspendErrorMapLoaded() && !SCUtil.isVoid(suspendableErrorsLoadService)) {
      YFCDocument inXML = YFCDocument.createDocument("RootElement");
      YFCElement eleRoot = inXML.getDocumentElement();
      eleRoot.setAttribute("GetAllErrors", CommonLiterals.YES);
      eleRoot.setAttribute("LoadSuspendErrors", CommonLiterals.YES);
      invokeYantraService(suspendableErrorsLoadService, inXML);
    }
  }

  private YFCDocument getErrorDocFromException(Exception ex) {
    YFCDocument errorDoc = null;
    if (ex instanceof YFCException) {
      errorDoc = ((YFCException) ex).getXML();
    }
    return !SCUtil.isVoid(errorDoc) ? errorDoc : getDefaultErrorDoc(ex);
  }

  private YFCDocument getDefaultErrorDoc(Exception ex) {
    return ExceptionUtil.getYFSExceptionDocument(getErrorCode(ex), ex);
  }

  private String getErrorCode(Exception ex) {
    String errorCode = null;
    if (ex instanceof YFSException) {
      errorCode = ((YFSException) ex).getErrorCode();
    }

    if (SCUtil.isVoid(errorCode)) {
      // ERR9900023=Unknown exception while invoking service.
      errorCode = ExceptionLiterals.UNKNOWN_SERVICE_ERROR;
    }
    return errorCode;
  }

}
