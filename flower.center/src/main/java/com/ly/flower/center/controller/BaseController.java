/**
 * 
 */
package com.ly.flower.center.controller;

import com.ly.flower.center.model.Response;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public class BaseController {
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  protected <T> Response<T> ok(T data) {

    return new Response<>(data);
  }

  public <T> Response<T> ok() {
    return new Response<T>();
  }

  protected <T> Response<T> error(int code, String msg) {
    return new Response<>(code, msg);
  }

}
