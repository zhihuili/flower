package com.ly.train.flower.common.sample;

import com.ly.train.flower.common.service.Service;

public class ServiceB implements Service {

  int i = 0;

  @Override
  /**
   * upper case service
   */
  public Object process(Object message) {
    if (message != null && message instanceof String) {
      MessageA ma = new MessageA();
      ma.setI(i++);
      ma.setS(((String) message).toUpperCase());
      return ma;
    }
    return "";
  }
}
