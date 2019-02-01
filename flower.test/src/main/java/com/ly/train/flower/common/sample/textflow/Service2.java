package com.ly.train.flower.common.sample.textflow;

import com.ly.train.flower.common.service.Service;

public class Service2 implements Service<Message2> {

  @Override
  public Object process(Message2 message) {
    return message.getAge() + 1;
  }

}
