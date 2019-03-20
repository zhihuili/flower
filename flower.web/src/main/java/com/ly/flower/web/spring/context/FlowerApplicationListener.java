/**
 * 
 */
package com.ly.flower.web.spring.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotatedElementUtils;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public class FlowerApplicationListener implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {
  static final Logger logger = LoggerFactory.getLogger(FlowerApplicationListener.class);
  private ApplicationContext applicationContext;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {

    String[] beanNames = applicationContext.getBeanDefinitionNames();
    for (String beanName : beanNames) {
      Class<?> beanType = applicationContext.getType(beanName);
      if (isFlowerService(beanType)) {
        com.ly.train.flower.common.service.FlowerService flowerService =
            (com.ly.train.flower.common.service.FlowerService) applicationContext.getBean(beanName);
        FlowerService flowerService2 = beanType.getAnnotation(FlowerService.class);
        String serviceName = beanType.getSimpleName();
        if (flowerService2 != null && StringUtil.isNotBlank(flowerService2.value())) {
          serviceName = flowerService2.value();
        }
        ServiceFactory.registerFlowerService(serviceName, flowerService);
        // logger.info("注入实例 : " + flowerService);
      }
    }

  }

  private boolean isFlowerService(Class<?> beanType) {
    return AnnotatedElementUtils.hasAnnotation(beanType, FlowerService.class);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

}
