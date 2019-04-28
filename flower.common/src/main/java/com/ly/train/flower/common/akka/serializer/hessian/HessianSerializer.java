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
package com.ly.train.flower.common.akka.serializer.hessian;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.serializer.Codec;
import akka.actor.Extension;
import akka.serialization.JSerializer;

/**
 * @author leeyazhou
 * 
 */
public class HessianSerializer extends JSerializer implements Extension {
  static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);

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

    return Codec.Hessian.getSerializer().encode(data);
  }

  @Override
  public Object fromBinaryJava(byte[] data, Class<?> clazz) {
    // logger.info("反序列化 {}: {}", clazz, data);
    return Codec.Hessian.getSerializer().decode(data, clazz.getName());
  }



}
