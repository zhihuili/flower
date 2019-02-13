package com.ly.train.flower.common.sample.web;

import com.ly.train.flower.common.service.AfterDelay;
import com.ly.train.flower.common.service.HttpService;
import com.ly.train.flower.common.service.web.Flush;
import com.ly.train.flower.common.service.web.Complete;
import com.ly.train.flower.common.service.web.Web;

public class FlowService implements HttpService, Complete, Flush {


  public FlowService() {
  }

  @Override
  /**
   * trim service
   */
  public Object process(Object message, Web web) throws Exception {

    web.println(" - end:" + System.currentTimeMillis());
    return "";
  }

  public long delay() {
    return 100;
  }

}
