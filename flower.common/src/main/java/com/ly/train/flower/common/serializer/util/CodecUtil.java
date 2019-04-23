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
package com.ly.train.flower.common.serializer.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.ly.train.flower.common.serializer.Codec;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public class CodecUtil {
  private static final Logger logger = LoggerFactory.getLogger(CodecUtil.class);
  private static final ConcurrentMap<String, Codec> codecCache = new ConcurrentHashMap<>();

  private static CodecUtil instance = null;

  private CodecUtil() {
    try {
      // codecCache.put(Object.class.getName(), Codec.KRYO);
    } catch (Exception e) {
      logger.error("", e);
    }
  }

  public static CodecUtil getInstance() {
    if (instance == null) {
      synchronized (CodecUtil.class) {
        if (instance == null) {
          instance = new CodecUtil();
        }
      }
    }
    return instance;
  }


  public Codec getCodec(String className) {
    return codecCache.getOrDefault(className, Codec.Hessian);
  }
}
