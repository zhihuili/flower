package com.ly.train.flower.common.sample.programflow;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.containe.ServiceContext;

public class ServiceC implements Service<MessageA> {

  @Override
  /**
   * print service
   */
  public Object process(MessageA message, ServiceContext context) {
    MessageA ma = (MessageA) message;
    System.out.println(ma.getS() + " I am " + ma.getI());
    return null;
  }

}
