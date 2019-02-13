package com.ly.train.flower.common.sample.condition;

import com.ly.train.flower.common.service.Service;

public class ServiceA implements Service<String> {

  @Override
  public Object process(String message) {
    if ("b".equals(message)) {
      return new MessageB();
    }
    if ("c".equals(message)) {
      return new MessageC();
    }
    return null;
  }

}
