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

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author leeyazhou
 * 
 */
public class Assert {


  public static void state(boolean expression, String message) {
    if (!expression) {
      throw new IllegalStateException(message);
    }
  }

  public static void state(boolean expression, Supplier<String> messageSupplier) {
    if (!expression) {
      throw new IllegalStateException(nullSafeGet(messageSupplier));
    }
  }

  public static void isTrue(boolean expression, String message) {
    if (!expression) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
    if (!expression) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }


  public static void isNull(Object object, String message) {
    if (object != null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void isNull(Object object, Supplier<String> messageSupplier) {
    if (object != null) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  public static void notNull(Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notEquals(Object o1, Object o2, String message) {
    if (o1 != null && o1.equals(o2)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notNull(Object object, Supplier<String> messageSupplier) {
    if (object == null) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  public static void hasLength(String text, String message) {
    if (!StringUtil.hasLength(text)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void hasLength(String text, Supplier<String> messageSupplier) {
    if (!StringUtil.hasLength(text)) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  public static void hasText(String text, String message) {
    if (!StringUtil.hasText(text)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void hasText(String text, Supplier<String> messageSupplier) {
    if (!StringUtil.hasText(text)) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }


  public static void doesNotContain(String textToSearch, String substring, String message) {
    if (StringUtil.hasLength(textToSearch) && StringUtil.hasLength(substring) && textToSearch.contains(substring)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void doesNotContain(String textToSearch, String substring, Supplier<String> messageSupplier) {
    if (StringUtil.hasLength(textToSearch) && StringUtil.hasLength(substring) && textToSearch.contains(substring)) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }


  public static void notEmpty(Object[] array, String message) {
    if (ObjectUtil.isEmpty(array)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notEmpty(Object[] array, Supplier<String> messageSupplier) {
    if (ObjectUtil.isEmpty(array)) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  public static void noNullElements(Object[] array, String message) {
    if (array != null) {
      for (Object element : array) {
        if (element == null) {
          throw new IllegalArgumentException(message);
        }
      }
    }
  }

  public static void noNullElements(Object[] array, Supplier<String> messageSupplier) {
    if (array != null) {
      for (Object element : array) {
        if (element == null) {
          throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
      }
    }
  }


  public static void notEmpty(Collection<?> collection, String message) {
    if (CollectionUtil.isEmpty(collection)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notEmpty(Collection<?> collection, Supplier<String> messageSupplier) {
    if (CollectionUtil.isEmpty(collection)) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }


  public static void notEmpty(Map<?, ?> map, String message) {
    if (CollectionUtil.isEmpty(map)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notEmpty(Map<?, ?> map, Supplier<String> messageSupplier) {
    if (CollectionUtil.isEmpty(map)) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  public static void isInstanceOf(Class<?> type, Object obj, String message) {
    notNull(type, "Type to check against must not be null");
    if (!type.isInstance(obj)) {
      instanceCheckFailed(type, obj, message);
    }
  }

  public static void isInstanceOf(Class<?> type, Object obj, Supplier<String> messageSupplier) {
    notNull(type, "Type to check against must not be null");
    if (!type.isInstance(obj)) {
      instanceCheckFailed(type, obj, nullSafeGet(messageSupplier));
    }
  }

  public static void isInstanceOf(Class<?> type, Object obj) {
    isInstanceOf(type, obj, "");
  }

  public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
    notNull(superType, "Super type to check against must not be null");
    if (subType == null || !superType.isAssignableFrom(subType)) {
      assignableCheckFailed(superType, subType, message);
    }
  }

  public static void isAssignable(Class<?> superType, Class<?> subType, Supplier<String> messageSupplier) {
    notNull(superType, "Super type to check against must not be null");
    if (subType == null || !superType.isAssignableFrom(subType)) {
      assignableCheckFailed(superType, subType, nullSafeGet(messageSupplier));
    }
  }

  public static void isAssignable(Class<?> superType, Class<?> subType) {
    isAssignable(superType, subType, "");
  }


  private static void instanceCheckFailed(Class<?> type, Object obj, String msg) {
    String className = (obj != null ? obj.getClass().getName() : "null");
    String result = "";
    boolean defaultMessage = true;
    if (StringUtil.hasLength(msg)) {
      if (endsWithSeparator(msg)) {
        result = msg + " ";
      } else {
        result = messageWithTypeName(msg, className);
        defaultMessage = false;
      }
    }
    if (defaultMessage) {
      result = result + ("Object of class [" + className + "] must be an instance of " + type);
    }
    throw new IllegalArgumentException(result);
  }

  private static void assignableCheckFailed(Class<?> superType, Class<?> subType, String msg) {
    String result = "";
    boolean defaultMessage = true;
    if (StringUtil.hasLength(msg)) {
      if (endsWithSeparator(msg)) {
        result = msg + " ";
      } else {
        result = messageWithTypeName(msg, subType);
        defaultMessage = false;
      }
    }
    if (defaultMessage) {
      result = result + (subType + " is not assignable to " + superType);
    }
    throw new IllegalArgumentException(result);
  }

  private static boolean endsWithSeparator(String msg) {
    return (msg.endsWith(":") || msg.endsWith(";") || msg.endsWith(",") || msg.endsWith("."));
  }

  private static String messageWithTypeName(String msg, Object typeName) {
    return msg + (msg.endsWith(" ") ? "" : ": ") + typeName;
  }


  private static String nullSafeGet(Supplier<String> messageSupplier) {
    return (messageSupplier != null ? messageSupplier.get() : null);
  }


}
