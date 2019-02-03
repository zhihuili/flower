package com.ly.train.flower.common.actor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ly.train.flower.common.service.FlowerService;
import com.ly.train.flower.common.service.HttpService;
import com.ly.train.flower.common.service.Joint;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.ServiceConstants;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.containe.FlowContext;
import com.ly.train.flower.common.service.containe.ServiceContext;
import com.ly.train.flower.common.service.containe.ServiceFactory;
import com.ly.train.flower.common.service.message.DefaultMessage;
import com.ly.train.flower.common.service.message.FirstMessage;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.message.ReturnMessage;
import com.ly.train.flower.common.service.web.Flush;
import com.ly.train.flower.common.service.web.Last;
import com.ly.train.flower.common.service.web.Web;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * Wrap service by actor, make service driven by message.
 * 
 * @author zhihui.li
 *
 */
public class ServiceActor extends UntypedActor {

  FlowerService service;

  Set<RefType> nextServiceActors;

  Map<String, ActorRef> callers = new ConcurrentHashMap<String, ActorRef>();

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

  public ServiceActor(String flowName, String serviceName, int index) throws Exception {
    this.service = ServiceFactory.getService(serviceName);
    if (service instanceof Joint) {
      ((Joint) service).setSourceNumber(
          ServiceFlow.getServiceConcig(flowName, serviceName).getJointSourceNumber());
    }
    nextServiceActors = new HashSet<RefType>();
    Set<String> nextServiceNames = ServiceFlow.getNextFlow(flowName, serviceName);
    if (nextServiceNames != null && !nextServiceNames.isEmpty()) {
      for (String str : nextServiceNames) {
        RefType refType = new RefType();

        if (ServiceFactory.getServiceClassName(str).equals(ServiceConstants.JOINT_SERVICE_NAME)) {
          refType.setJoint(true);
        }
        refType.setActorRef(ServiceActorFactory.buildServiceActor(flowName, str, index));
        nextServiceActors.add(refType);
      }
    }
  }

  @Override
  public void onReceive(Object arg0) throws Throwable {
    if (arg0 == null || !(arg0 instanceof FlowMessage))
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
      callers.put(fm.getTransactionId(), getSender());
    }

    ServiceContext context = FlowContext.getServiceContext(fm.getTransactionId());
    Object o = DefaultMessage.getMessage();// set default
    if (service instanceof HttpService) {
      if (context != null) {
        ((HttpService) service).process(fm.getMessage(), context.getWeb());
      }
    }
    if (service instanceof Service) {
      o = ((Service) service).process(fm.getMessage());
    }
    if (context != null) {
      Web web = context.getWeb();
      if (web != null) {
        if (service instanceof Flush) {
          web.flush();
        }
        if (service instanceof Last) {
          web.complete();
          FlowContext.removeServiceContext(fm.getTransactionId());
        }
      }
    }
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
