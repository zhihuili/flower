package com.ly.train.flower.common.service.containe;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FlowContext {

  private static final long DefaultTimeoutMilliseconds = 300000;

  private static Map<String, ServiceContext> flowContext =
      new ConcurrentHashMap<String, ServiceContext>();

  public static void putServiceContext(String uuid, ServiceContext serviceContext) {
    serviceContext.setLastUsedMilliseconds(System.currentTimeMillis());
    flowContext.put(uuid, serviceContext);
  }

  public static ServiceContext getServiceContext(String uuid) {
    return flowContext.get(uuid);
  }

  public static void removeServiceContext(String uuid) {
    flowContext.remove(uuid);
  }

  public static void clearTimeoutServiceContext(){
    Set<String> keys = flowContext.keySet();
    long currentTimeMillis = System.currentTimeMillis();
    for (String key:keys) {
      ServiceContext serviceContext = flowContext.get(key);
      if(serviceContext != null){
        if(currentTimeMillis - serviceContext.getLastUsedMilliseconds() > DefaultTimeoutMilliseconds){
          flowContext.remove(key);
        }
      }
    }
  }
}
