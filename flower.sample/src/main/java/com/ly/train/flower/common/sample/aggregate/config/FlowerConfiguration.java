package com.ly.train.flower.common.sample.aggregate.config;

import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.SpringFlowerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengyu.zhang
 *
 */
@Configuration
public class FlowerConfiguration {

  @Bean
  public FlowerFactory flowerFactory() {
    return new SpringFlowerFactory();
  }
}
