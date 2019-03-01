package com.ly.train.flower.common.sample.Supervisor;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.containe.ServiceContext;

public class Service1 implements Service {

  @Override
  public Object process(Object message, ServiceContext context) {
    return ((Message1) message).getM2();
  }

}
