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
package com.ly.train.flower.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public abstract class Bootstrap {


  public static void main(String[] args) {
    String mainClass = "com.ly.train.flower.container.FlowerBootstrap";
    final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    logger.info("flower start class : {}", mainClass);
    try {
      @SuppressWarnings("unchecked")
      Class<Bootstrap> bootstrapClass = (Class<Bootstrap>) Class.forName(mainClass);
      Bootstrap bootstrap = bootstrapClass.newInstance();
      bootstrap.startup(args);
    } catch (Throwable e) {
      logger.error("fail to start flower container.", e);
      e.printStackTrace();
    }
  }

  private void startup(String[] args) throws Throwable {
    doStartup();
  }

  /**
   * 启动flower container
   */
  public abstract void doStartup() throws Throwable;
}
