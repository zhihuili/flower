/**
 * 
 */
package com.ly.flower.center;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author leeyazhou
 *
 */
@SpringBootApplication
@EnableAutoConfiguration
public class CenterApplication {
  static final Logger logger = LoggerFactory.getLogger(CenterApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(CenterApplication.class, args);
  }

  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {

      logger.info("Let's inspect the beans provided by Spring Boot:");

      String[] beanNames = ctx.getBeanDefinitionNames();
      Arrays.sort(beanNames);
      for (String beanName : beanNames) {
        logger.debug("注入实例 : {}", beanName);
      }

    };
  }
}
