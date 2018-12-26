package com.ly.train.flower.common.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {
  private static Map<String, Service> serviceMap = new HashMap<String, Service>();

  public static void registerService(String serviceName, Service service) {
    serviceMap.put(serviceName, service);
  }

  public static Service getService(String serviceName) {
    return serviceMap.get(serviceName);
  }

}
