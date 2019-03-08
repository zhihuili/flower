package com.ly.train.flower.common.actor;

import static java.util.concurrent.TimeUnit.DAYS;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ly.train.flower.common.service.Aggregate;
import com.ly.train.flower.common.service.Complete;
import com.ly.train.flower.common.service.FlowerService;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.ServiceConstants;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.containe.FlowContext;
import com.ly.train.flower.common.service.containe.ServiceContext;
import com.ly.train.flower.common.service.containe.ServiceFactory;
import com.ly.train.flower.common.service.containe.ServiceLoader;
import com.ly.train.flower.common.service.message.Condition;
import com.ly.train.flower.common.service.message.DefaultMessage;
import com.ly.train.flower.common.service.message.FirstMessage;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.message.ReturnMessage;
import com.ly.train.flower.common.service.web.Flush;
import com.ly.train.flower.common.service.web.HttpComplete;
import com.ly.train.flower.common.service.web.Web;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * Wrap service by actor, make service driven by message.
 * 
 * @author zhihui.li
 *
 */
public class ServiceActor extends UntypedActor {
  ActorSystem system;
  FlowerService service;

  Set<RefType> nextServiceActors;

  Map<String, ActorRef> callers = new ConcurrentHashMap<String, ActorRef>();

  final Future<String> delayFuture = Futures.successful("delay");
  final FiniteDuration maxTimeout = Duration.create(9999, DAYS);

  class RefType {
    ActorRef actorRef;
    Class messageType;
    String serviceName;
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

    public Class getMessageType() {
      return messageType;
    }

    public void setMessageType(Class messageType) {
      this.messageType = messageType;
    }

    public String getServiceName() {
      return serviceName;
    }

    public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
    }

  }

  public ServiceActor(String flowName, String serviceName, int index, ActorSystem system)
      throws Exception {
    this.system = system;
    this.service = ServiceFactory.getService(serviceName);
    if (service instanceof Aggregate) {
      ((Aggregate) service).setSourceNumber(
          ServiceFlow.getServiceConcig(flowName, serviceName).getJointSourceNumber());
    }
    nextServiceActors = new HashSet<RefType>();
    Set<String> nextServiceNames = ServiceFlow.getNextFlow(flowName, serviceName);
    if (nextServiceNames != null && !nextServiceNames.isEmpty()) {
      for (String str : nextServiceNames) {
        RefType refType = new RefType();
        refType.setActorRef(ServiceActorFactory.buildServiceActor(flowName, str, index));
        refType.setMessageType(ServiceLoader.getInstance().getServiceMessageType(str));
        refType.setServiceName(str);

        if (ServiceFactory.getServiceClassName(str)
                .equals(ServiceConstants.AGGREGATE_SERVICE_NAME)) {
          refType.setJoint(true);
          AggregateServiceActorTimer.getInstance().add(refType.getActorRef());
        }
        nextServiceActors.add(refType);
      }
    }
  }

  @Override
  public void onReceive(Object arg0) throws Throwable {
    if (arg0 == null || !(arg0 instanceof FlowMessage)) {
      return;
    }

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
    try {
      o = ((Service) service).process(fm.getMessage(), context);
    } catch (Exception e) {
      Web web = context.getWeb();
      FlowContext.removeServiceContext(fm.getTransactionId());
      if (web != null) {
        web.complete();
      }
      throw e;
    }

    Web web = context.getWeb();
    if (service instanceof Complete) {
      FlowContext.removeServiceContext(fm.getTransactionId());
    }
    if (web != null) {
      if (service instanceof Flush) {
        web.flush();
      }
      if (service instanceof HttpComplete || service instanceof Complete) {
        web.complete();
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
        // condition fork for one-service to multi-service
        if (refType.getMessageType().isInstance(o)) {
          if (!(o instanceof Condition) || !(((Condition) o).getCondition() instanceof String)
              || stringInStrings(refType.getServiceName(),
                  ((Condition) o).getCondition().toString())) {
            refType.getActorRef().tell(flowMessage, getSelf());
          }
        }
      }
    }
  }

  /**
   * Is String s in String ss?
   * 
   * @param s "service1"
   * @param ss “service1,service2”
   * @return
   */
  private boolean stringInStrings(String s, String ss) {
    String[] sa = ss.split(",");
    if (sa != null && sa.length > 0) {
      for (String se : sa) {
        if (se.equals(s)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * clear actor
   */
  private void clear() {
  }
}
