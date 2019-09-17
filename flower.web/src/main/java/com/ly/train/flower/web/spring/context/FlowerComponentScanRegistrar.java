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
package com.ly.train.flower.web.spring.context;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * flower {@link FlowerComponentScan} Bean Registrar
 * 
 */
public class FlowerComponentScanRegistrar implements ImportBeanDefinitionRegistrar {

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

    Set<String> packagesToScan = getPackagesToScan(importingClassMetadata);


    registerReferenceAnnotationBeanPostProcessor(registry, packagesToScan);
  }

  /**
   * 
   * @param registry {@link BeanDefinitionRegistry}
   */
  private void registerReferenceAnnotationBeanPostProcessor(BeanDefinitionRegistry registry,
      Set<String> packagesToScan) {
    registerInfrastructureBean(registry, FlowerBeanRegistryPostProcessor.class.getSimpleName(),
        FlowerBeanRegistryPostProcessor.class, packagesToScan);
  }

  public static void registerInfrastructureBean(BeanDefinitionRegistry beanDefinitionRegistry, String beanName,
      Class<?> beanType, Set<String> packagesToScan) {
    if (!beanDefinitionRegistry.containsBeanDefinition(beanName)) {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(beanType);
      beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(packagesToScan);
      beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
      beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
    }

  }

  private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
    AnnotationAttributes attributes =
        AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(FlowerComponentScan.class.getName()));
    String[] basePackages = attributes.getStringArray("basePackages");
    Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
    String[] value = attributes.getStringArray("value");
    // Appends value array attributes
    Set<String> packagesToScan = new LinkedHashSet<String>(Arrays.asList(value));
    packagesToScan.addAll(Arrays.asList(basePackages));
    for (Class<?> basePackageClass : basePackageClasses) {
      packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
    }
    if (packagesToScan.isEmpty()) {
      return Collections.singleton(ClassUtils.getPackageName(metadata.getClassName()));
    }
    return packagesToScan;
  }

}
