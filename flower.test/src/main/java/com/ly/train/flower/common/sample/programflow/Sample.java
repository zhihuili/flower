package com.ly.train.flower.common.sample.programflow;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.service.ServiceFactory;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.ServiceLoader;

public class Sample {

  public static void main(String[] args) throws Exception {
    buildServiceEnv();
    // ActorRef actor = ServiceActorFactory.buildServiceActor("serviceA");
    // actor.tell(" Hello World!", null);

    for (int i = 0; i < 5; i++) {
      ServiceFacade.asyncCallService("sample", "serviceA", " Hello World! ");
    }

    Thread.sleep(1000);
    System.exit(0);
  }

  public static void buildServiceEnv() {
    ServiceFactory.registerService("serviceA", "com.ly.train.flower.common.sample.programflow.ServiceA");
    ServiceFactory.registerService("serviceB", "com.ly.train.flower.common.sample.programflow.ServiceB");
    ServiceFactory.registerService("serviceC", "com.ly.train.flower.common.sample.programflow.ServiceC");

    // serviceA -> serviceB -> serviceC
    ServiceFlow.buildFlow("sample", "serviceA", "serviceB");
    ServiceFlow.buildFlow("sample", "serviceB", "serviceC");

  }

}
