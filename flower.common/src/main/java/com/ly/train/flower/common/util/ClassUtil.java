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

import java.util.HashMap;
import java.util.Map;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public class ClassUtil {
  private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);
  private static final Map<String, Class<?>> cache = new HashMap<String, Class<?>>();

  public static Class<?> forName(String className) {
    return forName(className, ClassUtil.class.getClassLoader());
  }

  public static Class<?> forName(String className, ClassLoader loader) {
    Class<?> ret = cache.get(className);
    if (ret == null) {
      try {
        ret = Class.forName(className, true, loader);
        Class<?> temp = cache.putIfAbsent(className, ret);
        if (temp != null) {
          ret = temp;
        }
      } catch (ClassNotFoundException e) {
        logger.error("", e);
      }
    }
    return ret;
  }

}
