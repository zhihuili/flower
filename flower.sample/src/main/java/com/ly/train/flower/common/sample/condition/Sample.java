package com.ly.train.flower.common.sample.condition;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.containe.ServiceFactory;
import com.ly.train.flower.common.service.containe.ServiceLoader;

public class Sample {

  public static void main(String[] args) throws Exception {
    buildServiceEnv();

    ServiceFacade.asyncCallService("sample", "serviceA", "b");
  }

  public static void buildServiceEnv() {
    ServiceFactory.registerService("serviceA",
        "com.ly.train.flower.common.sample.condition.ServiceA");
    ServiceFactory.registerService("serviceB",
        "com.ly.train.flower.common.sample.condition.ServiceB");
    ServiceFactory.registerService("serviceC",
        "com.ly.train.flower.common.sample.condition.ServiceC");

    // serviceA -> serviceB
    //          -> serviceC
    ServiceFlow.buildFlow("sample", "serviceA", "serviceB");
    ServiceFlow.buildFlow("sample", "serviceA", "serviceC");

  }

}
