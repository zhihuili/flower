package com.ly.train.flower.ddd.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.ddd.factory.DDDFactory;
import com.ly.train.flower.ddd.gateway.CommandGateway;
import com.ly.train.flower.ddd.gateway.QueryGateway;
import com.ly.train.flower.web.spring.boot.autoconfigure.FlowerProperties;

/**
 * @author leeyazhou
 */
@Configuration
public class DDDConfiguration {
  private static final Logger logger = LoggerFactory.getLogger(DDDConfiguration.class);
  @Bean
  @ConditionalOnMissingBean
  public DDDFactory dddFactory(FlowerFactory flowerFactory, FlowerProperties flowerProperties) {
    logger.info("auto confige " + DDDFactory.class);
    return new DDDFactory(flowerFactory, flowerProperties.getConfig().getBasePackage());
  }

  @Bean
  @ConditionalOnMissingBean
  public CommandGateway commandGateway(DDDFactory dddFactory) {
    logger.info("auto confige {}" , dddFactory.getCommandGateway());
    return dddFactory.getCommandGateway();
  }

  @Bean
  @ConditionalOnMissingBean
  public QueryGateway queryGateway(DDDFactory dddFactory) {
    return dddFactory.getQueryGateway();
  }
}
