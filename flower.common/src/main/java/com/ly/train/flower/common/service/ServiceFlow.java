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
import java.util.concurrent.ConcurrentMap;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.service.container.ServiceLoader;
import com.ly.train.flower.common.service.container.ServiceMeta;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

public class ServiceFlow {
  private static final Logger logger = LoggerFactory.getLogger(ServiceFlow.class);
  // Map<flowName,Map<sourceServiceName,Set<targetServiceName>>>
  private static final ConcurrentMap<String, Map<String, Set<String>>> flowCache =
      new ConcurrentHashMap<String, Map<String, Set<String>>>();

  // Map<flowName, Map<serviceName, ServiceConfig>>
  private static final ConcurrentMap<String, Map<String, ServiceConfig>> serviceConfigs =
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
    validateFlow(preServiceName, nextServiceName);

    Map<String, Set<String>> flow = flowCache.get(flowName);
    if (flow == null) {
      flow = new ConcurrentHashMap<String, Set<String>>();
      flowCache.put(flowName, flow);
    }

    Set<String> set = flow.get(preServiceName);

    if (set == null) {
      set = new HashSet<String>();
      flow.put(preServiceName, set);
    }

    boolean ret = set.add(nextServiceName);
    if (!ret) {
      return;
    }
    logger.info(" buildFlow : {}, preService : {}, nextService : {}", flowName, preServiceName, nextServiceName);
    String s = ServiceLoader.getInstance().loadServiceMeta(nextServiceName).getServiceClass().getName();
    if (Constant.AGGREGATE_SERVICE_NAME.equals(s)) {
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
    Map<String, Set<String>> flow = flowCache.get(flowName);
    if (flow == null)
      return null;
    return flow.get(serviceName);
  }

  public static ServiceConfig getServiceConfig(String flowName, String serviceName) {
    return serviceConfigs.get(flowName).get(serviceName);
  }

  private static void validateFlow(String preServiceName, String nextServiceName) {
    ServiceMeta preServiceMata = ServiceLoader.getInstance().loadServiceMeta(preServiceName);
    ServiceMeta nextServiceMata = ServiceLoader.getInstance().loadServiceMeta(nextServiceName);
    if (preServiceMata == null || nextServiceMata == null) {
      return;
    }

    if (preServiceMata.getServiceClass().getName().equals(Constant.AGGREGATE_SERVICE_NAME)
        || nextServiceMata.getServiceClass().getName().equals(Constant.AGGREGATE_SERVICE_NAME)) {
      return;
    }

    Class<?> preReturnType = preServiceMata.getResultType();
    Class<?> nextParamType = nextServiceMata.getParamType();

    if (preReturnType == null || nextParamType == null) {
      throw new FlowerException(preServiceMata.getServiceClass() + "->preReturnType : " + preReturnType + ", "
          + nextServiceMata.getServiceClass() + "-> nextParamType : " + nextParamType);
    }

    if (!nextParamType.isAssignableFrom(preReturnType)) {
      throw new FlowerException("build flower error, because " + preServiceMata.getServiceClass() + " (" + preReturnType.getSimpleName()
          + ") is not compatible for " + nextServiceMata.getServiceClass() + "(" + nextParamType.getSimpleName() + ")");
    }

  }
}
