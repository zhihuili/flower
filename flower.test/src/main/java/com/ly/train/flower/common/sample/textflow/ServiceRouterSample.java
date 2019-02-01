package com.ly.train.flower.common.sample.textflow;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.service.EnvBuilder;

public class ServiceRouterSample {

  public static void main(String[] args) throws Exception {

    int loopNumber = 500;

    EnvBuilder.buildEnv();

    Message2 m2 = new Message2(10, "Zhihui");
    Message1 m1 = new Message1();
    m1.setM2(m2);

    // 200 flows

    ServiceRouter sr = ServiceFacade.buildServiceRouter("sample", "service1", 200);
    long begin = System.currentTimeMillis();
    for (int i = 0; i < loopNumber; i++) {
      sr.asyncCallService(m1);
    }
    long end = System.currentTimeMillis();
    System.out.println("200 flows cost time: " + (end - begin));

    // single flow
    begin = System.currentTimeMillis();
    for (int i = 0; i < loopNumber; i++) {
      // ServiceFacade.asyncCallService("sample", "service1", m1);
    }
    end = System.currentTimeMillis();
    System.out.println("single flow cost time: " + (end - begin));
  }

}
