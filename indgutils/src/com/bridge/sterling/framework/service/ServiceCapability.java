package com.bridge.sterling.framework.service;


import java.sql.Connection;

import com.yantra.yfc.date.YTimestamp;
import com.yantra.yfc.dom.YFCDocument;

public interface ServiceCapability {

  public YFCDocument invokeYantraService(String serviceName, YFCDocument inXml);

  public YFCDocument invokeYantraApi(String apiName, YFCDocument inXml);

  public YFCDocument invokeYantraApi(String apiName, YFCDocument inXml, YFCDocument tempXml);
  
  public YFCDocument invokeYantraApi(String apiName, YFCDocument inXml, String tempXml);

  public Object getTxnObject(String key);

  public void setTxnObject(String key, Object obj);

  public YTimestamp getDBDate();

  public Long getNextSequenceNumber(String sequenceName);

  public Connection getDBConnection();

  public String getUserId();

  public String getProgId();
  
  public YFCDocument invokeEntityApi(String apiName, YFCDocument inXml, YFCDocument tempXml);

}
