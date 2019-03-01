package com.ly.train.flower.common.sample.web.async;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.containe.ServiceContext;

public class ServiceA implements Service<String> {

  @Override
  public Object process(String message, ServiceContext context) throws Exception {
    // id非Integer的时候会抛出一个异常
    Integer result = Integer.valueOf(context.getWeb().getParameter("id"));
    return result;
  }

}
