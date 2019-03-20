/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.common.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.util.StringUtil;

public class ServiceFlow {

  // Map<flowName,Map<sourceServiceName,Set<targetServiceName>>>
  private static Map<String, Map<String, Set<String>>> flows =
      new ConcurrentHashMap<String, Map<String, Set<String>>>();

  // Map<flowName, Map<serviceName, ServiceConfig>>
  private static Map<String, Map<String, ServiceConfig>> serviceConfigs =
      new ConcurrentHashMap<String, Map<String, ServiceConfig>>();

  public static void buildFlow(String flowName, Class<?> preServiceClass, Class<?> nextServiceClass) {
    final FlowerService preServiceAnnotation = preServiceClass.getAnnotation(FlowerService.class);
    final FlowerService nextServiceAnnotation = nextServiceClass.getAnnotation(FlowerService.class);
    String preServiceName = preServiceClass.getSimpleName();
    String nextServiceName = nextServiceClass.getSimpleName();

    if (preServiceAnnotation != null && StringUtil.isNotBlank(preServiceAnnotation.value())) {
      preServiceName = preServiceAnnotation.value();
    }
    if (nextServiceAnnotation != null && StringUtil.isNotBlank(nextServiceAnnotation.value())) {
      nextServiceName = nextServiceAnnotation.value();
    }
    buildFlow(flowName, preServiceName, nextServiceName);
  }

  public static void buildFlow(String flowName, String preServiceName, String nextServiceName) {
    if ("null".equals(nextServiceName.trim().toLowerCase())) {
      return;
    }
    Map<String, Set<String>> flow = flows.get(flowName);
    if (flow == null) {
      flow = new ConcurrentHashMap<String, Set<String>>();
      flows.put(flowName, flow);
    }

    Set<String> set = flow.get(preServiceName);

    if (set == null) {
      set = new HashSet<String>();
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
