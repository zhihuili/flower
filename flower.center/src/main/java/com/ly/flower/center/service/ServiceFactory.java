/**
 * 
 */
package com.ly.flower.center.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.ly.flower.center.model.ServiceInfo;

/**
 * @author leeyazhou
 *
 */
public class ServiceFactory {
  private static final ConcurrentMap<String, List<ServiceInfo>> caches = new ConcurrentHashMap<>();

  public static boolean register(ServiceInfo serviceInfo) {
    List<ServiceInfo> infos = caches.get(serviceInfo.getClassName());
    if (infos == null) {
      infos = new ArrayList<>();
      caches.putIfAbsent(serviceInfo.getClassName(), infos);
      infos = caches.get(serviceInfo.getClassName());
    }

    infos.add(serviceInfo);
    return true;
  }

  public static Map<String, List<ServiceInfo>> getAll() {
    return caches;
  }
}
