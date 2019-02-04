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

    Thread.sleep(100);
    web.println(" - end:" + System.currentTimeMillis());
    // System.out.println("FlowService processed "+message.toString());
    return "";
  }

}
