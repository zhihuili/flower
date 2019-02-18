package com.ly.train.flower.common.sample.condition;

import com.ly.train.flower.common.service.Service;

public class ServiceE implements Service {

  @Override
  public Object process(Object message) {
    System.out.println("I am Service E.");
    MessageX x = new MessageX();
    x.setCondition(1);
    return x;
  }

}
