package com.ly.train.flower.common.sample.condition;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.containe.ServiceContext;

public class ServiceE implements Service {

  @Override
  public Object process(Object message, ServiceContext context) {
    System.out.println("I am Service E.");
    MessageX x = new MessageX();
    x.setCondition(1);
    return x;
  }

}
