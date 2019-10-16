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
package com.ly.train.flower.web.spring.context;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import com.ly.train.flower.common.annotation.Aggregate;
import com.ly.train.flower.common.annotation.FlowerService;

/**
 * @author leeyazhou
 * 
 */
public class FlowerBeanRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
    ResourceLoaderAware, BeanClassLoaderAware, InitializingBean {
  static final Logger logger = LoggerFactory.getLogger(FlowerBeanRegistryPostProcessor.class);
  private Environment environment;
  protected ClassLoader classLoader;
  private ResourceLoader resourceLoader;
  private Set<String> packagesToScan;

  public FlowerBeanRegistryPostProcessor(Set<String> packagesToScan) {
    this.packagesToScan = packagesToScan;
  }

  public FlowerBeanRegistryPostProcessor(String... packagesToScan) {
    this(Arrays.asList(packagesToScan));
  }

  public void setPackagesToScan(Set<String> packagesToScan) {
    this.packagesToScan = packagesToScan;
  }

  public FlowerBeanRegistryPostProcessor(Collection<String> packagesToScan) {
    this(new LinkedHashSet<String>(packagesToScan));
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

  }

  @Override
  public void setBeanClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    Set<String> serviceTypes = new HashSet<>();
    Set<String> aggregateTypes = new HashSet<>();
    FlowerClassPathBeanDefinitionScanner scanner =
        new FlowerClassPathBeanDefinitionScanner(registry, environment, resourceLoader);
    scanner.addIncludeFilter(new AnnotationTypeFilter(FlowerService.class));
    scanner.addIncludeFilter(new AnnotationTypeFilter(Aggregate.class));

    for (String basePackage : packagesToScan) {
      Set<BeanDefinitionHolder> beanDefinitionHolders = scanner.doScan(basePackage);
      logger.info("scan basepackage ：{}", basePackage);
      for (BeanDefinitionHolder beanHolder : beanDefinitionHolders) {
        ScannedGenericBeanDefinition beanDefinition = (ScannedGenericBeanDefinition) beanHolder.getBeanDefinition();
        Set<String> annotationsSet = beanDefinition.getMetadata().getAnnotationTypes();
        String beanName = beanHolder.getBeanName();
        if (!registry.containsBeanDefinition(beanName)) {
          registry.registerBeanDefinition(beanName, beanHolder.getBeanDefinition());
          if (annotationsSet.contains(FlowerService.class.getName())) {
            serviceTypes.add(beanDefinition.getBeanClassName());
          }
          if (annotationsSet.contains(Aggregate.class.getName())) {
            aggregateTypes.add(beanDefinition.getBeanClassName());
          }
        }
      }
    }
    BeanDefinition beanDefinition = registry.getBeanDefinition("flowerFactory");
    beanDefinition.getPropertyValues().add("serviceTypes", serviceTypes);

    final String dddFactoryName = "dddFactory";
    if (registry.containsBeanDefinition(dddFactoryName)) {
      BeanDefinition dddFactoryBeanDefinition = registry.getBeanDefinition(dddFactoryName);
      dddFactoryBeanDefinition.getPropertyValues().add("aggregateTypes", aggregateTypes);
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {}

}
