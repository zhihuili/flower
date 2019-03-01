package com.ly.train.flower.common.sample.textflow;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.containe.ServiceContext;

public class Service3 implements Service<Message2> {

  @Override
  public Object process(Message2 message, ServiceContext context) {
    return message.getName().toUpperCase();
  }

}
