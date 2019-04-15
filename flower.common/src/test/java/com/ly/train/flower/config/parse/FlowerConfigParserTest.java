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
package com.ly.train.flower.config.parse;

import org.junit.Test;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.config.parser.FlowerConfigParser;

/**
 * @author leeyazhou
 *
 */
public class FlowerConfigParserTest {

  @Test
  public void testParse() {
    FlowerConfig flowerConfig = new FlowerConfigParser("flower.yml").parse();
    System.out.println(flowerConfig);
    System.out.println(flowerConfig.getRegistry());
  }

  @Test
  public void testParse1() {
    FlowerConfig flowerConfig = new FlowerConfigParser("conf/flower_25003.yml").parse();
    System.out.println(flowerConfig);
  }

  @Test
  public void testParse2() {
    FlowerConfig flowerConfig = new FlowerConfigParser("conf/flower_25004.yml").parse();
    System.out.println(flowerConfig);
  }
}
