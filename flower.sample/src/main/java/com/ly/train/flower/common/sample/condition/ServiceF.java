package com.ly.train.flower.common.sample.condition;

import com.ly.train.flower.common.service.Service;

public class ServiceF implements Service {

  @Override
  public Object process(Object message) {
    System.out.println("I am Service F.");
    return null;
  }

}
