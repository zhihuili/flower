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
package com.ly.train.flower.common.io;

import java.util.List;
import org.junit.Test;
import com.ly.train.flower.common.io.resource.Resource;
import com.ly.train.flower.common.io.resource.ResourceLoader;
import com.ly.train.flower.common.util.FileUtil;
import com.ly.train.flower.common.util.Pair;

/**
 * @author leeyazhou
 */
public class ResourceLoaderTest {

  @Test
  public void testGetResources() {
    ResourceLoader resourceLoader = new ResourceLoader("", ".services");
    Resource[] a = resourceLoader.getResources();
    System.out.println(a);
    for (Resource r : a) {
      System.out.println(r.getURL());
      List<Pair<String, String>> re = FileUtil.readService(r);
      System.out.println(re);
    }
  }

  @Test
  public void testGetResources2() {
    ResourceLoader resourceLoader = new ResourceLoader("META-INF", "");
    Resource[] a = resourceLoader.getResources();
    System.out.println(a);
    // for (Resource r : a) {
    // System.out.println(r.getURL());
    // List<Pair<String, String>> re = FileUtil.readService(r);
    // System.out.println(re);
    // }
  }

  @Test
  public void testGetResourcesCLass() {
    ResourceLoader resourceLoader = new ResourceLoader("com.ly", ".class");
    Resource[] a = resourceLoader.getResources();
    System.out.println(a.length);
  }

}
