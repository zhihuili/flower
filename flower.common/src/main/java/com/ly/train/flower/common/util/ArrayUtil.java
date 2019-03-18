/**
 * 
 */
package com.ly.train.flower.common.util;

/**
 * @author leeyazhou
 *
 */
public class ArrayUtil {
  public static boolean isEmpty(final Object[] array) {
    return array == null || array.length == 0;
  }

  public static boolean isNotEmpty(final Object[] array) {
    return !isEmpty(array);
  }
}
