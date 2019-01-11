package com.ly.train.flower.common.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServiceFlow {
  private static Map<String, Set<String>> serviceFlow = new HashMap<String, Set<String>>();

  public static void buildFlow(String preServiceName, String nextServiceName) {
    Set<String> set;
    set = serviceFlow.get(preServiceName);

    if (set == null) {
      set = new HashSet<String>();
      set.add(nextServiceName);
      serviceFlow.put(preServiceName, set);
    }

    set.add(nextServiceName);

    Service s = ServiceFactory.getService(nextServiceName);
    if (s instanceof Joint) {
      ((Joint) s).sourceNumberPlus();
    }
  }

  public static Set<String> getNextFlow(String serviceName) {
    return serviceFlow.get(serviceName);
  }
}
