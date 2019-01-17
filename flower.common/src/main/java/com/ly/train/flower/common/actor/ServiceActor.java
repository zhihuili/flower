package com.ly.train.flower.common.actor;

import java.util.*;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.ServiceFactory;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.message.FirstMessage;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.message.ReturnMessage;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class ServiceActor extends UntypedActor {

  Service service;

  Set<ActorRef> nextServiceActors;

  Map<String,ActorRef> callers = new HashMap<String,ActorRef>();

  public ServiceActor(String flowName, String serviceName) throws Exception {
    this.service = ServiceFactory.getService(serviceName);
    nextServiceActors = new HashSet<ActorRef>();
    Set<String> nextServiceNames = ServiceFlow.getNextFlow(flowName, serviceName);
    if (nextServiceNames != null && !nextServiceNames.isEmpty()) {
      for (String str : nextServiceNames) {
        nextServiceActors.add(ServiceActorFactory.buildServiceActor(flowName,str));
      }
    }
  }

  @Override
  public void onReceive(Object arg0) throws Throwable {
    if (arg0 == null || ! (arg0 instanceof FlowMessage))
      return;
    FlowMessage fm = (FlowMessage) arg0;

    // receive returned message，send to caller
    if (arg0 instanceof ReturnMessage) {
      callers.get(fm.getTransactionId()).tell(arg0, getSelf());
      callers.remove(fm.getTransactionId());
      return;
    }

    // receive started message, set caller
    if (arg0 instanceof FirstMessage)
      fm.setTransactionId(UUID.randomUUID().toString());
      callers.put(fm.getTransactionId(), getSender());


    Object o = service.process(arg0);
    if (o == null)// for joint service
      return;

    ( (FlowMessage) o).setTransactionId(fm.getTransactionId());
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
