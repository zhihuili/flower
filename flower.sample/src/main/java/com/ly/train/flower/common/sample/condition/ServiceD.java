package com.ly.train.flower.common.sample.condition;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.containe.ServiceContext;

public class ServiceD implements Service {

  @Override
  public Object process(Object message, ServiceContext context) {
    System.out.println("I am Service D.");
    return null;
  }

}
