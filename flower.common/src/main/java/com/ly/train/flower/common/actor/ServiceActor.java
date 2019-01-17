package com.ly.train.flower.common.actor;

import java.util.*;

import com.ly.train.flower.common.service.JointService;
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

  Set<RefType> nextServiceActors;

  Map<String,ActorRef> callers = new HashMap<String,ActorRef>();

  class RefType {
    ActorRef actorRef;
    boolean isJoint = false;

    public ActorRef getActorRef() {
      return actorRef;
    }

    public void setActorRef(ActorRef actorRef) {
      this.actorRef = actorRef;
    }

    public boolean isJoint() {
      return isJoint;
    }

    public void setJoint(boolean joint) {
      isJoint = joint;
    }
  }
  public ServiceActor(String flowName, String serviceName) throws Exception {
    this.service = ServiceFactory.getService(serviceName);
    nextServiceActors = new HashSet<RefType>();
    Set<String> nextServiceNames = ServiceFlow.getNextFlow(flowName, serviceName);
    if (nextServiceNames != null && !nextServiceNames.isEmpty()) {
      for (String str : nextServiceNames) {
        RefType refType = new RefType();

        if (ServiceFactory.getService(str) instanceof JointService) {
          refType.setJoint(true);
        }
        refType.setActorRef(ServiceActorFactory.buildServiceActor(flowName,str));
        nextServiceActors.add(refType);
      }
    }
  }

  @Override
  public void onReceive(Object arg0) throws Throwable {
    if (arg0 == null || ! (arg0 instanceof FlowMessage))
      return;

    FlowMessage fm = (FlowMessage) arg0;

    // receive returned message，send to caller
    if (fm.getMessage() instanceof ReturnMessage) {
      callers.get(fm.getTransactionId()).tell(fm.getMessage(), getSelf());
      callers.remove(fm.getTransactionId());
      return;
    }

    // receive started message, set caller
    if (fm.getMessage() instanceof FirstMessage) {
      fm.setTransactionId(UUID.randomUUID().toString());
      callers.put(fm.getTransactionId(), getSender());
    }



    Object o = service.process(fm.getMessage());
    if (o == null)// for joint service
      return;
    FlowMessage flowMessage = new FlowMessage();
    flowMessage.setMessage(o);
    flowMessage.setTransactionId(fm.getTransactionId());
    if (nextServiceActors != null && !nextServiceActors.isEmpty()) {
      for (RefType refType : nextServiceActors) {
        if (refType.isJoint()) {
          FlowMessage flowMessage1 = new FlowMessage();
          flowMessage1.setMessage(o);
          flowMessage1.setTransactionId(fm.getTransactionId());
          flowMessage.setMessage(flowMessage1);
        }
        refType.getActorRef().tell(flowMessage, getSelf());
      }
    }
  }

  /**
   * clear actor
   */
  private void clear() {
  }

}
