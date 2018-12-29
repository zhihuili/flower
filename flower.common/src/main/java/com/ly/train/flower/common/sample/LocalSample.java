package com.ly.train.flower.common.sample;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.service.ServiceFactory;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.ServiceLoader;

public class LocalSample {

  public static void main(String[] args) throws Exception {
    buildServiceEnv();
    // ActorRef actor = ServiceActorFactory.buildServiceActor("serviceA");
    // actor.tell(" Hello World!", null);

    for (int i = 0; i < 5; i++) {
      ServiceFacade.callService("serviceA", " Hello World! ");
    }
    
    Thread.sleep(1000);
    System.exit(0);
  }

  public static void buildServiceEnv() {
    ServiceFactory.registerService("serviceA",
        ServiceLoader.getInstance().loadService("com.ly.train.flower.common.sample.ServiceA"));
    ServiceFactory.registerService("serviceB",
        ServiceLoader.getInstance().loadService("com.ly.train.flower.common.sample.ServiceB"));
    ServiceFactory.registerService("serviceC",
        ServiceLoader.getInstance().loadService("com.ly.train.flower.common.sample.ServiceC"));

    ServiceFlow.buildFlow("serviceA", "serviceB");
    ServiceFlow.buildFlow("serviceB", "serviceC");

  }

}
