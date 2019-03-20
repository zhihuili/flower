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
