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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.exception.ServiceNotFoundException;
import com.ly.train.flower.common.service.container.ServiceLoader;
import com.ly.train.flower.common.service.container.ServiceMeta;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.common.util.Pair;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

public class ServiceFlow {
  private static final Logger logger = LoggerFactory.getLogger(ServiceFlow.class);
  private static final ConcurrentMap<String, ServiceFlow> serviceFlows = new ConcurrentHashMap<>();

  // Map<sourceServiceName,Set<targetServiceName>>
  private final ConcurrentMap<String, Set<String>> servicesOfFlow = new ConcurrentHashMap<>();

  // Map<serviceName, ServiceConfig>
  private final ConcurrentMap<String, ServiceConfig> serviceConfigs = new ConcurrentHashMap<>();

  private final String flowName;

  /**
   * 流程的第一个服务
   */
  private String headServiceName;

  private ServiceFlow(String flowName) {
    this.flowName = flowName;
  }

  /**
   * 流程名称
   * 
   * @return 流程名称
   */
  public String getFlowName() {
    return flowName;
  }

  /**
   * 流程的第一个服务
   * 
   * @return the headServiceName
   */
  public String getHeadServiceName() {
    return headServiceName;
  }

  /**
   * 1. 已经存在指定 flowName 的流程，则返回原有流程对象<br/>
   * 2. 不存在指定 flowName 的流程，则新建一个流程对象并缓存
   * 
   * @param flowName 流程名称
   * @return {@code ServiceFlow}
   */
  public static ServiceFlow getOrCreate(String flowName) {
    ServiceFlow serviceFlow = serviceFlows.get(flowName);
    if (serviceFlow == null) {
      synchronized (logger) {
        if (serviceFlow == null) {
          serviceFlow = new ServiceFlow(flowName);
          serviceFlows.putIfAbsent(flowName, serviceFlow);
        }
      }
    }
    return serviceFlow;
  }

  /**
   * 组建流程节点
   * 
   * @param preServiceClass 前一个流程服务节点类
   * @param nextServiceClass 后一个流程服务节点类
   * @return {@link ServiceFlow}
   */
  public ServiceFlow buildFlow(Class<?> preServiceClass, Class<?> nextServiceClass) {
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
    return buildFlow(preServiceName, nextServiceName);
  }

  /**
   * 组建流程节点
   * 
   * @param preServiceName 前一个流程服务节点名称
   * @param nextServiceName 后一个流程服务节点名称
   * @return {@link ServiceFlow}
   */
  public ServiceFlow buildFlow(String preServiceName, String nextServiceName) {
    validateFlow(preServiceName, nextServiceName);


    Set<String> services = servicesOfFlow.get(preServiceName);

    if (services == null) {
      services = new HashSet<String>();
      servicesOfFlow.put(preServiceName, services);
    }
    if (headServiceName == null) {
      this.headServiceName = preServiceName;
    }

    boolean ret = services.add(nextServiceName);
    if (!ret) {
      return this;
    }
    logger.info(" buildFlow : {}, preService : {}, nextService : {}", flowName, preServiceName, nextServiceName);
    ServiceMeta serviceMeta = ServiceLoader.getInstance().loadServiceMeta(nextServiceName);
    if (serviceMeta == null) {
      throw new ServiceNotFoundException("serviceName : " + nextServiceName);
    }
    String s = serviceMeta.getServiceClass().getName();
    if (Constant.AGGREGATE_SERVICE_NAME.equals(s)) {
      ServiceConfig serviceConfig = serviceConfigs.get(nextServiceName);
      if (serviceConfig == null) {
        serviceConfig = new ServiceConfig(this.flowName);
        serviceConfig.setServiceName(nextServiceName);
        serviceConfigs.put(nextServiceName, serviceConfig);
      }
      serviceConfig.jointSourceNumberPlus();
    }
    return this;
  }

  public ServiceFlow buildParelelFlow(String previousServiceName, Collection<String> nextServiceNames) {
    for (String nextServiceName : nextServiceNames) {
      buildFlow(previousServiceName, nextServiceName);
    }
    return this;
  }

  public ServiceFlow buildParelelFlow(Class<?> previousServiceName, Collection<Class<?>> nextServiceClass) {
    for (Class<?> nextServiceName : nextServiceClass) {
      buildFlow(previousServiceName, nextServiceName);
    }
    return this;
  }

  /**
   * 组建流程节点
   * 
   * @param flow flow
   */
  public ServiceFlow buildFlow(List<Pair<String, String>> flow) {
    for (Pair<String, String> pair : flow) {
      buildFlow(pair.getKey(), pair.getValue());
    }
    return this;
  }

  public Set<String> getNextFlow(String serviceName) {
    return servicesOfFlow.get(serviceName);
  }

  public ServiceConfig getServiceConfig(String serviceName) {
    return serviceConfigs.get(serviceName);
  }

  private void validateFlow(String preServiceName, String nextServiceName) {
    if (StringUtil.isBlank(preServiceName) || StringUtil.isBlank(nextServiceName)) {
      throw new FlowerException(
          "service name can't be null. preServiceName : " + preServiceName + ", nextServiceName : " + nextServiceName);
    }

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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ServiceFlow [\r\n\tflowName = ");
    builder.append(flowName);
    builder.append("\r\n\t");
    Set<String> nextServices = servicesOfFlow.get(headServiceName);

    builder.append(headServiceName);
    builder.append(" --> ").append(servicesOfFlow.get(headServiceName));

    if (nextServices != null) {
      for (Map.Entry<String, Set<String>> entry : servicesOfFlow.entrySet()) {
        if (headServiceName.equals(entry.getKey())) {
          continue;
        }
        builder.append("\r\n\t");
        builder.append(entry.getKey());
        builder.append(" -- > ").append(entry.getValue());
      }
    }


    builder.append("\n]");
    return builder.toString();
  }


}
