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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.annotation.FlowerServiceUtil;
import com.ly.train.flower.common.exception.ServiceNotFoundException;
import com.ly.train.flower.common.scanner.DefaultClassScanner;
import com.ly.train.flower.common.service.FlowerService;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.util.Assert;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.registry.Registry;
import com.ly.train.flower.registry.config.ServiceInfo;

public class ServiceFactory extends AbstractInit {
  private static final Logger logger = LoggerFactory.getLogger(ServiceFactory.class);
  private final ServiceLoader serviceLoader = new ServiceLoader(this);
  // <flowName, ServiceFlow>
  private final ConcurrentMap<String, ServiceFlow> serviceFlows = new ConcurrentHashMap<>();
  private final FlowerConfig flowerConfig;
  private final FlowerFactory flowerFactory;

  public ServiceFactory(FlowerFactory flowerFactory) {
    this.flowerFactory = flowerFactory;
    this.flowerConfig = flowerFactory.getFlowerConfig();
  }

  @Override
  protected void doInit() {
    serviceLoader.init();
    String basePackage = flowerConfig.getBasePackage();
    if (StringUtil.isBlank(basePackage)) {
      return;
    }

    Set<Class<?>> flowers =
        DefaultClassScanner.getInstance().getClassListByAnnotation(basePackage,
            com.ly.train.flower.common.annotation.FlowerService.class);
    logger.info("scan flowerService, basePackage : {}, find flowerService : {}", basePackage, flowers.size());
    for (Class<?> clazz : flowers) {
      serviceLoader.registerServiceType(FlowerServiceUtil.getServiceName(clazz), clazz);
    }
  }

  public ServiceLoader getServiceLoader() {
    return serviceLoader;
  }

  public void registerService(String serviceName, String serviceClassName) {
    serviceLoader.registerServiceType(serviceName, serviceClassName);

    ServiceMeta serviceMeta = serviceLoader.loadServiceMeta(serviceName);

    ServiceConfig serviceConfig = new ServiceConfig();
    serviceConfig.setServiceName(serviceName);
    serviceConfig.setServiceMeta(serviceMeta);
    serviceConfig.setApplication(flowerConfig.getName());
    // flowerFactory.getActorFactory().buildServiceRouter(serviceConfig, -1);

    Set<Registry> registries = flowerFactory.getRegistry();
    // logger.info("注册中心 {} : {}", serviceName, registries.size());
    if (registries.isEmpty()) {
      return;
    }


    serviceClassName = serviceMeta.getServiceClassName();

    ServiceInfo serviceInfo = new ServiceInfo();
    serviceInfo.setApplication(flowerConfig.getName());
    serviceInfo.setAddress(flowerConfig.toURL());
    serviceInfo.setCreateTime(new Date());
    serviceInfo.setServiceMeta(serviceMeta);
    for (Registry registry : registries) {
      registry.register(serviceInfo);
    }
  }

  public void registerService(String serviceName, Class<?> serviceClass) {
    registerService(serviceName, serviceClass.getName());
  }

  public void registerService(Map<String, String> map) {
    for (Map.Entry<String, String> entry : map.entrySet()) {
      registerService(entry.getKey().trim(), entry.getValue().trim());
    }

  }

  public void registerFlowerService(String serviceName, FlowerService flowerService) {
    serviceLoader.registerFlowerService(serviceName, flowerService);
  }

  public FlowerService getService(String serviceName) {
    return serviceLoader.loadService(serviceName);
  }

  public String getServiceClassName(String serviceName) {
    ServiceMeta serviceMeta = serviceLoader.loadServiceMeta(serviceName);
    if (serviceMeta == null) {
      throw new ServiceNotFoundException("serviceName : " + serviceName);
    }
    return serviceMeta.getServiceClassName();
  }

  public String getServiceClassParameter(String serviceName) {
    return getServiceConf(serviceName, 1);
  }

  private String getServiceConf(String serviceName, int index) {
    return serviceLoader.loadServiceMeta(serviceName).getConfig(index);
  }

