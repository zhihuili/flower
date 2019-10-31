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
package com.ly.train.flower.core.service.container.simple;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.exception.handler.ExceptionHandler;
import com.ly.train.flower.common.exception.handler.ExceptionHandlerManager;
import com.ly.train.flower.common.lifecyle.AbstractLifecycle;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.common.util.FlowerVersion;
import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.config.RegistryConfig;
import com.ly.train.flower.config.parser.FlowerConfigParser;
import com.ly.train.flower.core.akka.ActorFactory;
import com.ly.train.flower.core.akka.ServiceActorFactory;
import com.ly.train.flower.core.akka.ServiceFacade;
import com.ly.train.flower.core.akka.router.FlowRouter;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.core.service.container.ServiceFactory;
import com.ly.train.flower.registry.Registry;
import com.ly.train.flower.registry.RegistryFactory;

/**
 * @author leeyazhou
 * 
 */
public class SimpleFlowerFactory extends AbstractLifecycle implements FlowerFactory {
  private static final Logger logger = LoggerFactory.getLogger(SimpleFlowerFactory.class);

  private static volatile FlowerFactory instance;
  private FlowerConfig flowerConfig;
  private volatile Set<Registry> registries;
  private volatile ActorFactory actorFactory;
  private volatile ServiceFactory serviceFactory;
  private volatile ServiceFacade serviceFacade;
  private String configLocation = "flower.yml";
  private ExceptionHandlerManager exceptionHandlerManager = ExceptionHandlerManager.getInstance();

  public SimpleFlowerFactory() {
    newFlowerFactory();
  }

  public SimpleFlowerFactory(String configLocation) {
    this.configLocation = configLocation;
    newFlowerFactory();
  }

  public SimpleFlowerFactory(FlowerConfig flowerConfig) {
    this.flowerConfig = flowerConfig;
    newFlowerFactory();
  }
  
  private void newFlowerFactory() {
    this.actorFactory = new ServiceActorFactory(this);
    this.serviceFactory = new ServiceFactory(this);
    this.serviceFacade = new ServiceFacade(this);
    this.start();
  }

  public static FlowerFactory get() {
    if (instance == null) {
      synchronized (logger) {
        if (instance == null) {
          FlowerFactory temp = new SimpleFlowerFactory();
          temp.start();
          SimpleFlowerFactory.instance = temp;
        }
      }
    }
    return instance;
  }

  @Override
  protected void doInit() {
    FlowerVersion.logVersionInfo();
    if (this.flowerConfig == null) {
      this.flowerConfig = new FlowerConfigParser(this.configLocation).parse();
    }
    this.actorFactory.init();
    this.serviceFactory.init();
  }

  private Set<Registry> initRegistryFactories() {
    Set<Registry> ret = new HashSet<Registry>();
    Set<RegistryConfig> registryConfigs = getFlowerConfig().getRegistry();
    if (registryConfigs == null) {
      return ret;
    }
    for (RegistryConfig config : registryConfigs) {
      RegistryFactory registryFactory = ExtensionLoader.load(RegistryFactory.class).load(config.getProtocol());
      if (registryFactory != null) {
        URL url = config.toURL();
        Registry registry = registryFactory.createRegistry(url);
        if (registry.getClass().getName().equals("com.ly.train.flower.registry.simple.SimpleRegistry")) {
          setFlowerFactory(registry);
        }
        ret.add(registry);
        logger.info("find registry : {}", url);
      }
    }
    return ret;
  }

  private void setFlowerFactory(Registry registry) {
    Method method;
    try {
      method = registry.getClass().getMethod("setFlowerFactory", FlowerFactory.class);
      method.invoke(registry, this);
    } catch (Exception e) {
      throw new FlowerException(e);
    }
  }



  @Override
  public FlowerConfig getFlowerConfig() {
    return flowerConfig;
  }

  @Override
  public Set<Registry> getRegistry() {
    if (registries == null) {
      synchronized (this) {
        if (registries == null) {
          this.registries = initRegistryFactories();
        }
      }
    }
    return registries;
  }

  @Override
  public ActorFactory getActorFactory() {
    return actorFactory;
  }


  @Override
  public void doStart() {
    init();
    logger.info("do start FlowerFactory");
    this.actorFactory.start();
    this.serviceFactory.start();
  }

  @Override
  public void doStop() {
    logger.info("do stop FlowerFactory");
    this.actorFactory.stop();
    this.serviceFactory.stop();
  }

  @Override
  public ServiceFactory getServiceFactory() {
    return serviceFactory;
  }

  @Override
  public ServiceFacade getServiceFacade() {
    return serviceFacade;
  }

  @Override
  public void registerExceptionHandler(Class<? extends Throwable> exceptionClass, ExceptionHandler exceptionHandler) {
    exceptionHandlerManager.registerHandler(exceptionClass, exceptionHandler);
  }

  @Override
  public void setDefaultExceptionHandler(ExceptionHandler defaultExceptionHandler) {
    exceptionHandlerManager.setDefaultExceptionHandler(defaultExceptionHandler);
  }

  @Override
  public FlowRouter buildFlowRouter(String flowName, int flowNumber) {
    return getActorFactory().buildFlowRouter(flowName, flowNumber);
  }
}
