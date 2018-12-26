package com.ly.train.flower.common.actor;

public class ServiceFacade {
  public static void callService(String serviceName, Object o) throws Exception {
    ServiceActorFactory.buildServiceActor(serviceName).tell(o, null);
  }
}
