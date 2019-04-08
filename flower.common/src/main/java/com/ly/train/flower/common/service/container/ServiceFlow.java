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
import java.util.concurrent.atomic.AtomicInteger;
import com.ly.train.flower.common.annotation.FlowerServiceUtil;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.exception.ServiceNotFoundException;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.impl.AggregateService;
import com.ly.train.flower.common.util.AnnotationUtil;
import com.ly.train.flower.common.util.Assert;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.common.util.Pair;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import com.ly.train.flower.registry.Registry;
import com.ly.train.flower.registry.config.ServiceInfo;

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
 *            .buildFlow(Arrays.asList("serviceB","serviceC"), "serviceD");
 * </code>
 * 
 * 
 * @author zhihui.li
 * @author leeyazhou
 *
 */
public final class ServiceFlow {
  private static final Logger logger = LoggerFactory.getLogger(ServiceFlow.class);

  // Map<sourceServiceName,Set<targetServiceName>> 流程
  private final ConcurrentMap<String, Set<ServiceConfig>> servicesOfFlow = new ConcurrentHashMap<>();

  // Map<serviceName, ServiceConfig> 每个服务节点的配置信息
  private final ConcurrentMap<String, ServiceConfig> serviceConfigs = new ConcurrentHashMap<>();

  private final AtomicInteger index = new AtomicInteger(0);

  private final String flowName;
  private final FlowerFactory flowerFactory;
  private final ServiceFactory serviceFactory;
  private final ServiceLoader serviceLoader;
  /**
   * 流程的第一个服务
   */
  private ServiceConfig headServiceConfig;

  // public ServiceFlow(String flowName) {
  // this(flowName, SimpleFlowerFactory.get());
  // }

