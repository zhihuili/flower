package com.ly.train.flower.common.sample.aggregate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.service.container.SpringFlowerFactory;

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
