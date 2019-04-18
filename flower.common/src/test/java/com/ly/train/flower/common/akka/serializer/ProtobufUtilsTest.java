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
package com.ly.train.flower.common.akka.serializer;

import org.junit.Test;
import com.ly.train.flower.common.akka.serializer.model.ModelA;
import com.ly.train.flower.common.akka.serializer.model.ModelB;

/**
 * @author leeyazhou
 *
 */
public class ProtobufUtilsTest {

  @Test
  public void testEncode() {
    ModelB modelB = new ModelB();
    modelB.setName("modelB");
    modelB.setOrderNo("10001000");
    byte[] modelBByte = ProtobufUtils.encode(modelB);

    ModelA modelA = ProtobufUtils.decode(modelBByte, ModelA.class);
    System.out.println(modelA.getName());
  }
}