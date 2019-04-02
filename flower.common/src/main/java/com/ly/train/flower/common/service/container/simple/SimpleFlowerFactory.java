/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
