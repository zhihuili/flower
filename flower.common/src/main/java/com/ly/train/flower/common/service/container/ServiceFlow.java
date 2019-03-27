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
package com.ly.train.flower.common.service.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.exception.ServiceNotFoundException;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.impl.AggregateService;
import com.ly.train.flower.common.util.AnnotationUtil;
import com.ly.train.flower.common.util.Assert;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.common.util.Pair;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * 
 * 服务流程
 * 
 * <p>
 * <code>
 * ServiceFlow.getOrCreate("flowSample")
 *            .buildFlow("serviceA", "serviceB")
 *            .buildFlow("serviceB","serviceC");
 * </code>
 * <p>
 * <code>
 * ServiceFlow.getOrCreate("flowSample")
 *            .buildAggregateFlow(Arrays.asList("serviceB","serviceC");
 * </code>
 * 
 * 
 * @author zhihui.li
 * @author leeyazhou
 *
 */
public final class ServiceFlow {
  private static final Logger logger = LoggerFactory.getLogger(ServiceFlow.class);
  private static final ConcurrentMap<String, ServiceFlow> serviceFlows = new ConcurrentHashMap<>();

  // Map<sourceServiceName,Set<targetServiceName>> 流程
  private final ConcurrentMap<String, Set<ServiceConfig>> servicesOfFlow = new ConcurrentHashMap<>();

  // Map<serviceName, ServiceConfig> 每个服务节点的配置信息
  private final ConcurrentMap<String, ServiceConfig> serviceConfigs = new ConcurrentHashMap<>();

  private final String flowName;

  /**
   * 流程的第一个服务
   */
  private ServiceConfig headServiceConfig;

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
   * @return {@link ServiceConfig}
   */
  public ServiceConfig getHeadServiceConfig() {
    return headServiceConfig;
  }

  /**
   * 1. 已经存在指定 flowName 的流程，则返回原有流程对象<br/>
   * 2. 不存在指定 flowName 的流程，则新建一个流程对象并缓存
   * 
   * @param flowName 流程名称
   * @return {@code ServiceFlow}
   */
  public static ServiceFlow getOrCreate(String flowName) {
    Assert.notNull(flowName, "flowName can't be null !");
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
   * 聚合服务节点名称生成
   * <p>
   * 对名字进行排序后拼接成字符串：serviceA
   * 
   * @param serviceNames
   * @return
   */
  public String generateAggregateServiceName(List<String> serviceNames) {
    StringBuilder sb = new StringBuilder();
    sb.append(flowName).append("$");
    Collections.sort(serviceNames);
    for (int i = 0; i < serviceNames.size(); i++) {
      if (i == 0) {
        sb.append(serviceNames.get(i));
      } else {
        sb.append("_").append(serviceNames.get(i));
      }
    }
    sb.append("$aggregateService");
    return sb.toString();
  }

  public String generateAggregateServiceName2(List<Class<?>> serviceNameClasses) {
    List<String> serviceNames = new ArrayList<>();
    for (Class<?> cl : serviceNameClasses) {
      serviceNames.add(AnnotationUtil.getFlowerServiceValue(cl));
    }
    return generateAggregateServiceName(serviceNames);
  }

  /**
   * 添加聚合服务节点
   * 
   * @param preServiceNames 需要聚合的服务名称
   * @return {@link ServiceFlow}
   */
  public ServiceFlow buildAggregateFlow(List<String> preServiceNames) {
    final String aggregateServiceName = generateAggregateServiceName(preServiceNames);
    ServiceFactory.registerService(aggregateServiceName, AggregateService.class);

    for (String s : preServiceNames) {
      buildFlow(s, aggregateServiceName);
    }

    return this;
  }

  public ServiceFlow buildAggregateFlow2(List<Class<?>> preServiceNames) {
    final String aggregateServiceName = generateAggregateServiceName2(preServiceNames);
    ServiceFactory.registerService(aggregateServiceName, AggregateService.class);

    for (Class<?> s : preServiceNames) {
      String serviceName = AnnotationUtil.getFlowerServiceValue(s);
      buildFlow(serviceName, aggregateServiceName);
    }

    return this;
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

    ServiceConfig preConfig = getOrCreateServiceConfig(preServiceName);
    ServiceConfig nextConfig = getOrCreateServiceConfig(nextServiceName);

    Set<ServiceConfig> nextServices = servicesOfFlow.get(preServiceName);

    if (nextServices == null) {
      nextServices = new HashSet<>();
      servicesOfFlow.put(preServiceName, nextServices);
    }
    if (headServiceConfig == null) {
      this.headServiceConfig = preConfig;
    }

    boolean ret = nextServices.add(nextConfig);
    if (!ret) {
      return this;
    }

    logger.info(" buildFlow : {}, preService : {}, nextService : {}", flowName, preServiceName, nextServiceName);
    // 添加成功，更新配置信息
    preConfig.addNextServiceConfig(nextConfig);
    nextConfig.addPreviousServiceConfig(preConfig);


    ServiceMeta serviceMeta = ServiceLoader.getInstance().loadServiceMeta(nextServiceName);
    if (serviceMeta == null) {
      throw new ServiceNotFoundException("serviceName : " + nextServiceName);
    }
    if (Constant.AGGREGATE_SERVICE_NAME.equals(serviceMeta.getServiceClass().getName())) {
      if (!serviceConfigs.containsKey(nextServiceName)) {
        serviceConfigs.put(nextServiceName, nextConfig);
      }
      nextConfig.jointSourceNumberPlus();
    }
    return this;
  }

  /**
   * 配置并行服务节点
   * 
   * @param previousServiceName 当前服务节点
   * @param nextServiceNames 下行服务节点名称
   * @return
   */
  public ServiceFlow buildParelelFlow(String previousServiceName, Collection<String> nextServiceNames) {
    for (String nextServiceName : nextServiceNames) {
      buildFlow(previousServiceName, nextServiceName);
    }
    return this;
  }

  /**
   * 配置并行服务节点
   * 
   * @param previousServiceName 当前服务节点
   * @param nextServiceClass 下行服务节点类名称
   * @return
   */
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

  /**
   * 获取 OR 初始化服务节点配置信息
   * 
   * @param serviceName 服务节点名称
   * @return {@link ServiceConfig}
   */
  private ServiceConfig getOrCreateServiceConfig(String serviceName) {
    ServiceConfig serviceConfig = serviceConfigs.get(serviceName);
    if (serviceConfig == null) {
      serviceConfig = new ServiceConfig(flowName);
      serviceConfig.setServiceName(serviceName);
      serviceConfigs.putIfAbsent(serviceName, serviceConfig);
    }
    return serviceConfig;
  }

  /**
   * 获取下行服务节点
   * 
   * @param serviceName 服务节点名称
   * @return {@link ServiceConfig}
   */
  public Set<ServiceConfig> getNextFlow(String serviceName) {
    return servicesOfFlow.get(serviceName);
  }

  /**
   * 获取服务节点的配置信息
   * 
   * @param serviceName 服务节点名称
   * @return {@link ServiceConfig}
   */
  public ServiceConfig getServiceConfig(String serviceName) {
    return serviceConfigs.get(serviceName);
  }

  /**
   * 校验流程参数是否兼容
   * 
   * @param preServiceName 前一个服务节点名称
   * @param nextServiceName 后一个服务节点名称
   */
  private void validateFlow(String preServiceName, String nextServiceName) {
    Assert.notNull(preServiceName, "preServiceName can't be null !");
    Assert.notNull(nextServiceName, "nextServiceName can't be null !");

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
    Set<ServiceConfig> nextServices = servicesOfFlow.get(getHeadServiceConfig().getServiceName());

    builder.append(getHeadServiceConfig().getSimpleDesc());
    builder.append(" --> ");
    getHeadServiceConfig().getNextServiceConfigs().forEach(item -> {
      builder.append(item.getSimpleDesc()).append(",");
    });

    if (nextServices != null) {
      for (Map.Entry<String, Set<ServiceConfig>> entry : servicesOfFlow.entrySet()) {
        if (getHeadServiceConfig().getServiceName().equals(entry.getKey())) {
          continue;
        }
        builder.append("\r\n\t");
        builder.append(getServiceConfig(entry.getKey()).getSimpleDesc());
        builder.append(" -- > ");
        entry.getValue().forEach(item -> {
          builder.append(item.getSimpleDesc()).append(", ");
        });
      }
    }


    builder.append("\n]");
    return builder.toString();
  }


}
