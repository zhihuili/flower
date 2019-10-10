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
package com.ly.train.flower.ddd.factory;

import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import com.ly.train.flower.common.annotation.FlowerServiceUtil;
import com.ly.train.flower.common.core.service.FlowerService;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.ddd.config.DDDConfig;

/**
 * @author leeyazhou
 */
public class DDDFactory
    implements ApplicationContextAware, InitializingBean, ApplicationListener<ContextRefreshedEvent> {
  private static final Logger logger = LoggerFactory.getLogger(DDDFactory.class);
  private DDDConfig dddConfig;
  private FlowerFactory flowerFactory;
  private Set<String> aggregateTypes;
  private Set<String> serviceTypes;
  private ApplicationContext applicationContext;

  public DDDFactory(FlowerFactory flowerFactory, DDDConfig dddConfig) {
    this.flowerFactory = flowerFactory;
    this.dddConfig = dddConfig;
  }


  public void setAggregateTypes(Set<String> aggregateTypes) {
    this.aggregateTypes = aggregateTypes;
  }

  public void setServiceTypes(Set<String> serviceTypes) {
    this.serviceTypes = serviceTypes;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (serviceTypes != null) {
      for (String serviceType : serviceTypes) {
        Class<?> serviceClass = Class.forName(serviceType);
        String serviceName = FlowerServiceUtil.getServiceName(serviceClass);
        flowerFactory.getServiceFactory().registerService(serviceName, serviceClass);
      }
    }
    if (aggregateTypes != null) {
      for (String type : aggregateTypes) {
        Class<?> clazz = Class.forName(type);
        dddConfig.dealHandlers(clazz);
      }
    }
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (serviceTypes != null) {
      for (String serviceType : serviceTypes) {
        try {
          Class<?> serviceClass = Class.forName(serviceType);
          String serviceName = FlowerServiceUtil.getServiceName(serviceClass);
          FlowerService flowerService = (FlowerService) applicationContext.getBean(serviceClass);
          flowerFactory.getServiceFactory().registerFlowerService(serviceName, flowerService);
        } catch (ClassNotFoundException e) {
          logger.error("", e);
        }
      }
    }
  }
}
