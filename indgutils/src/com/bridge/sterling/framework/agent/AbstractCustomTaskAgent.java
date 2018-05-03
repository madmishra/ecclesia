package com.bridge.sterling.framework.agent;

import java.sql.Connection;

import org.w3c.dom.Document;

import com.bridge.sterling.framework.service.ServiceAccess;
import com.bridge.sterling.framework.service.ServiceInvoker;
import com.bridge.sterling.framework.service.ServiceInvokerManager;
import com.bridge.sterling.utils.GenericUtil;
import com.bridge.sterling.utils.LoggerUtil;
import com.bridge.sterling.utils.XMLUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.ycp.japi.util.YCPBaseTaskAgent;
import com.yantra.yfc.date.YTimestamp;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public abstract class AbstractCustomTaskAgent extends YCPBaseTaskAgent implements ServiceAccess {

  private static YFCLogCategory logger = YFCLogCategory.instance(AbstractCustomTaskAgent.class);

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
  
  public YFCDocument invokeYantraApi(String apiName, YFCDocument inXml, YFCDocument tempXml) {
    return getServiceInvoker().invokeYantraApi(apiName, inXml, tempXml);
  }
  
  public YFCDocument invokeYantraApi(String apiName, YFCDocument inXml, String tempXml) {
	    return getServiceInvoker().invokeYantraApi(apiName, inXml, tempXml);
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

  public Long getNextSequenceNumber(String sequenceName) {
    return getServiceInvoker().getNextSequenceNumber(sequenceName);
  }
  
  public YTimestamp getDBDate() {
    return getServiceInvoker().getDBDate();
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

  public Document executeTask(YFSEnvironment env, Document inXml) {
    LoggerUtil.startComponentLog(logger, this.getClass().getName(), "executeTask", inXml);

    YFCDocument outDoc = null;
    GenericUtil.validateServiceInput(env, inXml);

    try {
      initServiceInvoker(env);
      outDoc = executeTask(XMLUtil.getYFCDocument(inXml));
    } catch (YFSException yfsexp) {
      LoggerUtil.logExceptionWithXML(logger, "executeTask", inXml, yfsexp);
      throw yfsexp;
    }

    LoggerUtil.endComponentLog(logger, this.getClass().getName(), "executeTask", "void");
    return XMLUtil.getDocument(outDoc);
  }

  public abstract YFCDocument executeTask(YFCDocument executeTaskXml);

}
