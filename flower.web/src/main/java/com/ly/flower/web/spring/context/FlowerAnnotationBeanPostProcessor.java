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
package com.ly.flower.web.spring.context;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import com.ly.train.flower.common.annotation.FlowerService;

/**
 * @author leeyazhou
 *
 */
public class FlowerAnnotationBeanPostProcessor extends AutowiredAnnotationBeanPostProcessor {


  public FlowerAnnotationBeanPostProcessor() {
    Set<Class<? extends Annotation>> autowiredAnnotationType = new HashSet<>();
    autowiredAnnotationType.add(FlowerService.class);
    setAutowiredAnnotationTypes(autowiredAnnotationType);
  }

  @Override
  public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
  }

}
