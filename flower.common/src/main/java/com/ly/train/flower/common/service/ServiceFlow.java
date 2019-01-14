package com.ly.train.flower.common.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServiceFlow {
  // Map<flowName,Map<String sourceServiceName,Set<targetServiceName>>>
  private static Map<String, Map<String, Set<String>>> flows =
      new HashMap<String, Map<String, Set<String>>>();

  public static void buildFlow(String flowName, String preServiceName, String nextServiceName) {
    Map<String, Set<String>> flow = flows.get(flowName);
    if (flow == null) {
      flow = new HashMap<String, Set<String>>();
      flows.put(flowName, flow);
    }

    Set<String> set;
    set = flow.get(preServiceName);

    if (set == null) {
      set = new HashSet<String>();
      set.add(nextServiceName);
      flow.put(preServiceName, set);
    }

    set.add(nextServiceName);

    Service s = ServiceFactory.getService(nextServiceName);
    if (s instanceof Joint) {
      ((Joint) s).sourceNumberPlus();
    }
  }

  public static void buildFlow(String flowName, List<String[]> flow) {
    for (String[] connection : flow) {
      String sourceServiceName = connection[0];
      String targetServiceName = connection[1];
      buildFlow(flowName, sourceServiceName.trim(), targetServiceName.trim());
    }
  }

  public static Set<String> getNextFlow(String flowName, String serviceName) {
    Map<String, Set<String>> flow = flows.get(flowName);
    if (flow == null)
      return null;
    return flow.get(serviceName);
  }
}
