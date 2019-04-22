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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public class ClassUtil {
  private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);
  private static final ConcurrentMap<String, ClassWrapper> cache = new ConcurrentHashMap<String, ClassWrapper>();

  public static Class<?> forName(String className) {
    return forName(className, ClassUtil.class.getClassLoader(), true);
  }

  public static Class<?> forNameNoException(String className) {
    return forName(className, ClassUtil.class.getClassLoader(), false);
  }

  public static boolean exists(String className) {
    return forNameNoException(className) != null;
  }

  public static Class<?> forName(String className, ClassLoader loader, boolean throwException) {
    if (StringUtil.isBlank(className)) {
      return null;
    }
    ClassWrapper ret = cache.get(className);
    if (ret == null) {
      Class<?> clazz = null;
      try {
        clazz = Class.forName(className, true, loader);
      } catch (ClassNotFoundException e) {
        if (throwException) {
          logger.error("", e);
        }
      }
      ret = new ClassWrapper(clazz);
      ClassWrapper temp = cache.putIfAbsent(className, ret);
      if (temp != null) {
        ret = temp;
      }
    }
    return ret.getClazz();
  }

  static class ClassWrapper {
    private Class<?> clazz;

    public ClassWrapper(Class<?> clazz) {
      this.clazz = clazz;
    }

    public Class<?> getClazz() {
      return clazz;
    }

    public void setClazz(Class<?> clazz) {
      this.clazz = clazz;
    }
  }
}
