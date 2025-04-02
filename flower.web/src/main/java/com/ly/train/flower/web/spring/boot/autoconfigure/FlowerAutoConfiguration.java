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
package com.ly.train.flower.web.spring.boot.autoconfigure;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.fastjson2.JSON;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.config.RegistryConfig;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.web.spring.container.SpringFlowerFactory;
import com.ly.train.flower.web.spring.context.FlowerAnnotationBeanPostProcessor;

/**
 * @author leeyazhou
 */
@Configuration
@EnableConfigurationProperties(FlowerProperties.class)
@ConditionalOnClass(value = SpringFlowerFactory.class)
@ConditionalOnMissingBean(value = FlowerFactory.class)
public class FlowerAutoConfiguration {
  private static final Logger logger = LoggerFactory.getLogger(FlowerAutoConfiguration.class);

  @Bean
  @ConditionalOnMissingBean
  public FlowerAnnotationBeanPostProcessor flowerAnnotationBeanPostProcessor() {
    return new FlowerAnnotationBeanPostProcessor();
  }

  @Bean
  @ConditionalOnMissingBean
  public FlowerFactory flowerFactory(FlowerProperties properties) {
    logger.info("flower auto configure FlowerFactory. config : " + JSON.toJSONString(properties));
    FlowerConfig flowerConfig = properties.getConfig();
    if (flowerConfig == null) {
      flowerConfig = new FlowerConfig();
    }
    Set<RegistryConfig> registry = buildRegistryConfig(properties.getRegistry());
    flowerConfig.setRegistry(registry);
    FlowerFactory factory = new SpringFlowerFactory(flowerConfig);
    return factory;
  }

  private Set<RegistryConfig> buildRegistryConfig(Set<String> registryStr) {
    Set<RegistryConfig> ret = new HashSet<>();
    if (registryStr != null) {
      for (String registry : registryStr) {
        RegistryConfig item = new RegistryConfig();
        item.setUrl(registry);
        ret.add(item);
      }
    }
    return ret;
  }

}
