package com.ly.train.flower.common.sample;

import com.ly.train.flower.common.service.Service;

public class ServiceA implements Service {

  private ClassA ca;

  public ServiceA() {
    this.ca = new ClassA();
  }

  @Override
  public Object process(Object message) {
    ca.f();
    if (message != null && message instanceof String) {
      return ((String) message).trim();
    }
    return "";
  }

}
