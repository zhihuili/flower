package com.ly.train.flower.common.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ly.train.flower.common.service.containe.ServiceFactory;

public class ServiceFlow {

  // Map<flowName,Map<sourceServiceName,Set<targetServiceName>>>
  private static Map<String, Map<String, Set<String>>> flows =
      new ConcurrentHashMap<String, Map<String, Set<String>>>();

  // Map<flowName, Map<serviceName, ServiceConfig>>
  private static Map<String, Map<String, ServiceConfig>> serviceConfigs =
      new ConcurrentHashMap<String, Map<String, ServiceConfig>>();

  public static void buildFlow(String flowName, String preServiceName, String nextServiceName) {
    if ("null".equals(nextServiceName.trim().toLowerCase())) {
      return;
    }
    Map<String, Set<String>> flow = flows.get(flowName);
    if (flow == null) {
      flow = new ConcurrentHashMap<String, Set<String>>();
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

    String s = ServiceFactory.getServiceClassName(nextServiceName);
    if (s != null && s.equals(ServiceConstants.AGGREGATE_SERVICE_NAME)) {
      Map<String, ServiceConfig> serviceConfigMap = serviceConfigs.get(flowName);
      if (serviceConfigMap == null) {
        serviceConfigMap = new ConcurrentHashMap<String, ServiceConfig>();
        serviceConfigs.put(flowName, serviceConfigMap);
      }
      ServiceConfig serviceConfig = serviceConfigMap.get(nextServiceName);
      if (serviceConfig == null) {
        serviceConfig = new ServiceConfig();
        serviceConfigMap.put(nextServiceName, serviceConfig);
      }
      serviceConfig.jointSourceNumberPlus();
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

  public static ServiceConfig getServiceConcig(String flowName, String serviceName) {
    return serviceConfigs.get(flowName).get(serviceName);
  }
}
