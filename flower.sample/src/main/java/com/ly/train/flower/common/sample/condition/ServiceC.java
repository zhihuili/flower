package com.ly.train.flower.common.sample.condition;

import com.ly.train.flower.common.service.Service;

public class ServiceC implements Service<MessageC> {

  @Override
  /**
   * print service
   */
  public Object process(MessageC message) {
    System.out.println("I am Service C.");
    MessageX mx = new MessageX();
    mx.setCondition("serviceE");
    return mx;
  }

}
