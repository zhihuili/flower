package com.ly.train.flower.common.sample.web;

import com.ly.train.flower.common.service.HttpService;
import com.ly.train.flower.common.service.web.Flush;
import com.ly.train.flower.common.service.web.Last;
import com.ly.train.flower.common.service.web.Web;

public class FlowService implements HttpService, Last, Flush {

  private ClassA ca;

  public FlowService() {
    this.ca = new ClassA();
  }

  @Override
  /**
   * trim service
   */
  public Object process(Object message, Web web) throws Exception {
    ca.f();

    web.println(message.toString());
    Thread.sleep(5000);

    System.out.println("FlowService processed "+message.toString());
    return "";
  }

}
