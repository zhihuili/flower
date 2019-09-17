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
/**
 * 
 */
package com.ly.train.flower.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javassist.NotFoundException;

/**
 * @author leeyazhou
 * 
 */
public class ReflectUtil {
  /**
   * void(V).
   */
  public static final char JVM_VOID = 'V';

  /**
   * boolean(Z).
   */
  public static final char JVM_BOOLEAN = 'Z';

  /**
   * byte(B).
   */
  public static final char JVM_BYTE = 'B';

  /**
   * char(C).
   */
  public static final char JVM_CHAR = 'C';

  /**
   * double(D).
   */
  public static final char JVM_DOUBLE = 'D';

  /**
   * float(F).
   */
  public static final char JVM_FLOAT = 'F';

  /**
   * int(I).
   */
  public static final char JVM_INT = 'I';

  /**
   * long(J).
   */
  public static final char JVM_LONG = 'J';

  /**
   * short(S).
   */
  public static final char JVM_SHORT = 'S';

  public static String getName(Class<?> clazz) {
    if (clazz.isArray()) {
      StringBuilder sb = new StringBuilder();
      do {
        sb.append("[]");
        clazz = clazz.getComponentType();
      } while (clazz.isArray());

      return clazz.getName() + sb.toString();
    }
    return clazz.getName();
  }

  /**
   * get class desc. boolean[].class => "[Z" Object.class => "Ljava/lang/Object;"
   * 
   * @param clazz class.
   * @return desc.
   * @throws NotFoundException ex
   */
  public static String getDesc(Class<?> clazz) {
    StringBuilder ret = new StringBuilder();

    while (clazz.isArray()) {
      ret.append('[');
      clazz = clazz.getComponentType();
    }

    if (clazz.isPrimitive()) {
      String clazzName = clazz.getName();
      if ("void".equals(clazzName)) {
        ret.append(JVM_VOID);
      } else if ("boolean".equals(clazzName)) {
        ret.append(JVM_BOOLEAN);
      } else if ("byte".equals(clazzName)) {
        ret.append(JVM_BYTE);
      } else if ("char".equals(clazzName)) {
        ret.append(JVM_CHAR);
      } else if ("double".equals(clazzName)) {
        ret.append(JVM_DOUBLE);
      } else if ("float".equals(clazzName)) {
        ret.append(JVM_FLOAT);
      } else if ("int".equals(clazzName)) {
        ret.append(JVM_INT);
      } else if ("long".equals(clazzName)) {
        ret.append(JVM_LONG);
      } else if ("short".equals(clazzName)) {
        ret.append(JVM_SHORT);
      }
    } else {
      ret.append('L');
      ret.append(clazz.getName().replace('.', '/'));
      ret.append(';');
    }
    return ret.toString();
  }

  /**
   * get constructor desc. "()V", "(Ljava/lang/String;I)V"
   * 
   * @param structor constructor.
   * @return desc
   */
  public static String getDesc(final Constructor<?> structor) {
    StringBuilder ret = new StringBuilder("(");
    Class<?>[] parameterTypes = structor.getParameterTypes();
    for (int i = 0; i < parameterTypes.length; i++) {
      ret.append(getDesc(parameterTypes[i]));
    }
    ret.append(')').append('V');
    return ret.toString();
  }

  public static ClassLoader getClassLoader(Class<?> cls) {
    ClassLoader cl = null;
    try {
      cl = Thread.currentThread().getContextClassLoader();
    } catch (Throwable ex) {
      // Cannot access thread context ClassLoader - falling back to system class
      // loader...
    }
    if (cl == null) {
      // No thread context class loader -> use class loader of this class.
      cl = cls.getClassLoader();
    }
    return cl;
  }

  /**
   * get method desc. "(I)I", "()V", "(Ljava/lang/String;Z)V"
   * 
   * @param method method.
   * @return desc.
   */
  public static String getDescWithoutMethodName(Method method) {
    StringBuilder ret = new StringBuilder();
    ret.append('(');
    Class<?>[] parameterTypes = method.getParameterTypes();
    for (int i = 0; i < parameterTypes.length; i++) {
      ret.append(getDesc(parameterTypes[i]));
    }
    ret.append(')').append(getDesc(method.getReturnType()));
    return ret.toString();
  }
}
