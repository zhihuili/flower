package com.ly.train.flower.common.sample.programflow;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.containe.ServiceContext;

public class ServiceA implements Service<String> {

  private ClassA ca;

  public ServiceA() {
    this.ca = new ClassA();
  }

  @Override
  /**
   * trim service
   */
  public Object process(String message, ServiceContext context) {
    ca.f();
    if (message != null && message instanceof String) {
      return ((String) message).trim();
    }
    return "";
  }

}
