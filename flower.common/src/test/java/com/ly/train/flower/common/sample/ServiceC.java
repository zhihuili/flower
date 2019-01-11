package com.ly.train.flower.common.sample;

import com.ly.train.flower.common.service.Service;

public class ServiceC implements Service {

  @Override
  /**
   * print service
   */
  public Object process(Object message) {
    MessageA ma = (MessageA) message;
    System.out.println(ma.getS() + " I am " + ma.getI());
    return null;
  }

}
