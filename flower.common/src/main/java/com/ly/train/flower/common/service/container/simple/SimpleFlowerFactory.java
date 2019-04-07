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
/**
 * 
 */
package com.ly.train.flower.common.service.container.simple;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.reflections.Reflections;
import com.ly.train.flower.common.akka.ServiceActorFactory;
import com.ly.train.flower.common.annotation.FlowerServiceUtil;
import com.ly.train.flower.common.exception.ExceptionHandler;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceFlow;
import com.ly.train.flower.common.service.container.ServiceLoader;
import com.ly.train.flower.common.util.Assert;
import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.config.parser.FlowerConfigParser;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import com.ly.train.flower.registry.Registry;
import com.ly.train.flower.registry.RegistryFactory;
import com.ly.train.flower.registry.config.RegistryConfig;

/**
 * @author leeyazhou
 *
 */
public class SimpleFlowerFactory implements FlowerFactory {
  private static final Logger logger = LoggerFactory.getLogger(SimpleFlowerFactory.class);

  // <flowName, ServiceFlow>
  private static final ConcurrentMap<String, ServiceFlow> serviceFlows = new ConcurrentHashMap<>();
  private static FlowerFactory instance;



  private FlowerConfig flowerConfig;
  private Set<Registry> registries = new HashSet<>();
  private ExceptionHandler exceptionHandler;
  private ServiceActorFactory serviceActorFactory;

  public SimpleFlowerFactory() {}

  public static FlowerFactory get() {
    if (instance == null) {
      synchronized (logger) {
        if (instance == null) {
          instance = new SimpleFlowerFactory();
          instance.init();
        }
      }
    }
    return instance;
  }

  @Override
  public boolean init() {
    initFlowerConfig();
    initRegistryFactories();
    scanFlowerTypes();
    this.serviceActorFactory = new ServiceActorFactory(this);
    return true;
  }

  private void initRegistryFactories() {
    Set<RegistryConfig> registryConfigs = getFlowerConfig().getRegistry();
    if (registryConfigs == null) {
      return;
    }

    for (RegistryConfig config : registryConfigs) {
      RegistryFactory registryFactory = ExtensionLoader.load(RegistryFactory.class).load(config.getProtocol());
      if (registryFactory != null) {
        URL url = new URL(config.getProtocol(), config.getHost(), config.getPort());
        this.registries.add(registryFactory.createRegistry(url));
      }
    }

  }

  private void initFlowerConfig() {
    this.flowerConfig = new FlowerConfigParser().parse();
    logger.info("load flower config : {}", flowerConfig);
  }

  protected void scanFlowerTypes() {
    String basePackage = getFlowerConfig().getBasePackage();
    if (StringUtil.isBlank(basePackage)) {
      return;
    }
    Reflections reflections = new Reflections(basePackage);
    Set<Class<?>> flowers =
        reflections.getTypesAnnotatedWith(com.ly.train.flower.common.annotation.FlowerService.class);
    logger.info("scan flowerService, basePackage : {}, find flowerService : {}", basePackage, flowers.size());
    for (Class<?> clazz : flowers) {
      ServiceLoader.getInstance().registerServiceType(FlowerServiceUtil.getServiceName(clazz), clazz);
    }

  }

  @Override
  public FlowerConfig getFlowerConfig() {
    return flowerConfig;
  }

  @Override
  public Set<Registry> getRegistry() {
    return registries;
  }

  @Override
  public ServiceFlow getOrCreateServiceFlow(String flowName) {
    Assert.notNull(flowName, "flowName can't be null !");
    ServiceFlow serviceFlow = serviceFlows.get(flowName);
    if (serviceFlow == null) {
      serviceFlow = new ServiceFlow(flowName);
      serviceFlows.putIfAbsent(flowName, serviceFlow);
    }
    return serviceFlow;
  }


  @Override
  public ExceptionHandler getExceptionHandler() {
    if (this.exceptionHandler == null) {
      this.exceptionHandler = new ExceptionHandler();
    }
    return exceptionHandler;
  }

  @Override
  public ServiceActorFactory getServiceActorFactory() {
    return serviceActorFactory;
  }
}
