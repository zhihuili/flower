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

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.web.spring.container.SpringFlowerFactory;

/**
 * @author leeyazhou
 */
@Configuration
@EnableConfigurationProperties(FlowerProperties.class)
public class FlowerAutoConfiguration {
  private static final Logger logger = LoggerFactory.getLogger(FlowerAutoConfiguration.class);


  @Bean
  @ConditionalOnMissingBean
  public FlowerFactory flowerFactory(FlowerProperties flowerProperties) {
    logger.info("flower auto configure.");
    FlowerFactory factory = new SpringFlowerFactory(flowerProperties.getConfig());
    return factory;
  }

}
