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
package com.ly.train.flower.core.akka.serializer.hessian;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.core.akka.serializer.hessian.model.ModelA;
import com.ly.train.flower.core.akka.serializer.hessian.model.ModelB;
import com.ly.train.flower.core.serializer.Codec;
import com.ly.train.flower.core.serializer.Serializer;

/**
 * @author leeyazhou
 * 
 */
public class HessianTest {

  @Test
  public void testEncode() {
    ModelB modelB = new ModelB();
    modelB.setName("modelB");
    modelB.setOrderNo("10001000");
    Codec codec = Codec.Hessian;
    byte[] modelBByte = codec.getSerializer().encode(modelB);

    ModelA modelA = (ModelA) codec.getSerializer().decode(modelBByte, ModelA.class.getName());
    System.out.println(modelA.getName());
  }

  @Test
  public void testEncodeObject() {
    System.out.println(ExtensionLoader.load(Serializer.class).load().encode(new Object()));
  }

  @Test
  public void testSet() {
    Set<String> data = new HashSet<String>();
    data.add("aaa");
    data.add("bbb");
    data.add("aaa");
    Codec codec = Codec.Hessian;
    byte[] bytes = codec.getSerializer().encode(data);

    data = (Set<String>) codec.getSerializer().decode(bytes, Set.class.getName());
    data.isEmpty();
    for (String item : data) {
      System.out.println(item);
    }
  }
}
