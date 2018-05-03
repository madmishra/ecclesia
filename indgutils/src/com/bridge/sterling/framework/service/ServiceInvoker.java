package com.bridge.sterling.framework.service;

import com.yantra.yfs.japi.YFSEnvironment;


public interface ServiceInvoker extends ServiceCapability {
  public void setYFSEnvironment(YFSEnvironment env);

  public boolean hasEnvironment();

  public YFSEnvironment createNewEnvironment();

  public YFSEnvironment createNewEnvironment(String userId, String progId);

  public YFSEnvironment createNewEnvironment(YFSEnvironment env);

  public void releaseYFSEnvironment();

  public void setNewYFSEnvironment();

  public void setNewYFSEnvironment(YFSEnvironment env);
}
