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
package com.ly.train.flower.common.loader;

import java.net.URL;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author leeyazhou
 *
 */
public class FlowerClassLoaderTest {

  @Test
  @Ignore
  public void test() throws Exception {
    URL[] urls = new URL[] {new URL(
        "file:/Volumes/Data/Users/lee/Desktop/flower-showcase/order-platform-0.0.1-SNAPSHOT/libs/order-api-0.0.1-SNAPSHOT.jar")};
    ClassLoader classLoader = new FlowerClassLoader(urls, getClass().getClassLoader());
    Class<?> clazz = classLoader.loadClass("com.ly.train.order.model.Order");
    System.out.println(clazz);
  }
}
