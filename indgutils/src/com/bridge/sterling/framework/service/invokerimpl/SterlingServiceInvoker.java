package com.bridge.sterling.framework.service.invokerimpl;

import java.rmi.RemoteException;
import java.sql.Connection;

import org.w3c.dom.Document;

import com.bridge.sterling.consts.CommonLiterals;
import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.framework.service.ServiceInvoker;
import com.bridge.sterling.utils.ExceptionUtil;
import com.bridge.sterling.utils.LoggerUtil;
import com.bridge.sterling.utils.XMLUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.ycp.core.YCPEntityApi;
import com.yantra.yfc.date.YTimestamp;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class SterlingServiceInvoker implements ServiceInvoker {
	private static YFCLogCategory logger = YFCLogCategory.instance(SterlingServiceInvoker.class
			.getName());


	private YIFApi yifApi;
	private YFSEnvironment env;


	public void setYFSEnvironment(YFSEnvironment env) {
		this.env = env;
	}

	public void setNewYFSEnvironment() {
		setYFSEnvironment(createNewEnvironment());
	}

	public void setNewYFSEnvironment(YFSEnvironment env) {
		setYFSEnvironment(createNewEnvironment(env));
	}


	public boolean hasEnvironment() {
		return !SCUtil.isVoid(getYFSEnvironment());
	}

	public YFSEnvironment createNewEnvironment() {
		return createNewEnvironment(getYFSEnvironment());
	}

	public YFSEnvironment createNewEnvironment(YFSEnvironment env) {
		return createNewEnvironment(env.getUserId(), env.getProgId());
	}

	public YFSEnvironment createNewEnvironment(String userId, String progId) {
		YFSEnvironment newEnv = null;
		try {
			YFCDocument envDoc = getEnvironmentInputXml(userId, progId);
			newEnv = getYIFApi().createEnvironment(envDoc.getDocument());
		} catch (RemoteException remexp) {
			LoggerUtil.errorLog(remexp.getClass().getName(), logger, remexp);
			throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_SERVICE_REMOTE_EXP, remexp);
		}
		return newEnv;
	}

	public void releaseYFSEnvironment() {
		try {
			if (!SCUtil.isVoid(getYFSEnvironment())) {
				getYIFApi().releaseEnvironment(getYFSEnvironment());
			}
		} catch (Exception ex) {
			LoggerUtil.errorLog("releaseEnv: exception(ignored): " + ex.getClass().getName(), logger, ex);
		}
	}

	public YFCDocument invokeYantraService(String serviceName, YFCDocument inXml) {
		LoggerUtil.debugLog(serviceName + " Input Xml", logger, inXml);

		Document outXml = null;
		validateServiceNameAndXml(serviceName, inXml);

		try {
			outXml = getYIFApi().executeFlow(getYFSEnvironment(), serviceName, inXml.getDocument());
		} catch (RemoteException remexp) {
			LoggerUtil.errorLog(remexp.getClass().getName(), logger, remexp);
			throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_SERVICE_REMOTE_EXP, remexp);
		}

		LoggerUtil.debugLog(serviceName + " Output Xml", logger, outXml);
		return XMLUtil.getYFCDocument(outXml);
	}


	public YFCDocument invokeYantraApi(String apiName, YFCDocument inXml) {
		YFCDocument templateXml = null;
		return invokeYantraApi(apiName, inXml, templateXml);
	}

	public YFCDocument invokeYantraApi(String apiName, YFCDocument inXml, YFCDocument templateXml) {
		LoggerUtil.debugLog(apiName + " Input Xml", logger, inXml);
		LoggerUtil.debugLog(apiName + " Template Xml", logger, templateXml);

		Document outXml = null;
		validateServiceNameAndXml(apiName, inXml);
		try {
			if (!SCUtil.isVoid(templateXml)) {
				setApiTemplate(apiName, templateXml.getDocument());
			}
			outXml = getYIFApi().invoke(getYFSEnvironment(), apiName, inXml.getDocument());

			if (!SCUtil.isVoid(templateXml)) {
				clearApiTemplate(apiName);
			}
		} catch (RemoteException remexp) {
			LoggerUtil.errorLog(remexp.getClass().getName(), logger, remexp);
			throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_SERVICE_REMOTE_EXP, remexp);
		}
		LoggerUtil.debugLog(apiName + " Output Xml", logger, outXml);
		return XMLUtil.getYFCDocument(outXml);
	}

	public YFCDocument invokeYantraApi(String apiName, YFCDocument inXml, String templateXml) {
		LoggerUtil.debugLog(apiName + " Input Xml", logger, inXml);
		LoggerUtil.debugLog(apiName + " Template Xml", logger, templateXml);

		Document outXml = null;
		validateServiceNameAndXml(apiName, inXml);
		try {
			if (!SCUtil.isVoid(templateXml)) {
				setApiTemplate(apiName, templateXml);
			}
			outXml = getYIFApi().invoke(getYFSEnvironment(), apiName, inXml.getDocument());

			if (!SCUtil.isVoid(templateXml)) {
				clearApiTemplate(apiName);
			}
		} catch (RemoteException remexp) {
			LoggerUtil.errorLog(remexp.getClass().getName(), logger, remexp);
			throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_SERVICE_REMOTE_EXP, remexp);
		}
		LoggerUtil.debugLog(apiName + " Output Xml", logger, outXml);
		return XMLUtil.getYFCDocument(outXml);
	}

	public Object getTxnObject(String key) {
		return getYFSEnvironment().getTxnObject(key);
	}

	public void setTxnObject(String key, Object obj) {
		getYFSEnvironment().setTxnObject(key, obj);
	}

	public YTimestamp getDBDate() {
		return getYFSContext().getDBDate();
	}

	public Long getNextSequenceNumber(String sequenceName) {
		if (SCUtil.isVoid(sequenceName)) {
			// ERR0000172=Sequence Name cannot be null
			throw ExceptionUtil.getYFSException("ERR0000172");
		}
		return getYFSContext().getNextDBSeqNo(sequenceName);
	}

	public Connection getDBConnection() {
		return getYFSContext().getDBConnection();
	}

	public String getUserId() {
		return getYFSEnvironment().getUserId();
	}

	public String getProgId() {
		return getYFSEnvironment().getProgId();
	}

	public YFCDocument invokeEntityApi(String sApiName, YFCDocument inXml, YFCDocument tempXml) {
		YFCElement tempElem = null;
		if(!SCUtil.isVoid(tempXml)) {
			tempElem = tempXml.getDocumentElement();
		}
		return YCPEntityApi.getInstance().invoke(getYFSContext(), sApiName, inXml, tempElem);
	}

	private YFSEnvironment getYFSEnvironment() {
		if (SCUtil.isVoid(this.env)) {
			throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_SERVICE_ENV_NULL);
		}
		return this.env;
	}


	private YIFApi getYIFApi() {
		if (SCUtil.isVoid(this.yifApi)) {
			this.yifApi = getDefaultYIFApi();
		}
		return this.yifApi;
	}

	private YFSContext getYFSContext() {
		return (YFSContext) getYFSEnvironment();
	}

	private YIFApi getDefaultYIFApi() {
		YIFApi yifApi = null;
		try {
			yifApi = YIFClientFactory.getInstance().getApi();
		} catch (YIFClientCreationException yifCCEx) {
			logger.error(LoggerUtil.generateLogMessage(yifCCEx.getClass().getName(), yifCCEx));
			throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_SERVICE_CC_EXP, yifCCEx);
		}
		return yifApi;
	}

	private void setApiTemplate(String apiName, Document template) {
		YFSEnvironment yfsEnv = getYFSEnvironment();
		if (yfsEnv != null) {
			yfsEnv.setApiTemplate(apiName, template);
		}
	}

	private void setApiTemplate(String apiName, String template) {
		YFSEnvironment yfsEnv = getYFSEnvironment();
		if (yfsEnv != null) {
			yfsEnv.setApiTemplate(apiName, template);
		}
	}

	private void clearApiTemplate(String apiName) {
		YFSEnvironment yfsEnv = getYFSEnvironment();
		if (yfsEnv != null) {
			yfsEnv.clearApiTemplate(apiName);
		}
	}

	private YFCDocument getEnvironmentInputXml(String userId, String progId) {
		YFCDocument envDoc = YFCDocument.createDocument(CommonLiterals.YFSENVIRONMENT);
		YFCElement eleRoot = envDoc.getDocumentElement();
		eleRoot.setAttribute(CommonLiterals.USERID, userId);
		eleRoot.setAttribute(CommonLiterals.PROGID, progId);
		return envDoc;
	}

	private void validateServiceNameAndXml(String serviceName, YFCDocument inDoc) {
		if (SCUtil.isVoid(inDoc)) {
			throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_XML_NULL);
		}
		if (SCUtil.isVoid(serviceName)) {
			throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_SERVICENAME_NULL);
		}
	}
}
