package com.ly.train.flower.common.sample;

import com.ly.train.flower.common.service.Service;

public class ServiceC implements Service {

  @Override
  public Object process(Object message) {

    System.out.println(message);
    return null;
  }

}
