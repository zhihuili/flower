package com.ly.train.flower.common.sample.web;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.ServiceContext;

public class FlowService implements Service {

  private ClassA ca;

  public FlowService() {
    this.ca = new ClassA();
  }

  @Override
  /**
   * trim service
   */
  public Object process(Object message, ServiceContext context) {
    ca.f();
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("FlowService processed");
    return "";
  }

}
