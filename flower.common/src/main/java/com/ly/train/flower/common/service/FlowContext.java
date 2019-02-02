package com.ly.train.flower.common.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlowContext {

  private static Map<String, ServiceContext> flowContext =
      new ConcurrentHashMap<String, ServiceContext>();

  public static void putServiceContext(String uuid, ServiceContext serviceContext) {
    flowContext.put(uuid, serviceContext);
  }

  public static ServiceContext getServiceContext(String uuid) {
    return flowContext.get(uuid);
  }

  public static void removeServiceContext(String uuid) {
    flowContext.remove(uuid);
  }
}
