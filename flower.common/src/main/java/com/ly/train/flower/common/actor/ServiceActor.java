/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.common.actor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.service.Aggregate;
import com.ly.train.flower.common.service.Complete;
import com.ly.train.flower.common.service.FlowerService;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.ServiceConstants;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.container.FlowContext;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.service.container.ServiceLoader;
import com.ly.train.flower.common.service.message.Condition;
import com.ly.train.flower.common.service.message.DefaultMessage;
import com.ly.train.flower.common.service.message.FirstMessage;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.message.ReturnMessage;
import com.ly.train.flower.common.service.web.Flush;
import com.ly.train.flower.common.service.web.HttpComplete;
import com.ly.train.flower.common.service.web.Web;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
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
public class ServiceActor extends AbstractActor {
  static final Logger logger = LoggerFactory.getLogger(ServiceActor.class);
  private ActorSystem system;
  private FlowerService service;
  private String serviceName;
  private Set<RefType> nextServiceActors;

  private Map<String, ActorRef> callers = new ConcurrentHashMap<String, ActorRef>();

  protected final Future<String> delayFuture = Futures.successful("delay");
  protected final FiniteDuration maxTimeout = Duration.create(9999, TimeUnit.DAYS);

  class RefType {
    private ActorRef actorRef;
    private Class<?> messageType;
    private String serviceName;
    private boolean isJoint = false;

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

    public Class<?> getMessageType() {
      return messageType;
    }

    public void setMessageType(Class<?> messageType) {
      this.messageType = messageType;
    }

    public String getServiceName() {
      return serviceName;
    }

    public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
    }

  }

  public ServiceActor(String flowName, String serviceName, int index, ActorSystem system) throws Exception {
    this.system = system;
    this.serviceName = serviceName;
    this.service = ServiceFactory.getService(serviceName);
    if (service instanceof Aggregate) {
      ((Aggregate) service).setSourceNumber(ServiceFlow.getServiceConcig(flowName, serviceName).getJointSourceNumber());
    }
    this.nextServiceActors = new HashSet<RefType>();
    Set<String> nextServiceNames = ServiceFlow.getNextFlow(flowName, serviceName);
    if (nextServiceNames != null && !nextServiceNames.isEmpty()) {
      for (String str : nextServiceNames) {
        RefType refType = new RefType();

        if (ServiceFactory.getServiceClassName(str).equals(ServiceConstants.AGGREGATE_SERVICE_NAME)) {
          refType.setJoint(true);
        }
        refType.setActorRef(ServiceActorFactory.buildServiceActor(flowName, str, index));
        refType.setMessageType(ServiceLoader.getInstance().getServiceMessageType(str));
        refType.setServiceName(str);
        nextServiceActors.add(refType);
      }
    }
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder().match(FlowMessage.class, fm -> {
      try {
        onReceive(fm);
      } catch (Throwable e) {
        logger.error("", e);
      }
    }).matchAny(no -> {
      logger.warn("unhandled message, so discard it. {}", no);
    }).build();
  }

  public void onReceive(FlowMessage fm) throws Throwable {

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
      this.service = ServiceFactory.getService(serviceName);
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
              || stringInStrings(refType.getServiceName(), ((Condition) o).getCondition().toString())) {
            refType.getActorRef().tell(flowMessage, getSelf());
          }
        }
      }
    } else {

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
  void clear() {}
}
