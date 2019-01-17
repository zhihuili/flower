package com.ly.train.flower.common.sample.one;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.message.StringMessage;

public class Service3 implements Service<Message2> {

  @Override
  public Object process(Message2 message) {
    StringMessage stringMessage = new StringMessage();
    stringMessage.setMessage(message.getName().toUpperCase());
    return stringMessage;
  }

}
