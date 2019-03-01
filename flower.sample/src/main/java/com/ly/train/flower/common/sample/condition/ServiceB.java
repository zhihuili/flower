package com.ly.train.flower.common.sample.condition;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.containe.ServiceContext;

public class ServiceB implements Service<MessageB> {

  @Override
  public Object process(MessageB message, ServiceContext context) {
    System.out.println("I am Service B.");
    return null;
  }
}
