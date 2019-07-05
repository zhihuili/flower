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
package com.ly.train.flower.core.serializer;

import com.ly.train.flower.core.serializer.hessian.HessianSerializer;
import com.ly.train.flower.core.serializer.jdk.JdkSerializer;

/**
 * @author leeyazhou
 * 
 */
public enum Codec {

  /**
   * jdk
   */
  JDK("jdk", 0, new JdkSerializer()),


  Hessian("hessian", 1, new HessianSerializer());

  private String name;
  private int code;
  private Serializer serializer;

  private Codec(String name, int code, Serializer serializer) {
    this.name = name;
    this.code = code;
    this.serializer = serializer;
  }

  public String getName() {
    return name;
  }

  public int getCode() {
    return code;
  }

  public Serializer getSerializer() {
    return serializer;
  }

  public byte[] encode(Object data) {
    return getSerializer().encode(data);
  }

  public Object decode(byte[] data, String className) {
    return getSerializer().decode(data, className);
  }

  public static Codec valueOf(int code) {
    for (Codec item : values()) {
      if (code == item.getCode()) {
        return item;
      }
    }

    return null;
  }
}
