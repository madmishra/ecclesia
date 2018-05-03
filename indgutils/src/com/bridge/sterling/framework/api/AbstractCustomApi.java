package com.bridge.sterling.framework.api;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Document;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.framework.service.ServiceAccess;
import com.bridge.sterling.framework.service.ServiceInvoker;
import com.bridge.sterling.framework.service.ServiceInvokerManager;
import com.bridge.sterling.utils.ExceptionUtil;
import com.bridge.sterling.utils.GenericUtil;
import com.bridge.sterling.utils.LoggerUtil;
import com.bridge.sterling.utils.StringUtil;
import com.bridge.sterling.utils.XMLUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.date.YTimestamp;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public abstract class AbstractCustomApi implements ServiceAccess, YIFCustomApi {

	private static YFCLogCategory logger = YFCLogCategory.instance(AbstractCustomApi.class.getName());

	private Properties properties;
	private ServiceInvoker serviceInvoker;


	public ServiceInvoker initServiceInvoker(YFSEnvironment env) {
		if (SCUtil.isVoid(this.serviceInvoker)) {
			this.serviceInvoker = ServiceInvokerManager.getInstance().getServiceInvoker();
		}
		this.serviceInvoker.setYFSEnvironment(env);
		return this.serviceInvoker;
	}

	public ServiceInvoker getServiceInvoker() {
		return ServiceInvokerManager.validateServiceInvoker(this.serviceInvoker);
	}

	public YFCDocument invokeYantraService(String serviceName, YFCDocument inXml) {
		return getServiceInvoker().invokeYantraService(serviceName, inXml);
	}

	public YFCDocument invokeYantraApi(String apiName, YFCDocument inXml) {
		return getServiceInvoker().invokeYantraApi(apiName, inXml);
	}

	public YFCDocument invokeYantraApi(String apiName, YFCDocument inXml, YFCDocument tempXML) {
		return getServiceInvoker().invokeYantraApi(apiName, inXml, tempXML);
	}

	public YFCDocument invokeYantraApi(String apiName, YFCDocument inXml, String tempXML) {
		return getServiceInvoker().invokeYantraApi(apiName, inXml, tempXML);
	}

	public YFCDocument invokeEntityApi(String apiName, YFCDocument inXml, YFCDocument tempXML) {
		return getServiceInvoker().invokeEntityApi(apiName, inXml, tempXML);
	}


	public Object getTxnObject(String key) {
		return getServiceInvoker().getTxnObject(key);
	}

	public void setTxnObject(String key, Object obj) {
		getServiceInvoker().setTxnObject(key, obj);
	}

	public YTimestamp getDBDate() {
		return getServiceInvoker().getDBDate();
	}

	public Long getNextSequenceNumber(String sequenceName) {
		return getServiceInvoker().getNextSequenceNumber(sequenceName);
	}

	public Connection getDBConnection() {
		return getServiceInvoker().getDBConnection();
	}

	public String getUserId() {
		return getServiceInvoker().getUserId();
	}

	public String getProgId() {
		return getServiceInvoker().getProgId();
	}

	public void setProperties(Properties prop) {
		this.properties = prop;
	}

	public Properties getProperties() {
		return this.properties;
	}

	/**
	 * Gets property value for a property name from properties. If the value is null/blank, then
	 * method will either return empty string or throw an exception depending on throwException
	 * parameter
	 * 
	 * @param String sPropertyName
	 * @param boolean throwException
	 * @return String value for key in props or blank
	 */

	public String getProperty(String sPropertyName, boolean throwException) {
		String value = null;
		value = getProperties().getProperty(sPropertyName);

		if (SCUtil.isVoid(value)) {
			if (throwException) {
				Map<String, Object> errorMap = new HashMap<String, Object>();
				errorMap.put("Properties", "Properties");
				errorMap.put("Key", StringUtil.nonNull(sPropertyName));
				errorMap.put("Value", StringUtil.nonNull(value));
				// ERR0000164=Invalid Properties
				throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_IN_PROPERTIES, errorMap);
			} else {
				value = "";
			}
		} else {
			value = value.trim();
		}

		return value;
	}

	/**
	 * Gets node specific property value (key = <Node>.<Propertyname>) from properties. If no
	 * node-specific value is found, checks for property without node name. If the value is
	 * null/blank, then method will either return empty string or throw an exception depending on
	 * throwException parameter
	 * 
	 * @param String sPropertyName
	 * @param String sCurrNode
	 * @param boolean throwException
	 * @return String value for key in props or blank
	 */
	public String getNodeProperty(String sPropertyName, String sCurrNode, boolean throwExceptionFlag) {
		String value = null;
		if (!SCUtil.isVoid(sCurrNode)) {
			value = getProperty(sCurrNode + "." + sPropertyName);
		}
		if (SCUtil.isVoid(value)) {
			value = getProperty(sPropertyName, throwExceptionFlag);
		}
		return value;
	}

	/**
	 * Gets node specific property value (key = <Node>.<Propertyname>) from properties. If no
	 * node-specific value is found, checks for property without node name. If the value is
	 * null/blank, then method will return the default Value
	 * 
	 * @param String sPropertyName
	 * @param String sCurrNode
	 * @param String sDefaultValue
	 * @return String value for key in props or sDefaultValue
	 */
	public String getNodeProperty(String sPropertyName, String sCurrNode, String sDefaultValue) {
		String value = null;
		if (!SCUtil.isVoid(sCurrNode)) {
			value = getProperty(sCurrNode + "." + sPropertyName);
		}
		if (SCUtil.isVoid(value)) {
			value = getProperty(sPropertyName, sDefaultValue);
		}
		return value;
	}

	/**
	 * Gets property value for a property name from properties. If the value is null/blank, then
	 * method will return empty string
	 * 
	 * @param String sPropertyName
	 * @return String value for key in props or blank
	 */
	public String getProperty(String sPropertyName) {
		return getProperty(sPropertyName, false);
	}

	/**
	 * Gets property value for a property name from properties. If the value is null/blank, then
	 * Default value is returned
	 * 
	 * @param String sPropertyName
	 * @param String sDefaultValue
	 * @return String value for key in props or sDefaultValue
	 */
	public String getProperty(String sPropertyName, String sDefaultValue) {
		String value = null;
		value = getProperties().getProperty(sPropertyName);
		return (SCUtil.isVoid(value)) ? sDefaultValue : value;
	}


	public Document invoke(YFSEnvironment env, Document inXml) throws YFSException {
		YFCDocument outYFCXml = null;
		LoggerUtil.startComponentLog(logger, this.getClass().getName(), "invoke", inXml);
		try {
			GenericUtil.validateServiceInput(env, inXml);
			initServiceInvoker(env);

			outYFCXml = invoke(XMLUtil.getYFCDocument(inXml));

		} catch (YFSException yfsexp) {
			LoggerUtil.errorLog("Input XML", logger, inXml);
			LoggerUtil.errorLog(yfsexp.getClass().getName(), logger, yfsexp);
			throw yfsexp;
		}
		LoggerUtil.endComponentLog(logger, this.getClass().getName(), "invoke", outYFCXml);
		return XMLUtil.getDocument(outYFCXml);
	}


	public abstract YFCDocument invoke(YFCDocument inXml) throws YFSException;

	/**
	 * 
	 * Method: validateProperties
	 * 
	 * Can be called to validate the existence of any required property keys. Will throw exception if
	 * any key is missing
	 * 
	 * 
	 */
	public void validateProperties(Set<String> requiredKeys) {
		boolean allOK = true;
		Map<String, Object> attrs = new HashMap<String, Object>();
		for (String key : requiredKeys) {
			String value = getProperty(key);
			if (SCUtil.isVoid(value)) {
				allOK = false;
				attrs.put(key, value);
			}
		}

		if (!allOK) {
			throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_REQ_API_PRM_MISSING, attrs);
		}
	}
}
