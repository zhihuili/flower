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

import org.apache.pekko.actor.Extension;
import org.apache.pekko.serialization.JSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.serializer.Serializer;

/**
 * @author leeyazhou
 * 
 */
public class HessianSerializer extends JSerializer implements Extension {
  static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);
  private static final Serializer hessianSerializer = ExtensionLoader.load(Serializer.class).load("hessian");

  @Override
  public int identifier() {
    return 0205;
  }

  @Override
  public boolean includeManifest() {
    return true;
  }

  @Override
  public byte[] toBinary(Object data) {
    // logger.info("序列化: {}", data);

    return hessianSerializer.encode(data);
  }

  @Override
  public Object fromBinaryJava(byte[] data, Class<?> clazz) {
    // logger.info("反序列化 {}: {}", clazz, data);
    return hessianSerializer.decode(data, clazz.getName());
  }



}
