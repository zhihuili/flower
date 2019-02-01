package com.ly.train.flower.common.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceFactory {
  private static Map<String, String> serviceMap = new ConcurrentHashMap<String, String>();

  public static void registerService(String serviceName, String serviceClass) {
    serviceMap.put(serviceName, serviceClass);
  }

  public static void registerService(Map<String, String> map) {
    for (Map.Entry<String, String> entry : map.entrySet()) {
      registerService(entry.getKey().trim(), entry.getValue().trim());
    }

  }

  public static Service getService(String serviceName) {
    return ServiceLoader.getInstance().loadService(serviceMap.get(serviceName));
  }

  public static String getServiceClassName(String serviceName) {
    return serviceMap.get(serviceName);
  }

}
