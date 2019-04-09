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
import java.util.concurrent.atomic.AtomicBoolean;
import com.ly.train.flower.common.akka.ServiceActorFactory;
import com.ly.train.flower.common.akka.ServiceFacade;
import com.ly.train.flower.common.exception.ExceptionHandler;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.service.container.lifecyle.AbstractLifecycle;
import com.ly.train.flower.common.util.ExtensionLoader;
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
public class SimpleFlowerFactory extends AbstractLifecycle implements FlowerFactory {
  private static final Logger logger = LoggerFactory.getLogger(SimpleFlowerFactory.class);

  private static volatile FlowerFactory instance;
  private FlowerConfig flowerConfig;
  private volatile Set<Registry> registries;
  private ExceptionHandler exceptionHandler;
  private volatile ServiceActorFactory serviceActorFactory;
  private volatile ServiceFactory serviceFactory;
  private volatile ServiceFacade serviceFacade;
  private volatile AtomicBoolean init = new AtomicBoolean();
  private String configLocation;

  public SimpleFlowerFactory() {}

  public SimpleFlowerFactory(String configLocation) {
    this.configLocation = configLocation;
  }

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
  public void init() {
    if (init.compareAndSet(false, true)) {
      getServiceActorFactory();
      getServiceFactory();
      getFlowerConfig();
    }
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
        URL url = new URL(config.getProtocol(), config.getHost(), config.getPort());
        ret.add(registryFactory.createRegistry(url));
        logger.info("find registry : {}", url);
      }
    }
    return ret;
  }



  @Override
  public FlowerConfig getFlowerConfig() {
    if (flowerConfig == null) {
      synchronized (this) {
        if (flowerConfig == null) {
          this.flowerConfig = new FlowerConfigParser(configLocation).parse();
        }
      }
    }
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
  public ExceptionHandler getExceptionHandler() {
    if (this.exceptionHandler == null) {
      this.exceptionHandler = new ExceptionHandler();
    }
    return exceptionHandler;
  }

  @Override
  public ServiceActorFactory getServiceActorFactory() {
    if (serviceActorFactory == null) {
      synchronized (this) {
        this.serviceActorFactory = new ServiceActorFactory(this);
        this.serviceActorFactory.start();
      }
    }
    return serviceActorFactory;
  }

  @Override
  public void doStart() {
    logger.info("start flower factory");
  }

  @Override
  public void doStop() {
    logger.info("stop flower factory");
  }

  @Override
  public ServiceFactory getServiceFactory() {
    if (serviceFactory == null) {
      synchronized (this) {
        if (serviceFactory == null) {
          this.serviceFactory = new ServiceFactory(this);
          this.serviceFactory.init();
        }
      }
    }
    return serviceFactory;
  }

  @Override
  public ServiceFacade getServiceFacade() {
    if (serviceFacade == null) {
      synchronized (this) {
        this.serviceFacade = new ServiceFacade(this);
      }
    }
    return serviceFacade;
  }
}
