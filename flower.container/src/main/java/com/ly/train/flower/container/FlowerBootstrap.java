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

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author leeyazhou
 * 
 */
public class FlowerBootstrap extends Bootstrap {
  protected static final Logger logger = LoggerFactory.getLogger(FlowerBootstrap.class);

  @Override
  public void doStartup(String configLocation) throws Throwable {
    Class<?> flowerFactoryClazz =
        Class
            .forName("com.ly.train.flower.common.service.container.simple.SimpleFlowerFactory", true, getClassLoader());
    Object flowerFactory = flowerFactoryClazz.getConstructor(String.class).newInstance(configLocation);
    Method startMethod = flowerFactoryClazz.getMethod("start");
    startMethod.invoke(flowerFactory);
  }

}
