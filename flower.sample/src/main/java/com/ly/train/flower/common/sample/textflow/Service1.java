package com.ly.train.flower.common.sample.textflow;

import com.ly.train.flower.common.service.Service;

public class Service1 implements Service {


  @Override
  public Object process(Object message) {
    return ((Message1)message).getM2();
  }

}
