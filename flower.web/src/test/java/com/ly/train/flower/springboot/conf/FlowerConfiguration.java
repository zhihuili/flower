package com.ly.train.flower.springboot.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.web.spring.container.SpringFlowerFactory;

/**
 * @author leeyazhou
 */
@Configuration
public class FlowerConfiguration {


  @Bean
  public FlowerFactory flowerFactory() {
    return new SpringFlowerFactory();
  }
}