  /**
   * 1. 已经存在指定 flowName 的流程，则返回原有流程对象<br/>
   * 2. 不存在指定 flowName 的流程，则新建一个流程对象并缓存
   * 
   * @param flowName 流程名称
   * @return {@code ServiceFlow}
   */
  public ServiceFlow getOrCreateServiceFlow(String flowName) {
    Assert.notNull(flowName, "flowName can't be null !");
    ServiceFlow serviceFlow = serviceFlows.get(flowName);
    if (serviceFlow != null) {
      return serviceFlow;
    }
    serviceFlow = getServiceFlowFromRegistry(flowName);
    if (serviceFlow == null) {
      serviceFlow = new ServiceFlow(flowName, flowerFactory);
      serviceFlows.putIfAbsent(flowName, serviceFlow);
    }
    return serviceFlow;
  }

  /**
   * 1. 首先从本地加载<br/>
   * 2. 本地加载不到，如果有配置注册中心，就从配置中心获取
   * 
   * @param serviceConfig serviceConfig
   * @return {@link ServiceMeta}
   */
  public ServiceMeta loadServiceMeta(ServiceConfig serviceConfig) {
    ServiceMeta serviceMeta = serviceLoader.loadServiceMeta(serviceConfig.getServiceName());
    if (serviceMeta != null) {
      serviceConfig.setLocal(true);
      return serviceMeta;
    }

    List<ServiceInfo> serviceInfos = this.loadServiceInfoFromRegistrry(serviceConfig);
    for (ServiceInfo serviceInfo : serviceInfos) {
      serviceConfig.addAddress(serviceInfo.getAddress());
      serviceConfig.setApplication(serviceInfo.getApplication());
      serviceMeta = serviceInfo.getServiceMeta();
    }

    if (serviceMeta != null) {
      serviceConfig.setServiceMeta(serviceMeta);
      serviceConfig.setLocal(false);
      return serviceMeta;
    }
    throw new ServiceNotFoundException("serviceName : " + serviceConfig.getServiceName() + ", serviceConfig : "
        + serviceConfig);
  }

  public List<ServiceInfo> loadServiceInfoFromRegistrry(ServiceConfig serviceConfig) {
    Set<Registry> registries = flowerFactory.getRegistry();
    if (registries == null || registries.isEmpty()) {
      return null;
    }
    ServiceInfo queryInfo = new ServiceInfo();
    ServiceMeta queryMeta = new ServiceMeta();
    queryMeta.setServiceName(serviceConfig.getServiceName());
    queryInfo.setServiceMeta(queryMeta);
    queryInfo.setApplication(serviceConfig.getApplication());
    List<ServiceInfo> ret = new ArrayList<ServiceInfo>();
    for (Registry registry : registries) {
      List<ServiceInfo> serviceInfos = registry.getProvider(queryInfo);
      if (serviceInfos != null) {
        for (ServiceInfo serviceInfo : serviceInfos) {
          logger.info("注册中心获取连接 {} : {}", serviceConfig.getServiceName(), serviceInfo.getServiceMeta());
          if (serviceInfo.getServiceMeta().getServiceName().equals(serviceConfig.getServiceName())) {
            // add service address
            ret.add(serviceInfo);
          }
        }
      }
    }
    if (ret.isEmpty()) {
      throw new ServiceNotFoundException(serviceConfig.getServiceName() + " not found.");
    }

    return ret;
  }

  private ServiceFlow getServiceFlowFromRegistry(String flowName) {
    Set<Registry> registries = flowerFactory.getRegistry();
    ServiceConfig serviceConfig = new ServiceConfig(flowName);
    serviceConfig.setApplication(flowerConfig.getName());
    for (Registry registry : registries) {
      List<ServiceConfig> configs = registry.getServiceConfig(serviceConfig);
      for (ServiceConfig config : configs) {
        if (flowName.contentEquals(config.getFlowName())) {
          ServiceFlow serviceFlow = new ServiceFlow(flowName, config, flowerFactory);
          serviceFlows.putIfAbsent(flowName, serviceFlow);
          logger.info("load ServiceConfig from registry {} ：{}", flowName, serviceFlow);
          return serviceFlow;
        }
      }
    }
    return null;
  }


}
