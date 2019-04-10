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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.common.annotation.FlowerServiceUtil;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.impl.AggregateService;
import com.ly.train.flower.common.util.AnnotationUtil;
import com.ly.train.flower.common.util.Assert;
import com.ly.train.flower.common.util.ClassUtil;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.common.util.Pair;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import com.ly.train.flower.registry.Registry;

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

  // Map<serviceName, ServiceConfig> 每个服务节点的配置信息
  private final ConcurrentMap<String, ServiceConfig> serviceConfigsCache = new ConcurrentHashMap<>();

  private final AtomicInteger index = new AtomicInteger(0);

  private final String flowName;
  private final FlowerFactory flowerFactory;
  private final ServiceFactory serviceFactory;
  private final ServiceLoader serviceLoader;
  /**
   * 流程的头结点
   */
  private ServiceConfig header;

  public ServiceFlow(String flowName, FlowerFactory flowerFactory) {
    this(flowName, null, flowerFactory);
  }

  public ServiceFlow(String flowName, ServiceConfig header, FlowerFactory flowerFactory) {
    this.flowName = flowName;
    this.header = header;
    this.flowerFactory = flowerFactory;
    this.serviceFactory = flowerFactory.getServiceFactory();
    this.serviceLoader = serviceFactory.getServiceLoader();
    if (header != null) {
      initServiceConfigsCache(header);
    }
  }

  private void initServiceConfigsCache(ServiceConfig header) {
    if (header == null) {
      return;
    }
    serviceConfigsCache.putIfAbsent(header.getServiceName(), header);

    if (header.getNextServiceConfigs() == null) {
      return;
    }
    for (ServiceConfig item : header.getNextServiceConfigs()) {
      initServiceConfigsCache(item);
    }
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
    return header;
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
    logger.info("start register ServiceConfig : {}", header);
    flowerFactory.getServiceActorFactory().buildFlowRouter(flowName, 1);
    String json = JSONObject.toJSONString(header);
    ServiceConfig config = JSONObject.parseObject(json, ServiceConfig.class);
    Set<Registry> registries = flowerFactory.getRegistry();
    for (Registry registry : registries) {
      registry.registerServiceConfig(config);
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

    if (header == null) {
      this.header = preConfig;
    }

    ServiceMeta preServiceMeta = serviceFactory.loadServiceMeta(preConfig);
    ServiceMeta nextServiceMeta = serviceFactory.loadServiceMeta(nextConfig);

    if (!preServiceMeta.isInnerAggregateService() && nextServiceMeta.isAggregateService()) {
      ServiceConfig serviceConfig = null;
      Set<ServiceConfig> previousServiceConfigs = findPreviousServiceConfig(header, nextConfig, null);
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
      // 添加成功，更新配置信息
      validateFlow(preServiceName, nextServiceName);
      preConfig.addNextServiceConfig(nextConfig);

      if (nextConfig.isAggregateService()) {
        nextConfig.jointSourceNumberPlus();
      }
      logger.info(" buildFlow : {}, preService : {}, nextService : {}", flowName, preServiceName, nextServiceName);
    }
    return this;
  }

  private Set<ServiceConfig> findPreviousServiceConfig(ServiceConfig header, ServiceConfig serviceConfig,
      Set<ServiceConfig> preServiceConfigs) {
    if (preServiceConfigs == null) {
      preServiceConfigs = new HashSet<ServiceConfig>();
    }

    Set<ServiceConfig> nexts = header.getNextServiceConfigs();
    if (nexts != null)
      for (ServiceConfig item : nexts) {
        Set<ServiceConfig> temp = item.getNextServiceConfigs();
        if (temp != null && temp.contains(serviceConfig)) {
          preServiceConfigs.add(item);
        } else {
          findPreviousServiceConfig(item, serviceConfig, preServiceConfigs);
        }
      }

    return preServiceConfigs;
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
    Assert.notNull(serviceName, "serviceName can't be null.");
    ServiceConfig serviceConfig = serviceConfigsCache.get(serviceName);
    if (serviceConfig == null) {
      serviceConfig = new ServiceConfig();
      serviceConfig.setFlowName(flowName);
      serviceConfig.setServiceName(serviceName);
      serviceConfig.setIndex(index.getAndIncrement());
      serviceConfig.setServiceMeta(serviceFactory.loadServiceMeta(serviceConfig));
      serviceConfigsCache.putIfAbsent(serviceName, serviceConfig);
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
    if (header == null || StringUtil.isBlank(serviceName)) {
      return null;
    }
    if (header.getServiceName().equals(serviceName)) {
      return header.getNextServiceConfigs();
    }
    Set<ServiceConfig> temp = header.getNextServiceConfigs();
    while (temp != null) {
      Set<ServiceConfig> t = new HashSet<ServiceConfig>();
      for (ServiceConfig item : temp) {
        if (item.getServiceName().equals(serviceName)) {
          return item.getNextServiceConfigs();
        }
        if (item.getNextServiceConfigs() != null) {
          t.addAll(item.getNextServiceConfigs());
        }
        temp = t;
      }
    }

    return null;
  }


  /**
   * 获取服务节点的配置信息
   * 
   * @param serviceName 服务节点名称
   * @return {@link ServiceConfig}
   */
  public ServiceConfig getServiceConfig(String serviceName) {
    return serviceConfigsCache.get(serviceName);
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
    Assert.notEquals(preServiceName, nextServiceName, "preServiceName can't equals nextServiceName, preServiceName : "
        + preServiceName + ", nextServiceName : " + nextServiceName);
    ServiceMeta preServiceMata = serviceLoader.loadServiceMeta(preServiceName);
    ServiceMeta nextServiceMata = serviceLoader.loadServiceMeta(nextServiceName);
    if (preServiceMata == null || nextServiceMata == null) {
      return;
    }

    if (preServiceMata.getServiceClassName().equals(Constant.AGGREGATE_SERVICE_NAME)
        || nextServiceMata.getServiceClassName().equals(Constant.AGGREGATE_SERVICE_NAME)) {
      return;
    }

    Class<?> preReturnType = ClassUtil.forName(preServiceMata.getResultType());
    Class<?> nextParamType = ClassUtil.forName(nextServiceMata.getParamType());

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
    builder.append("ServiceFlow [ flowName = ");
    builder.append(flowName);
    builder.append("\r\n\t");

    ServiceConfig hh = header;
    buildString(hh, builder);
    builder.append("\n]");
    return builder.toString();
  }

  private void buildString(ServiceConfig header, StringBuilder builder) {
    if (header == null) {
      return;
    }
    Set<ServiceConfig> nexts = header.getNextServiceConfigs();
    if (nexts == null || nexts.isEmpty()) {
      return;
    }
    StringBuilder temp = new StringBuilder();
    temp.append(header.getSimpleDesc()).append(" ---> ");
    for (ServiceConfig item : nexts) {
      temp.append(item.getSimpleDesc()).append(", ");
    }
    temp.append("\r\n\t");

    if (builder.indexOf(temp.toString()) == -1) {
      builder.append(temp);
    }
    for (ServiceConfig item : nexts) {
      if (item.getNextServiceConfigs() != null) {
        buildString(item, builder);
      }
    }

  }
}
