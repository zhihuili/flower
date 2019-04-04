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
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.config.parser.FlowerConfigParser;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import com.ly.train.flower.registry.Registry;
import com.ly.train.flower.registry.RegistryFactory;
import com.ly.train.flower.registry.config.RegistryConfig;
import com.ly.train.flower.registry.simple.SimpleRegistryFactory;
import com.ly.train.flower.registry.zookeeper.ZookeeperRegistryFactory;

/**
 * @author leeyazhou
 *
 */
public class SimpleFlowerFactory implements FlowerFactory {
  private static final Logger logger = LoggerFactory.getLogger(SimpleFlowerFactory.class);
  private static FlowerFactory instance;

  public SimpleFlowerFactory() {}

  private FlowerConfig flowerConfig;
  private Set<Registry> registries = new HashSet<>();

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
    return false;
  }

  private void initRegistryFactories() {
    Set<RegistryConfig> registryConfigs = getFlowerConfig().getRegistry();
    if (registryConfigs != null) {
      for (RegistryConfig config : registryConfigs) {
        RegistryFactory registryFactory = null;
        if ("flower".equalsIgnoreCase(config.getProtocol())) {
          registryFactory = new SimpleRegistryFactory();
        } else if ("zookeeper".equalsIgnoreCase(config.getProtocol())) {
          registryFactory = new ZookeeperRegistryFactory();
        }

        if (registryFactory != null) {
          URL url = new URL(config.getProtocol(), config.getHost(), config.getPort());
          this.registries.add(registryFactory.createRegistry(url));
        }
      }
    }
  }

  private void initFlowerConfig() {
    if (flowerConfig == null) {
      synchronized (this) {
        if (flowerConfig == null) {
          flowerConfig = new FlowerConfigParser().parse();
          logger.info("load flower config : {}", flowerConfig);
        }
      }
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
}
