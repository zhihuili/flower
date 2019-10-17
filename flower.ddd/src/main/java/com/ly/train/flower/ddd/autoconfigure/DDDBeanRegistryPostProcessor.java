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
package com.ly.train.flower.ddd.autoconfigure;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import com.ly.train.flower.common.core.service.Service;
import com.ly.train.flower.ddd.gateway.impl.DefaultCommandGateway;
import com.ly.train.flower.ddd.gateway.impl.DefaultQueryGateway;
import com.ly.train.flower.ddd.service.CommandHandlerService;
import com.ly.train.flower.ddd.service.DDDEndService;
import com.ly.train.flower.ddd.service.DDDStartService;
import com.ly.train.flower.ddd.service.EventHandlerService;
import com.ly.train.flower.ddd.service.QueryHandlerService;

/**
 * @author leeyazhou
 * 
 */
public class DDDBeanRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
  static final Logger logger = LoggerFactory.getLogger(DDDBeanRegistryPostProcessor.class);

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    Set<String> serviceTypes = new HashSet<>();
    Class<?> services[] =
        new Class<?>[] {CommandHandlerService.class, EventHandlerService.class, QueryHandlerService.class,
            DDDStartService.class, DDDEndService.class, DefaultCommandGateway.class, DefaultQueryGateway.class};

    for (Class<?> service : services) {
      BeanDefinition beanDefinition = new RootBeanDefinition(service);
      String handlerServiceName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, registry);
      registry.registerBeanDefinition(handlerServiceName, beanDefinition);
      if (Service.class.isAssignableFrom(service)) {
        serviceTypes.add(service.getName());
      }
    }

    final String dddFactoryName = "dddFactory";
    if (registry.containsBeanDefinition(dddFactoryName)) {
      BeanDefinition dddFactoryBeanDefinition = registry.getBeanDefinition(dddFactoryName);
      dddFactoryBeanDefinition.getPropertyValues().add("serviceTypes", serviceTypes);
    }

  }


}
