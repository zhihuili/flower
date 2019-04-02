/**
 * 
 */
package com.ly.flower.center.util;

import com.ly.flower.center.model.Response;

/**
 * @author leeyazhou
 *
 */
public class R {

  public static <T> Response<T> ok(T data) {

    return new Response<>(data);
  }

  public static <T> Response<T> ok() {
    return new Response<T>();
  }

  public static <T> Response<T> error(int code, String msg) {
    return new Response<>(code, msg);
  }
}
