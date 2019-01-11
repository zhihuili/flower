package com.ly.train.flower.common.actor;

import java.util.HashSet;
import java.util.Set;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.ServiceFactory;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.message.BlockedMessage;
import com.ly.train.flower.common.service.message.ReturnMessage;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class ServiceActor extends UntypedActor {

  Service service;

  Set<ActorRef> nextServiceActors;

  ActorRef caller;

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
    if (arg0 == null)
      return;

    // receive returned message，send to caller
    if (arg0 instanceof ReturnMessage) {
      caller.tell(arg0, getSelf());
      return;
    }

    // receive started message, set caller
    if (arg0 instanceof BlockedMessage)
      caller = getSender();

    Object o = service.process(arg0);
    if (o == null)// for joint service
      return;
    if (nextServiceActors != null && !nextServiceActors.isEmpty()) {
      for (ActorRef actor : nextServiceActors) {
        actor.tell(o, getSelf());
      }
    }
  }

  /**
   * clear actor
   */
  private void clear() {
  }

}
