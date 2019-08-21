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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public abstract class ObjectUtil {

  private static final int INITIAL_HASH = 7;
  private static final int MULTIPLIER = 31;

  private static final String EMPTY_STRING = "";
  private static final String NULL_STRING = "null";
  private static final String ARRAY_START = "{";
  private static final String ARRAY_END = "}";
  private static final String EMPTY_ARRAY = ARRAY_START + ARRAY_END;
  private static final String ARRAY_ELEMENT_SEPARATOR = ", ";


  public static boolean isCheckedException(Throwable ex) {
    return !(ex instanceof RuntimeException || ex instanceof Error);
  }

  public static boolean isCompatibleWithThrowsClause(Throwable ex, Class<?>... declaredExceptions) {
    if (!isCheckedException(ex)) {
      return true;
    }
    if (declaredExceptions != null) {
      for (Class<?> declaredException : declaredExceptions) {
        if (declaredException.isInstance(ex)) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean isArray(Object obj) {
    return (obj != null && obj.getClass().isArray());
  }

  public static boolean isEmpty(Object[] array) {
    return (array == null || array.length == 0);
  }

  @SuppressWarnings("rawtypes")
  public static boolean isEmpty(Object obj) {
    if (obj == null) {
      return true;
    }

    if (obj instanceof Optional) {
      return !((Optional) obj).isPresent();
    }
    if (obj instanceof CharSequence) {
      return ((CharSequence) obj).length() == 0;
    }
    if (obj.getClass().isArray()) {
      return Array.getLength(obj) == 0;
    }
    if (obj instanceof Collection) {
      return ((Collection) obj).isEmpty();
    }
    if (obj instanceof Map) {
      return ((Map) obj).isEmpty();
    }

    // else
    return false;
  }


  public static Object unwrapOptional(Object obj) {
    if (obj instanceof Optional) {
      Optional<?> optional = (Optional<?>) obj;
      if (!optional.isPresent()) {
        return null;
      }
      Object result = optional.get();
      Assert.isTrue(!(result instanceof Optional), "Multi-level Optional usage not supported");
      return result;
    }
    return obj;
  }

  public static boolean containsElement(Object[] array, Object element) {
    if (array == null) {
      return false;
    }
    for (Object arrayEle : array) {
      if (nullSafeEquals(arrayEle, element)) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsConstant(Enum<?>[] enumValues, String constant) {
    return containsConstant(enumValues, constant, false);
  }

  public static boolean containsConstant(Enum<?>[] enumValues, String constant, boolean caseSensitive) {
    for (Enum<?> candidate : enumValues) {
      if (caseSensitive ? candidate.toString().equals(constant) : candidate.toString().equalsIgnoreCase(constant)) {
        return true;
      }
    }
    return false;
  }

  public static <E extends Enum<?>> E caseInsensitiveValueOf(E[] enumValues, String constant) {
    for (E candidate : enumValues) {
      if (candidate.toString().equalsIgnoreCase(constant)) {
        return candidate;
      }
    }
    throw new IllegalArgumentException("Constant [" + constant + "] does not exist in enum type "
        + enumValues.getClass().getComponentType().getName());
  }

  public static <A, O extends A> A[] addObjectToArray(A[] array, O obj) {
    Class<?> compType = Object.class;
    if (array != null) {
      compType = array.getClass().getComponentType();
    } else if (obj != null) {
      compType = obj.getClass();
    }
    int newArrLength = (array != null ? array.length + 1 : 1);
    @SuppressWarnings("unchecked")
    A[] newArr = (A[]) Array.newInstance(compType, newArrLength);
    if (array != null) {
      System.arraycopy(array, 0, newArr, 0, array.length);
    }
    newArr[newArr.length - 1] = obj;
    return newArr;
  }

  public static Object[] toObjectArray(Object source) {
    if (source instanceof Object[]) {
      return (Object[]) source;
    }
    if (source == null) {
      return new Object[0];
    }
    if (!source.getClass().isArray()) {
      throw new IllegalArgumentException("Source is not an array: " + source);
    }
    int length = Array.getLength(source);
    if (length == 0) {
      return new Object[0];
    }
    Class<?> wrapperType = Array.get(source, 0).getClass();
    Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
    for (int i = 0; i < length; i++) {
      newArray[i] = Array.get(source, i);
    }
    return newArray;
  }


  // ---------------------------------------------------------------------
  // Convenience methods for content-based equality/hash-code handling
  // ---------------------------------------------------------------------

  /**
   * Determine if the given objects are equal, returning {@code true} if both are
   * {@code null} or {@code false} if only one is {@code null}.
   * 
   * <p>
   * Compares arrays with {@code Arrays.equals}, performing an equality check
   * based on the array elements rather than the array reference.
   * 
   * @param o1 first Object to compare
   * @param o2 second Object to compare
   * @return whether the given objects are equal
   * @see Object#equals(Object)
   * @see java.util.Arrays#equals
   */
  public static boolean nullSafeEquals(Object o1, Object o2) {
    if (o1 == o2) {
      return true;
    }
    if (o1 == null || o2 == null) {
      return false;
    }
    if (o1.equals(o2)) {
      return true;
    }
    if (o1.getClass().isArray() && o2.getClass().isArray()) {
      return arrayEquals(o1, o2);
    }
    return false;
  }

  private static boolean arrayEquals(Object o1, Object o2) {
    if (o1 instanceof Object[] && o2 instanceof Object[]) {
      return Arrays.equals((Object[]) o1, (Object[]) o2);
    }
    if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
      return Arrays.equals((boolean[]) o1, (boolean[]) o2);
    }
    if (o1 instanceof byte[] && o2 instanceof byte[]) {
      return Arrays.equals((byte[]) o1, (byte[]) o2);
    }
    if (o1 instanceof char[] && o2 instanceof char[]) {
      return Arrays.equals((char[]) o1, (char[]) o2);
    }
    if (o1 instanceof double[] && o2 instanceof double[]) {
      return Arrays.equals((double[]) o1, (double[]) o2);
    }
    if (o1 instanceof float[] && o2 instanceof float[]) {
      return Arrays.equals((float[]) o1, (float[]) o2);
    }
    if (o1 instanceof int[] && o2 instanceof int[]) {
      return Arrays.equals((int[]) o1, (int[]) o2);
    }
    if (o1 instanceof long[] && o2 instanceof long[]) {
      return Arrays.equals((long[]) o1, (long[]) o2);
    }
    if (o1 instanceof short[] && o2 instanceof short[]) {
      return Arrays.equals((short[]) o1, (short[]) o2);
    }
    return false;
  }

  public static int nullSafeHashCode(Object obj) {
    if (obj == null) {
      return 0;
    }
    if (obj.getClass().isArray()) {
      if (obj instanceof Object[]) {
        return nullSafeHashCode((Object[]) obj);
      }
      if (obj instanceof boolean[]) {
        return nullSafeHashCode((boolean[]) obj);
      }
      if (obj instanceof byte[]) {
        return nullSafeHashCode((byte[]) obj);
      }
      if (obj instanceof char[]) {
        return nullSafeHashCode((char[]) obj);
      }
      if (obj instanceof double[]) {
        return nullSafeHashCode((double[]) obj);
      }
      if (obj instanceof float[]) {
        return nullSafeHashCode((float[]) obj);
      }
      if (obj instanceof int[]) {
        return nullSafeHashCode((int[]) obj);
      }
      if (obj instanceof long[]) {
        return nullSafeHashCode((long[]) obj);
      }
      if (obj instanceof short[]) {
        return nullSafeHashCode((short[]) obj);
      }
    }
    return obj.hashCode();
  }

  public static int nullSafeHashCode(Object[] array) {
    if (array == null) {
      return 0;
    }
    int hash = INITIAL_HASH;
    for (Object element : array) {
      hash = MULTIPLIER * hash + nullSafeHashCode(element);
    }
    return hash;
  }

  public static int nullSafeHashCode(boolean[] array) {
    if (array == null) {
      return 0;
    }
    int hash = INITIAL_HASH;
    for (boolean element : array) {
      hash = MULTIPLIER * hash + Boolean.hashCode(element);
    }
    return hash;
  }

  /**
   * Return a hash code based on the contents of the specified array. If
   * {@code array} is {@code null}, this method returns 0.
   */
  public static int nullSafeHashCode(byte[] array) {
    if (array == null) {
      return 0;
    }
    int hash = INITIAL_HASH;
    for (byte element : array) {
      hash = MULTIPLIER * hash + element;
    }
    return hash;
  }

  /**
   * Return a hash code based on the contents of the specified array. If
   * {@code array} is {@code null}, this method returns 0.
   */
  public static int nullSafeHashCode(char[] array) {
    if (array == null) {
      return 0;
    }
    int hash = INITIAL_HASH;
    for (char element : array) {
      hash = MULTIPLIER * hash + element;
    }
    return hash;
  }

  /**
   * Return a hash code based on the contents of the specified array. If
   * {@code array} is {@code null}, this method returns 0.
   */
  public static int nullSafeHashCode(double[] array) {
    if (array == null) {
      return 0;
    }
    int hash = INITIAL_HASH;
    for (double element : array) {
      hash = MULTIPLIER * hash + Double.hashCode(element);
    }
    return hash;
  }

  /**
   * Return a hash code based on the contents of the specified array. If
   * {@code array} is {@code null}, this method returns 0.
   */
  public static int nullSafeHashCode(float[] array) {
    if (array == null) {
      return 0;
    }
    int hash = INITIAL_HASH;
    for (float element : array) {
      hash = MULTIPLIER * hash + Float.hashCode(element);
    }
    return hash;
  }

  /**
   * Return a hash code based on the contents of the specified array. If
   * {@code array} is {@code null}, this method returns 0.
   */
  public static int nullSafeHashCode(int[] array) {
    if (array == null) {
      return 0;
    }
    int hash = INITIAL_HASH;
    for (int element : array) {
      hash = MULTIPLIER * hash + element;
    }
    return hash;
  }

  /**
   * Return a hash code based on the contents of the specified array. If
   * {@code array} is {@code null}, this method returns 0.
   */
  public static int nullSafeHashCode(long[] array) {
    if (array == null) {
      return 0;
    }
    int hash = INITIAL_HASH;
    for (long element : array) {
      hash = MULTIPLIER * hash + Long.hashCode(element);
    }
    return hash;
  }

  /**
   * Return a hash code based on the contents of the specified array. If
   * {@code array} is {@code null}, this method returns 0.
   */
  public static int nullSafeHashCode(short[] array) {
    if (array == null) {
      return 0;
    }
    int hash = INITIAL_HASH;
    for (short element : array) {
      hash = MULTIPLIER * hash + element;
    }
    return hash;
  }

  /**
   * Return the same value as {@link Boolean#hashCode(boolean)} .
   * 
   * @deprecated as of Spring Framework 5.0, in favor of the native JDK 8 variant
   */
  @Deprecated
  public static int hashCode(boolean bool) {
    return Boolean.hashCode(bool);
  }

  /**
   * Return the same value as {@link Double#hashCode(double)} .
   * 
   * @deprecated as of Spring Framework 5.0, in favor of the native JDK 8 variant
   */
  @Deprecated
  public static int hashCode(double dbl) {
    return Double.hashCode(dbl);
  }

  /**
   * Return the same value as {@link Float#hashCode(float)} .
   * 
   * @deprecated as of Spring Framework 5.0, in favor of the native JDK 8 variant
   */
  @Deprecated
  public static int hashCode(float flt) {
    return Float.hashCode(flt);
  }

  /**
   * Return the same value as {@link Long#hashCode(long)} .
   * 
   * @deprecated as of Spring Framework 5.0, in favor of the native JDK 8 variant
   */
  @Deprecated
  public static int hashCode(long lng) {
    return Long.hashCode(lng);
  }


  // ---------------------------------------------------------------------
  // Convenience methods for toString output
  // ---------------------------------------------------------------------

  /**
   * Return a String representation of an object's overall identity.
   * 
   * @param obj the object (may be {@code null})
   * @return the object's identity as String representation, or an empty String if
   *         the object was {@code null}
   */
  public static String identityToString(Object obj) {
    if (obj == null) {
      return EMPTY_STRING;
    }
    return obj.getClass().getName() + "@" + getIdentityHexString(obj);
  }

  /**
   * Return a hex String form of an object's identity hash code.
   * 
   * @param obj the object
   * @return the object's identity code in hex notation
   */
  public static String getIdentityHexString(Object obj) {
    return Integer.toHexString(System.identityHashCode(obj));
  }

  public static String getDisplayString(Object obj) {
    if (obj == null) {
      return EMPTY_STRING;
    }
    return nullSafeToString(obj);
  }

  public static String nullSafeClassName(Object obj) {
    return (obj != null ? obj.getClass().getName() : NULL_STRING);
  }

  public static String nullSafeToString(Object obj) {
    if (obj == null) {
      return NULL_STRING;
    }
    if (obj instanceof String) {
      return (String) obj;
    }
    if (obj instanceof Object[]) {
      return nullSafeToString((Object[]) obj);
    }
    if (obj instanceof boolean[]) {
      return nullSafeToString((boolean[]) obj);
    }
    if (obj instanceof byte[]) {
      return nullSafeToString((byte[]) obj);
    }
    if (obj instanceof char[]) {
      return nullSafeToString((char[]) obj);
    }
    if (obj instanceof double[]) {
      return nullSafeToString((double[]) obj);
    }
    if (obj instanceof float[]) {
      return nullSafeToString((float[]) obj);
    }
    if (obj instanceof int[]) {
      return nullSafeToString((int[]) obj);
    }
    if (obj instanceof long[]) {
      return nullSafeToString((long[]) obj);
    }
    if (obj instanceof short[]) {
      return nullSafeToString((short[]) obj);
    }
    return obj.toString();
  }

  /**
   * Return a String representation of the contents of the specified array.
   * 
   * <p>
   * The String representation consists of a list of the array's elements,
   * enclosed in curly braces ({@code " "}). Adjacent elements are separated by
   * the characters {@code ", "} (a comma followed by a space). Returns
   * {@code "null"} if {@code array} is {@code null}.
   * 
   * @param array the array to build a String representation for
   * @return a String representation of {@code array}
   */
  public static String nullSafeToString(Object[] array) {
    if (array == null) {
      return NULL_STRING;
    }
    int length = array.length;
    if (length == 0) {
      return EMPTY_ARRAY;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      if (i == 0) {
        sb.append(ARRAY_START);
      } else {
        sb.append(ARRAY_ELEMENT_SEPARATOR);
      }
      sb.append(String.valueOf(array[i]));
    }
    sb.append(ARRAY_END);
    return sb.toString();
  }

  public static String nullSafeToString(boolean[] array) {
    if (array == null) {
      return NULL_STRING;
    }
    int length = array.length;
    if (length == 0) {
      return EMPTY_ARRAY;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      if (i == 0) {
        sb.append(ARRAY_START);
      } else {
        sb.append(ARRAY_ELEMENT_SEPARATOR);
      }

      sb.append(array[i]);
    }
    sb.append(ARRAY_END);
    return sb.toString();
  }

  public static String nullSafeToString(byte[] array) {
    if (array == null) {
      return NULL_STRING;
    }
    int length = array.length;
    if (length == 0) {
      return EMPTY_ARRAY;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      if (i == 0) {
        sb.append(ARRAY_START);
      } else {
        sb.append(ARRAY_ELEMENT_SEPARATOR);
      }
      sb.append(array[i]);
    }
    sb.append(ARRAY_END);
    return sb.toString();
  }

  /**
   * Return a String representation of the contents of the specified array.
   * 
   * <p>
   * The String representation consists of a list of the array's elements,
   * enclosed in curly braces ({@code " "}). Adjacent elements are separated by
   * the characters {@code ", "} (a comma followed by a space). Returns
   * {@code "null"} if {@code array} is {@code null}.
   * 
   * @param array the array to build a String representation for
   * @return a String representation of {@code array}
   */
  public static String nullSafeToString(char[] array) {
    if (array == null) {
      return NULL_STRING;
    }
    int length = array.length;
    if (length == 0) {
      return EMPTY_ARRAY;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      if (i == 0) {
        sb.append(ARRAY_START);
      } else {
        sb.append(ARRAY_ELEMENT_SEPARATOR);
      }
      sb.append("'").append(array[i]).append("'");
    }
    sb.append(ARRAY_END);
    return sb.toString();
  }

  public static String nullSafeToString(double[] array) {
    if (array == null) {
      return NULL_STRING;
    }
    int length = array.length;
    if (length == 0) {
      return EMPTY_ARRAY;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      if (i == 0) {
        sb.append(ARRAY_START);
      } else {
        sb.append(ARRAY_ELEMENT_SEPARATOR);
      }

      sb.append(array[i]);
    }
    sb.append(ARRAY_END);
    return sb.toString();
  }

  public static String nullSafeToString(float[] array) {
    if (array == null) {
      return NULL_STRING;
    }
    int length = array.length;
    if (length == 0) {
      return EMPTY_ARRAY;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      if (i == 0) {
        sb.append(ARRAY_START);
      } else {
        sb.append(ARRAY_ELEMENT_SEPARATOR);
      }

      sb.append(array[i]);
    }
    sb.append(ARRAY_END);
    return sb.toString();
  }

  /**
   * Return a String representation of the contents of the specified array.
   * 
   * <p>
   * The String representation consists of a list of the array's elements,
   * enclosed in curly braces ({@code " "}). Adjacent elements are separated by
   * the characters {@code ", "} (a comma followed by a space). Returns
   * {@code "null"} if {@code array} is {@code null}.
   * 
   * @param array the array to build a String representation for
   * @return a String representation of {@code array}
   */
  public static String nullSafeToString(int[] array) {
    if (array == null) {
      return NULL_STRING;
    }
    int length = array.length;
    if (length == 0) {
      return EMPTY_ARRAY;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      if (i == 0) {
        sb.append(ARRAY_START);
      } else {
        sb.append(ARRAY_ELEMENT_SEPARATOR);
      }
      sb.append(array[i]);
    }
    sb.append(ARRAY_END);
    return sb.toString();
  }

  /**
   * Return a String representation of the contents of the specified array.
   * 
   * <p>
   * The String representation consists of a list of the array's elements,
   * enclosed in curly braces ({@code " "}). Adjacent elements are separated by
   * the characters {@code ", "} (a comma followed by a space). Returns
   * {@code "null"} if {@code array} is {@code null}.
   * 
   * @param array the array to build a String representation for
   * @return a String representation of {@code array}
   */
  public static String nullSafeToString(long[] array) {
    if (array == null) {
      return NULL_STRING;
    }
    int length = array.length;
    if (length == 0) {
      return EMPTY_ARRAY;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      if (i == 0) {
        sb.append(ARRAY_START);
      } else {
        sb.append(ARRAY_ELEMENT_SEPARATOR);
      }
      sb.append(array[i]);
    }
    sb.append(ARRAY_END);
    return sb.toString();
  }

  public static String nullSafeToString(short[] array) {
    if (array == null) {
      return NULL_STRING;
    }
    int length = array.length;
    if (length == 0) {
      return EMPTY_ARRAY;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      if (i == 0) {
        sb.append(ARRAY_START);
      } else {
        sb.append(ARRAY_ELEMENT_SEPARATOR);
      }
      sb.append(array[i]);
    }
    sb.append(ARRAY_END);
    return sb.toString();
  }

}
