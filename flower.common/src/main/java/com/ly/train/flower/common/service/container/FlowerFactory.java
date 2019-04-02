/**
 * 
 */
package com.ly.train.flower.common.service.container;

import com.ly.train.flower.config.FlowerConfig;

/**
 * @author leeyazhou
 *
 */
public interface FlowerFactory {


  /**
   * 获取Flower容器配置信息
   * 
   * @return {@link FlowerConfig}
   */
  FlowerConfig getFlowerConfig();

}
