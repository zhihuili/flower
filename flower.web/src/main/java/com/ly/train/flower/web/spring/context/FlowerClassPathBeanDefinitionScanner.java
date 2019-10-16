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

import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 * 
 * @author leeyazhou
 * 
 */
public class FlowerClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

  private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

  private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

  public FlowerClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
      Environment environment, ResourceLoader resourceLoader) {
    super(registry, useDefaultFilters);
    setEnvironment(environment);
    setResourceLoader(resourceLoader);
    AnnotationConfigUtils.registerAnnotationConfigProcessors(registry);
  }

  public FlowerClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, Environment environment,
      ResourceLoader resourceLoader) {
    this(registry, false, environment, resourceLoader);
  }

  public Set<BeanDefinitionHolder> doScan(String... basePackages) {
    Assert.notEmpty(basePackages, "At least one base package must be specified");
    Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
    for (String basePackage : basePackages) {
      Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
      for (BeanDefinition candidate : candidates) {
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
        candidate.setScope(scopeMetadata.getScopeName());
        String beanName = this.beanNameGenerator.generateBeanName(candidate, this.getRegistry());
        if (candidate instanceof AbstractBeanDefinition) {
          postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
        }
        if (candidate instanceof AnnotatedBeanDefinition) {
          AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
        }
        if (checkCandidate(beanName, candidate)) {
          BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
          beanDefinitions.add(definitionHolder);
        }
      }
    }
    return beanDefinitions;
  }

}
