/**
 * 
 */
package com.ly.train.flower.common.service.container.simple;

import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.config.parser.FlowerConfigParser;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public class SimpleFlowerFactory implements FlowerFactory {
  private static final Logger logger = LoggerFactory.getLogger(SimpleFlowerFactory.class);
  private FlowerConfig flowerConfig;

  public SimpleFlowerFactory() {}

  @Override
  public FlowerConfig getFlowerConfig() {
    if (flowerConfig == null) {
      synchronized (this) {
        if (flowerConfig == null) {
          flowerConfig = new FlowerConfigParser().parse();
          logger.info("load flower config : {}", flowerConfig);
        }
      }
    }
    return flowerConfig;
  }
}
