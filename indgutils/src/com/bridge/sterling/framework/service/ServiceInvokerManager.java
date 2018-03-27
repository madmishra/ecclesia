package com.bridge.sterling.framework.service;

import com.bridge.sterling.framework.service.invokerimpl.SterlingServiceInvoker;
import com.bridge.sterling.utils.ExceptionUtil;
import com.sterlingcommerce.baseutil.SCUtil;

public class ServiceInvokerManager {
  private static ServiceInvokerManager _instance = null;

  protected ServiceInvokerManager() {}

  public static ServiceInvokerManager getInstance() {
    if (SCUtil.isVoid(_instance)) {
      _instance = new ServiceInvokerManager();
    }
    return _instance;
  }

  public ServiceInvoker getServiceInvoker() {
    return getDefaultServiceInvoker();
  }

  public ServiceInvoker getDefaultServiceInvoker() {
    return new SterlingServiceInvoker();
  }

  public static ServiceInvoker validateServiceInvoker(ServiceInvoker si) {
    if (SCUtil.isVoid(si) || !si.hasEnvironment()) {
      // ERR9900031=Internal Error. Service Invoker is not initialized.
      throw ExceptionUtil.getYFSException("ERR9900031");
    }
    return si;
  }
}
