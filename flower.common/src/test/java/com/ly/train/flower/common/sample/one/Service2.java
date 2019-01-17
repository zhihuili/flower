package com.ly.train.flower.common.sample.one;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.message.IntegerMessage;

public class Service2 implements Service<Message2> {

  @Override
  public Object process(Message2 message) {
    IntegerMessage integerMessage = new IntegerMessage();
    integerMessage.setMessage(message.getAge() + 1);
    return integerMessage;
  }

}
