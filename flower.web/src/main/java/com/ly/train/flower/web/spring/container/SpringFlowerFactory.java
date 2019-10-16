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
package com.ly.train.flower.web.spring.container;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.ly.train.flower.common.annotation.FlowerServiceUtil;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.core.service.container.simple.SimpleFlowerFactory;

/**
 * spring flower factory
 * 
 * @author leeyazhou
 */
public class SpringFlowerFactory extends SimpleFlowerFactory implements InitializingBean, ApplicationContextAware {
  private Set<String> serviceTypes = new HashSet<>();
  ApplicationContext applicationContext;

  public SpringFlowerFactory() {
    super();
  }

  public SpringFlowerFactory(String configLocation) {
    super(configLocation);
  }

  public SpringFlowerFactory(FlowerConfig flowerConfig) {
    super(flowerConfig);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    init();
    if (serviceTypes == null) {
      return;
    }
    for (String beanTypeStr : serviceTypes) {
      Class<?> beanType = Class.forName(beanTypeStr);
      String serviceName = FlowerServiceUtil.getServiceName(beanType);
      getServiceFactory().registerService(serviceName, beanType);
    }
  }

  public void setServiceTypes(Set<String> serviceTypes) {
    this.serviceTypes = serviceTypes;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

}
