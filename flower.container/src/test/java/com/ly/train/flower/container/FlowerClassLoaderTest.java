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
import org.junit.Ignore;
import org.junit.Test;
import com.ly.train.flower.container.loader.BootstrapClassLoader;

/**
 * @author leeyazhou
 * 
 */
public class FlowerClassLoaderTest {
  @Test
  public void test() throws Exception {
    ClassLoader classLoader =
        createClassLoader("/Volumes/Data/Users/lee/Desktop/flower-showcase/order-platform-0.0.1-SNAPSHOT");
    try {
      Class<?> clazz = classLoader.loadClass("org.springframework.context.support.ClassPathXmlApplicationContext");
      System.out.println(clazz);
    } catch (Exception e) {
      // ignore
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
          System.out.println(libs + f);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    URL[] u = new URL[urls.size()];

    ClassLoader classLoader = new BootstrapClassLoader(urls.toArray(u), Bootstrap.class.getClassLoader());
    return classLoader;
  }


  @Test
  @Ignore
  public void test2() throws Exception {
    URL[] urls = new URL[] {new URL(
        "file:/Volumes/Data/Users/lee/Desktop/flower-showcase/order-platform-0.0.1-SNAPSHOT/libs/order-api-0.0.1-SNAPSHOT.jar")};
    ClassLoader classLoader = new BootstrapClassLoader(urls, getClass().getClassLoader());
    Class<?> clazz = classLoader.loadClass("com.ly.train.order.model.Order");
    System.out.println(clazz);
  }
}
