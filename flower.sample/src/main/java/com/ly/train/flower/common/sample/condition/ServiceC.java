package com.ly.train.flower.common.sample.condition;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.containe.ServiceContext;

public class ServiceC implements Service<MessageC> {

  @Override
  /**
   * print service
   */
  public Object process(MessageC message, ServiceContext context) {
    System.out.println("I am Service C.");
    MessageX mx = new MessageX();
    mx.setCondition("serviceE,serviceD");
    return mx;
  }

}
