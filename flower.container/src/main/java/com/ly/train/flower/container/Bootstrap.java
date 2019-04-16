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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.ly.train.flower.common.loader.FlowerClassLoader;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public abstract class Bootstrap {

  private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

  public static void main(String[] args) {
    String mainClass = "com.ly.train.flower.container.SpringBootstrap";
    logger.info("flower start class : {}", mainClass);
    try {
      String flowerHome = System.getProperty(Constant.flowerHome);
      ClassLoader classLoader = createClassLoader(flowerHome);
      @SuppressWarnings("unchecked")
      Class<Bootstrap> bootstrapClass = (Class<Bootstrap>) Class.forName(mainClass, true, classLoader);
      Bootstrap bootstrap = bootstrapClass.newInstance();

      String configLocation = System.getProperty(Constant.springConfigLocationKey);
      bootstrap.startup(configLocation);
    } catch (Throwable e) {
      logger.error("fail to start flower container.", e);
      e.printStackTrace();
    }
  }

  private static ClassLoader createClassLoader(String flowerHome) {
    String libs = flowerHome + "/libs/";
    List<URL> urls = new ArrayList<URL>();
    File file = new File(libs);
    try {
      if (file.exists()) {
        for (String f : file.list()) {
          urls.add(new URL("file:" + libs + f));
        }
      }
    } catch (Exception e) {
      logger.error("", e);
    }

    URL[] u = new URL[urls.size()];

    ClassLoader classLoader = new FlowerClassLoader(urls.toArray(u), Bootstrap.class.getClassLoader());
    return classLoader;
  }

  private void startup(String configLocation) throws Throwable {
    doStartup(configLocation);
  }

  /**
   * 启动flower container
   */
  public abstract void doStartup(String configLocation) throws Throwable;
}
