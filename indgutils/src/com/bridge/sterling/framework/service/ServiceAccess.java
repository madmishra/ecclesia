package com.bridge.sterling.framework.service;


import com.yantra.yfs.japi.YFSEnvironment;

public interface ServiceAccess extends ServiceCapability {

  public ServiceInvoker initServiceInvoker(YFSEnvironment env);

  public ServiceInvoker getServiceInvoker();

}
