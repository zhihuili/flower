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
package com.ly.train.flower.common.util;

import java.util.Set;
import org.junit.Test;
import org.reflections.Reflections;
import com.ly.train.flower.common.annotation.FlowerService;

public class EnvBuilderTest {

  @Test
  public void testBuildEnv() throws Exception {
    EnvBuilder.buildEnv();
  }

  @Test
  public void testScanner() {
    Reflections reflections = new Reflections("com.ly");
    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(FlowerService.class);

    for (Class<?> c : classes) {
      System.out.println(c);
    }
  }
}
