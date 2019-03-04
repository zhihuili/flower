package com.ly.train.flower.common.sample.springboot;

import com.ly.flower.web.springboot.annotation.BindController;
import com.ly.train.flower.common.service.Complete;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.containe.ServiceContext;
import com.ly.train.flower.common.service.web.Flush;
import org.springframework.web.bind.annotation.RequestMethod;

public class ServiceB implements Service<Integer>, Flush, Complete {
  @Override
  public Object process(Integer message, ServiceContext context) throws Exception {
    context.getWeb().println("ServiceB:" + String.valueOf(message));
    return null;
  }
}
