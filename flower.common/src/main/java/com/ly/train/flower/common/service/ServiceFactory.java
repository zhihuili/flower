package com.ly.train.flower.common.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {
  private static Map<String, Service> serviceMap = new HashMap<String, Service>();

  public static void registerService(String serviceName, Service service) {
    serviceMap.put(serviceName, service);
  }

  public static void registerService(String serviceName, String serviceClass) {
    serviceMap.put(serviceName, ServiceLoader.getInstance().loadService(serviceClass));
  }

  public static void registerService(Map<String, String> map) {
    for (Map.Entry<String, String> entry : map.entrySet()) {
      registerService(entry.getKey().trim(), entry.getValue().trim());
    }

  }

  public static Service getService(String serviceName) {
    return serviceMap.get(serviceName);
  }

}
