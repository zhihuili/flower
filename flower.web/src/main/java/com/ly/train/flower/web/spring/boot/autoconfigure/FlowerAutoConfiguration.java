package com.ly.train.flower.web.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.web.spring.container.SpringFlowerFactory;

/**
 * @author leeyazhou
 */
@Configuration
@EnableConfigurationProperties(FlowerProperties.class)
public class FlowerAutoConfiguration {
  private static final Logger logger = LoggerFactory.getLogger(FlowerAutoConfiguration.class);


  @Bean
  @ConditionalOnMissingBean
  public FlowerFactory flowerFactory(FlowerProperties flowerProperties) {
    logger.info("flower auto configure.");
    FlowerFactory factory = new SpringFlowerFactory(flowerProperties.getConfig());
    return factory;
  }

}
