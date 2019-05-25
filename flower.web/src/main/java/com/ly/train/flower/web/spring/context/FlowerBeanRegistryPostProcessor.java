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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import com.ly.train.flower.common.annotation.Flower;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.annotation.FlowerServiceUtil;
import com.ly.train.flower.common.service.container.FlowerFactory;

/**
 * @author leeyazhou
 * 
 */
public class FlowerBeanRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
    ResourceLoaderAware, BeanClassLoaderAware, ApplicationContextAware, InitializingBean {
  static final Logger logger = LoggerFactory.getLogger(FlowerBeanRegistryPostProcessor.class);
  private Environment environment;
  protected ClassLoader classLoader;
  private ResourceLoader resourceLoader;
  private ApplicationContext applicationContext;

  private Set<String> packagesToScan;


  public FlowerBeanRegistryPostProcessor() {}

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
    RootBeanDefinition flowerAnnotationBeanPostProcessor =
        new RootBeanDefinition(FlowerAnnotationBeanPostProcessor.class);
    BeanDefinitionReaderUtils.registerWithGeneratedName(flowerAnnotationBeanPostProcessor, registry);
    BeanDefinitionReaderUtils.registerWithGeneratedName(new RootBeanDefinition(FlowerApplicationListener.class),
        registry);

    FlowerClassPathBeanDefinitionScanner scanner =
        new FlowerClassPathBeanDefinitionScanner(registry, environment, resourceLoader);

    scanner.addIncludeFilter(new AnnotationTypeFilter(FlowerService.class));
    Map<String, Class<?>> serviceTypes = new HashMap<String, Class<?>>();
    for (String basePackage : packagesToScan) {
      Set<BeanDefinitionHolder> beanDefinitionHolders = scanner.doScan(basePackage);
      logger.info("scan basepackage ：{}", basePackage);
      for (BeanDefinitionHolder beanHolder : beanDefinitionHolders) {
        String beanName = beanHolder.getBeanName();
        if (!registry.containsBeanDefinition(beanName)) {
          registry.registerBeanDefinition(beanName, beanHolder.getBeanDefinition());
          Class<?> beanType = applicationContext.getType(beanName);
          if (isFlowerService(beanType)) {
            String serviceName = FlowerServiceUtil.getServiceName(beanType);
            serviceTypes.put(serviceName, beanType);
          }
        }
      }
    }

    FlowerFactory flowerFactory = applicationContext.getBean(FlowerFactory.class);
    for (Map.Entry<String, Class<?>> entry : serviceTypes.entrySet()) {
      flowerFactory.getServiceFactory().registerService(entry.getKey(), entry.getValue());
    }

    scanner.resetFilters(false);
    scanner.addIncludeFilter(new AnnotationTypeFilter(Flower.class));
    // for (String basePackage : packagesToScan) {
    // Set<BeanDefinitionHolder> beanDefinitionHolders =
    // scanner.doScan(basePackage);
    // logger.info("scan basepackage ：{}", basePackage);
    // for (BeanDefinitionHolder beanHolder : beanDefinitionHolders) {
    // AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition)
    // beanHolder.getBeanDefinition();
    // BeanDefinitionReaderUtils.registerWithGeneratedName((AbstractBeanDefinition)
    // beanHolder.getBeanDefinition(),
    // registry);
    // Class<?> clazz = generateControllerClass(beanHolder);
    // beanDefinition = new RootBeanDefinition(clazz);
    // beanDefinition.getPropertyValues().add("target", new
    // RuntimeBeanReference(beanHolder.getBeanName()));
    // BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition,
    // registry);
    // logger.info("registry flower controller : {}", clazz);
    // }
    // }
  }


  private boolean isFlowerService(Class<?> beanType) {
    return AnnotatedElementUtils.hasAnnotation(beanType, FlowerService.class);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  // protected Class<?> generateControllerClass(BeanDefinitionHolder beanHolder) {
  // Class<?> originClass = null;
  // try {
  // originClass =
  // Class.forName(beanHolder.getBeanDefinition().getBeanClassName());
  // } catch (ClassNotFoundException e1) {
  // e1.printStackTrace();
  // }
  // ClassGenerator cg = ClassGenerator.newInstance();
  // cg.setClassName(beanHolder.getBeanDefinition().getBeanClassName() +
  // "Controller");
  //
  // BindController bindController =
  // originClass.getAnnotation(BindController.class);
  // Map<String, Object> controllerMap = new HashMap<>();
  // if (bindController != null) {
  // logger.info("添加注解: " + bindController);
  // controllerMap.put("value", bindController.path());
  // controllerMap.put("method", bindController.method());
  // }
  // cg.addAnnotation(RestController.class.getName(), controllerMap);
  //
  // cg.addField("public " + originClass.getName() + " target;");
  // cg.addMethod("public void setTarget(" + originClass.getName() + "
  // target){this.target = $1;}");
  //
  // try {
  // for (Method m : originClass.getDeclaredMethods()) {
  // logger.info("添加方法:{}, originClass: {}", m, originClass);
  // // cg.addMethod(m, m.getAnnotations());
  // }
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // cg.addDefaultConstructor();
  // // cg.addAnnotation(FlowerService.class.getName(), null);
  //
  //
  // Class<?> cl = cg.toClass();
  // return cl;
  // }

  @Override
  public void afterPropertiesSet() throws Exception {}

}
