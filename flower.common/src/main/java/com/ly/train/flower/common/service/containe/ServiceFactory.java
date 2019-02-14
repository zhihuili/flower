package com.ly.train.flower.common.service.containe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ly.train.flower.common.service.FlowerService;

public class ServiceFactory {
  private static Map<String, String> serviceMap = new ConcurrentHashMap<String, String>();
  private static Map<String, FlowerService> flowerServiceMap =
      new ConcurrentHashMap<String, FlowerService>();

  public static void registerService(String serviceName, String serviceClass) {
    serviceMap.put(serviceName, serviceClass);
  }

  public static void registerService(Map<String, String> map) {
    for (Map.Entry<String, String> entry : map.entrySet()) {
      registerService(entry.getKey().trim(), entry.getValue().trim());
    }

  }

  public static void registerFlowerService(String serviceName, FlowerService flowerService) {
    flowerServiceMap.put(serviceName, flowerService);
  }

  public static FlowerService getService(String serviceName) {
    FlowerService fs = flowerServiceMap.get(serviceName);
    if (fs != null) {
      return fs;
    }
    return ServiceLoader.getInstance().loadService(serviceName);
  }

  public static String getServiceClassName(String serviceName) {
    return serviceMap.get(serviceName);
  }

}
