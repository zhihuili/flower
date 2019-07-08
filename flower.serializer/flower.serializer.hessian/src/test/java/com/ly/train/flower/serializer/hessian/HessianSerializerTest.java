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
package com.ly.train.flower.serializer.hessian;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.serializer.Serializer;
import com.ly.train.flower.serializer.model.CollectionModel;
import com.ly.train.flower.serializer.model.ModelA;
import com.ly.train.flower.serializer.model.ModelB;

/**
 * @author leeyazhou
 * 
 */
public class HessianSerializerTest {
  Serializer serializer = ExtensionLoader.load(Serializer.class).load("hessian");

  @Test
  public void testObject() {
    ModelB modelB = new ModelB();
    modelB.setName("modelB");
    modelB.setOrderNo("10001000");
    byte[] modelBByte = serializer.encode(modelB);
    ModelA modelA = (ModelA) serializer.decode(modelBByte, ModelA.class.getName());
    System.out.println(modelA.getName());
    System.out.println(modelA);
  }

  @Test
  public void testCollection() {
    CollectionModel model = new CollectionModel();
    model.setName("user");
    model.setData(Arrays.asList("a", "b"));
    model.setData2(new HashSet<String>());
    byte[] modelBByte = serializer.encode(model);
    CollectionModel modelA = (CollectionModel) serializer.decode(modelBByte, CollectionModel.class.getName());
    System.out.println(modelA);

    Set<CollectionModel> sets = new HashSet<CollectionModel>();
    sets.add(model);
    byte[] setByte = serializer.encode(sets);
    sets = (Set) serializer.decode(setByte, HashSet.class.getName());
    System.out.println(sets);
  }


}
