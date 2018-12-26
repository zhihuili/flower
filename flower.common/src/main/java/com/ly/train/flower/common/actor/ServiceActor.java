package com.ly.train.flower.common.actor;

import java.util.HashSet;
import java.util.Set;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.ServiceFactory;
import com.ly.train.flower.common.service.ServiceFlow;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class ServiceActor extends UntypedActor {

  Service service;

  Set<ActorRef> nextServiceActors;

  public ServiceActor(String serviceName) throws Exception {
    this.service = ServiceFactory.getService(serviceName);
    nextServiceActors = new HashSet<ActorRef>();
    Set<String> nextServiceNames = ServiceFlow.getNextFlow(serviceName);
    if (nextServiceNames != null && !nextServiceNames.isEmpty()) {
      for (String str : nextServiceNames) {
        nextServiceActors.add(ServiceActorFactory.buildServiceActor(str));
      }
    }
  }

  @Override
  public void onReceive(Object arg0) throws Throwable {
    Object o = service.process(arg0);
    if (nextServiceActors != null && !nextServiceActors.isEmpty()) {
      for (ActorRef actor : nextServiceActors) {
        actor.tell(o, getSelf());
      }
    }
  }

}
