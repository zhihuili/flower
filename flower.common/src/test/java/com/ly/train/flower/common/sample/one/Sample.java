package com.ly.train.flower.common.sample.one;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.service.ServiceFactory;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.ServiceLoader;

public class Sample {

  public static void main(String[] args) throws Exception {
    buildServiceEnv();

    Message2 m2 = new Message2(10, "Zhihui");
    Message1 m1 = new Message1();
    m1.setM2(m2);

    System.out.println(ServiceFacade.syncCallService("sample", "service1", m1));

    Thread.sleep(2000);
    System.exit(0);
  }

  public static void buildServiceEnv() {
    ServiceFactory.registerService("service1",
        ServiceLoader.getInstance().loadService("com.ly.train.flower.common.sample.one.Service1"));
    ServiceFactory.registerService("service2",
        ServiceLoader.getInstance().loadService("com.ly.train.flower.common.sample.one.Service2"));
    ServiceFactory.registerService("service3",
        ServiceLoader.getInstance().loadService("com.ly.train.flower.common.sample.one.Service3"));
    ServiceFactory.registerService("service4",
        ServiceLoader.getInstance().loadService("com.ly.train.flower.common.sample.one.Service4"));
    ServiceFactory.registerService("service5",
        ServiceLoader.getInstance().loadService("com.ly.train.flower.common.service.JointService"));

    ServiceFlow.buildFlow("sample", "service1", "service2");
    ServiceFlow.buildFlow("sample", "service1", "service3");
    ServiceFlow.buildFlow("sample", "service2", "service5");
    ServiceFlow.buildFlow("sample", "service3", "service5");
    ServiceFlow.buildFlow("sample", "service5", "service4");
    ServiceFlow.buildFlow("sample", "service4", "service1");
  }

}
