package com.ly.train.flower.common.service;

import com.ly.train.flower.common.service.containe.ServiceContext;

public class NothingService implements Service {

  @Override
  public Object process(Object message, ServiceContext context) {
    return null;
  }

}
