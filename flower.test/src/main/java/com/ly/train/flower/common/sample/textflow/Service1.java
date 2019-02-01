package com.ly.train.flower.common.sample.textflow;

import com.ly.train.flower.common.service.Service;

public class Service1 implements Service<Message1> {


  @Override
  public Object process(Message1 message) {
    return message.getM2();
  }

}
