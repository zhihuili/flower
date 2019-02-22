package com.ly.train.flower.common.sample.web.async;

import com.ly.train.flower.common.service.HttpService;
import com.ly.train.flower.common.service.web.Web;

public class ServiceA implements HttpService<String> {

  @Override
  public Object process(String message, Web web) throws Exception {
    //id非Integer的时候会抛出一个异常
    Integer result = Integer.valueOf(web.getParameter("id"));
    return result;
  }

}