  public ServiceFlow(String flowName, FlowerFactory flowerFactory) {
    this.flowName = flowName;
    this.flowerFactory = flowerFactory;
    this.serviceFactory = flowerFactory.getServiceFactory();
    this.serviceLoader = serviceFactory.getServiceLoader();
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
   * @see FlowerFactory#getOrCreateServiceFlow(String)
   */
  // private static ServiceFlow getOrCreate(String flowName) {
  // return
  // SimpleFlowerFactory.get().getServiceFactory().getOrCreateServiceFlow(flowName);
  // }

  /**
   * 1. 已经存在指定 flowName 的流程，则返回原有流程对象<br/>
   * 2. 不存在指定 flowName 的流程，则新建一个流程对象并缓存
   * 
   * @param flowName 流程名称
   * @param serviceFactory {@code ServiceFactory}
   * @return {@code ServiceFlow}
   * @see ServiceFactory#getOrCreateServiceFlow(String)
   */
  public static ServiceFlow getOrCreate(String flowName, ServiceFactory serviceFactory) {
    return serviceFactory.getOrCreateServiceFlow(flowName);
  }

  /**
   * 组建流程节点
   * 
   * @param preServiceClass 前一个流程服务节点类
   * @param nextServiceClass 后一个流程服务节点类
   * @return {@link ServiceFlow}
   */
  public ServiceFlow buildFlow(Class<?> preServiceClass, Class<?> nextServiceClass) {
    String preServiceName = FlowerServiceUtil.getServiceName(preServiceClass);
    String nextServiceName = FlowerServiceUtil.getServiceName(nextServiceClass);

    return buildFlow(preServiceName, nextServiceName);
  }

  /**
   * 聚合服务节点名称生成
   * <p>
   * 对名字进行排序后拼接成字符串：serviceA
   * 
   * @param serviceNames
   * @return str
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

  public ServiceFlow build() {
    logger.info(" build {} success. \n {}", flowName, this);
    logger.info("start register ServiceConfig : {}", headServiceConfig);
    Set<Registry> registries = flowerFactory.getRegistry();
    for (Registry registry : registries) {
      registry.registerServiceConfig(headServiceConfig);
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
    ServiceConfig preConfig = getOrCreateServiceConfig(preServiceName);
    ServiceConfig nextConfig = getOrCreateServiceConfig(nextServiceName);

    Set<ServiceConfig> nextServices = getOrCreateNextFlow(preServiceName);
    if (headServiceConfig == null) {
      this.headServiceConfig = preConfig;
    }

    ServiceMeta preServiceMeta = loadServiceMeta(preConfig);
    ServiceMeta nextServiceMeta = loadServiceMeta(nextConfig);

    if (!preServiceMeta.isInnerAggregateService() && nextServiceMeta.isAggregateService()) {
      ServiceConfig serviceConfig = null;
      Set<ServiceConfig> previousServiceConfigs = nextConfig.getPreviousServiceConfigs();
      if (previousServiceConfigs != null) {
        for (ServiceConfig item : previousServiceConfigs) {
          if (serviceLoader.loadServiceMeta(item.getServiceName()).isInnerAggregateService()) {
            serviceConfig = item;
            break;
          }
        }
      }

      String aggregateServiceName =
          flowName + "$" + preConfig.getServiceName() + "_" + nextConfig.getServiceName() + "_AggregateService";
      if (serviceConfig != null) {
        aggregateServiceName = serviceConfig.getServiceName();
      } else {
        serviceFactory.registerService(aggregateServiceName, AggregateService.class);
      }
      buildFlow(preServiceName, aggregateServiceName);
      buildFlow(aggregateServiceName, nextServiceName);
      logger.info(" buildFlow : {}, preService : {}, nextService : {}", flowName, preServiceName, nextServiceName);
    } else {
      boolean ret = nextServices.add(nextConfig);
      if (!ret) {
        return this;
      }
      // 添加成功，更新配置信息
      validateFlow(preServiceName, nextServiceName);
      preConfig.addNextServiceConfig(nextConfig);
      nextConfig.addPreviousServiceConfig(preConfig);

      if (nextConfig.isAggregateService()) {
        nextConfig.jointSourceNumberPlus();
      }
      logger.info(" buildFlow : {}, preService : {}, nextService : {}", flowName, preServiceName, nextServiceName);
    }
    return this;
  }

  private ServiceMeta loadServiceMeta(ServiceConfig serviceConfig) {
    ServiceMeta serviceMeta = serviceLoader.loadServiceMeta(serviceConfig.getServiceName());
    if (serviceMeta == null) {
      serviceMeta = getFromRegistrry(serviceConfig);
      if (serviceMeta != null) {
        serviceConfig.setServiceMeta(serviceMeta);
        serviceConfig.setLocal(false);
        return serviceMeta;
      }
      throw new ServiceNotFoundException(
          "serviceName : " + serviceConfig.getServiceName() + ", serviceConfig : " + serviceConfig);
    }
    return serviceMeta;
  }

  private ServiceMeta getFromRegistrry(ServiceConfig serviceConfig) {
    Set<Registry> registries = flowerFactory.getRegistry();
    if (registries == null || registries.isEmpty()) {
      return null;
    }
    ServiceMeta serviceMeta = null;
    for (Registry registry : registries) {
      List<ServiceInfo> serviceInfos = registry.getProvider(null);
      if (serviceInfos != null) {
        for (ServiceInfo serviceInfo : serviceInfos) {
          if (serviceInfo.getServiceName().equals(serviceConfig.getServiceName())) {
            // add service address
            serviceConfig.setAddresses(serviceInfo.getAddresses());
            serviceMeta = serviceInfo.getServiceMeta();
          }
        }
      }
    }

    return serviceMeta;
  }

  /**
   * 配置并行服务节点
   * 
   * @param previousServiceName 当前服务节点
   * @param nextServiceNames 下行服务节点名称
   * @return {@link ServiceFlow}
   */
  public ServiceFlow buildFlow(String previousServiceName, Collection<String> nextServiceNames) {
    for (String nextServiceName : nextServiceNames) {
      buildFlow(previousServiceName, nextServiceName);
    }
    return this;
  }

  /**
   * 配置并行服务节点
   * 
   * @param previousServiceNames 前服务节点集合
   * @param nextServiceName 下行节点
   * @return {@link ServiceFlow}
   */
  public ServiceFlow buildFlow(Collection<String> previousServiceNames, String nextServiceName) {
    for (String preServiceName : previousServiceNames) {
      buildFlow(preServiceName, nextServiceName);
    }
    return this;
  }

  /**
   * 配置并行服务节点
   * 
   * @param previousServiceClass 当前服务节点
   * @param nextServiceClasses 下行服务节点类
   * @return {@link ServiceFlow}
   */
  public ServiceFlow buildFlow(Class<?> previousServiceClass, Collection<Class<?>> nextServiceClasses) {
    for (Class<?> nextServiceClass : nextServiceClasses) {
      buildFlow(previousServiceClass, nextServiceClass);
    }
    return this;
  }

  /**
   * 配置并行服务节点
   * 
   * @param previousServiceClasses 前服务节点集合
   * @param nextServiceClass 下行服务节点
   * @return {@link ServiceFlow}
   */
  public ServiceFlow buildFlow(Collection<Class<?>> previousServiceClasses, Class<?> nextServiceClass) {
    for (Class<?> preServiceClass : previousServiceClasses) {
      buildFlow(preServiceClass, nextServiceClass);
    }
    return this;
  }


  /**
   * 组建流程节点
   * 
   * @param flow flow
   * @return {@link ServiceFlow}
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
      serviceConfig = new ServiceConfig();
      serviceConfig.setFlowName(flowName);
      serviceConfig.setServiceName(serviceName);
      serviceConfig.setIndex(index.getAndIncrement());
      serviceConfig.setServiceMeta(loadServiceMeta(serviceConfig));
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

  protected Set<ServiceConfig> getOrCreateNextFlow(String serviceName) {
    Set<ServiceConfig> nextServices = getNextFlow(serviceName);
    if (nextServices == null) {
      nextServices = new HashSet<>();
      servicesOfFlow.put(serviceName, nextServices);
    }
    return nextServices;
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
    ServiceMeta preServiceMata = serviceLoader.loadServiceMeta(preServiceName);
    ServiceMeta nextServiceMata = serviceLoader.loadServiceMeta(nextServiceName);
    if (preServiceMata == null || nextServiceMata == null) {
      return;
    }

    if (preServiceMata.getServiceClassName().equals(Constant.AGGREGATE_SERVICE_NAME)
        || nextServiceMata.getServiceClassName().equals(Constant.AGGREGATE_SERVICE_NAME)) {
      return;
    }

    Class<?> preReturnType = preServiceMata.getResultType();
    Class<?> nextParamType = nextServiceMata.getParamType();

    if (preReturnType == null || nextParamType == null) {
      throw new FlowerException(preServiceMata.getServiceClassName() + "->preReturnType : " + preReturnType + ", "
          + nextServiceMata.getServiceClassName() + "-> nextParamType : " + nextParamType);
    }

    if (!nextParamType.isAssignableFrom(preReturnType)) {
      throw new FlowerException("build flower error, because " + preServiceMata.getServiceClassName() + " ("
          + preReturnType.getSimpleName() + ") is not compatible for " + nextServiceMata.getServiceClassName() + "("
          + nextParamType.getSimpleName() + ")");
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
