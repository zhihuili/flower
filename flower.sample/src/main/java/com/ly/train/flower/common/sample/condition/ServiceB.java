package com.ly.train.flower.common.sample.condition;

import com.ly.train.flower.common.service.Service;

public class ServiceB implements Service<MessageB> {

  @Override
  public Object process(MessageB message) {
    System.out.println("I am Service B.");
    return null;
  }

}
