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
package com.ly.train.flower.common.akka.actor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.akka.ServiceActorFactory;
import com.ly.train.flower.common.akka.actor.wrapper.ActorWrapper;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.service.Aggregate;
import com.ly.train.flower.common.service.Complete;
import com.ly.train.flower.common.service.FlowerService;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.service.container.ServiceFlow;
import com.ly.train.flower.common.service.impl.AggregateService;
import com.ly.train.flower.common.service.message.Condition;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.web.Flush;
import com.ly.train.flower.common.service.web.HttpComplete;
import com.ly.train.flower.common.service.web.Web;
import com.ly.train.flower.common.util.CloneUtil;
import akka.actor.ActorRef;
import akka.actor.Props;
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
public class ServiceActorV1 extends AbstractFlowerActor {
  static final Logger logger = LoggerFactory.getLogger(ServiceActorV1.class);
  /**
   * 同步要求结果的actor
   */
  private static final Map<String, ActorRef> syncActors = new ConcurrentHashMap<String, ActorRef>();

  protected final Future<String> delayFuture = Futures.successful("delay");
  protected final FiniteDuration maxTimeout = Duration.create(9999, TimeUnit.DAYS);

  private FlowerService service;
  private String serviceName;
  private String flowName;
  private final Set<RefType> nextServiceActors;
  private ServiceActorFactory serviceActorFactory;
  private ServiceFactory serviceFactory;

  static public Props props(String flowName, String serviceName, int index, FlowerFactory flowerFactory) {
    return Props.create(ServiceActorV1.class, () -> new ServiceActorV1(flowName, serviceName, index, flowerFactory));
  }

  public ServiceActorV1(String flowName, String serviceName, int index, FlowerFactory flowerFactory) throws Exception {
    this.flowName = flowName;
    this.serviceName = serviceName;
    this.nextServiceActors = new HashSet<RefType>();
    this.serviceActorFactory = flowerFactory.getServiceActorFactory();
    this.serviceFactory = flowerFactory.getServiceFactory();
    Set<ServiceConfig> serviceConfigs = ServiceFlow.getOrCreate(flowName, serviceFactory).getNextFlow(serviceName);
    if (serviceConfigs != null) {
      for (ServiceConfig serviceConfig : serviceConfigs) {
        RefType refType = new RefType();

        if (serviceConfig.isAggregateService()) {
          refType.setJoint(true);
        }
        refType.setActorRef(serviceActorFactory.buildServiceActor(serviceConfig, index));
        refType.setMessageType(
            serviceFactory.getServiceLoader().loadServiceMeta(serviceConfig.getServiceName()).getParamType());
        refType.setServiceName(serviceConfig.getServiceName());
        nextServiceActors.add(refType);
      }
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public void onServiceContextReceived(ServiceContext serviceContext) throws Throwable {
    FlowMessage fm = serviceContext.getFlowMessage();
    if (needCacheActorRef(serviceContext)) {
      syncActors.putIfAbsent(serviceContext.getId(), getSender());
    }

    Object result = null;
    try {
      result = ((Service) getService()).process(fm.getMessage(), serviceContext);
    } catch (Throwable e) {
      Web web = serviceContext.getWeb();
      if (web != null) {
        web.complete();
      }
      throw new FlowerException(
          "fail to invoke service " + serviceName + " : " + service + ", param : " + fm.getMessage(), e);
    }

    // logger.info("同步处理 ： {}, hasChild : {}", serviceContext.isSync(),
    // hasChildActor());
    if (serviceContext.isSync() && hasNoChildActor()) {
      // logger.info("返回响应 {}", result);
      ActorRef actor = syncActors.get(serviceContext.getId());
      if (actor != null) {
        actor.tell(result, getSelf());
        syncActors.remove(serviceContext.getId());
      }
      return;
    }

    Web web = serviceContext.getWeb();
    if (service instanceof Complete) {
      // FlowContext.removeServiceContext(fm.getTransactionId());
    }
    if (web != null) {
      if (service instanceof Flush) {
        web.flush();
      }
      if (service instanceof HttpComplete || service instanceof Complete) {
        web.complete();
      }
    }

    if (result == null) {// for joint service
      return;
    }

    for (RefType refType : nextServiceActors) {
      Object resultClone = CloneUtil.clone(result);
      ServiceContext context = serviceContext.newInstance();
      context.getFlowMessage().setMessage(resultClone);

      // condition fork for one-service to multi-service
      if (refType.getMessageType().isInstance(result)) {
        if (!(result instanceof Condition) || !(((Condition) result).getCondition() instanceof String)
            || stringInStrings(refType.getServiceName(), ((Condition) result).getCondition().toString())) {
          refType.getActorRef().tell(context, getSelf());
        }
      }
    }

  }

  /**
   * 懒加载方式获取服务实例
   * 
   * @return {@link FlowerService}
   */
  public FlowerService getService() {
    if (this.service == null) {
      this.service = serviceFactory.getServiceLoader().loadService(serviceName);
      if (service instanceof Aggregate) {
        ((AggregateService) service).setSourceNumber(
            ServiceFlow.getOrCreate(flowName, null).getServiceConfig(serviceName).getJointSourceNumber());
      }
    }
    return service;
  }

  /**
   * 有子服务节点
   * 
   * @return
   */
  private boolean hasChildActor() {
    return nextServiceActors != null && nextServiceActors.size() > 0;
  }

  /**
   * 没有子服务节点
   * 
   * @return
   */
  private boolean hasNoChildActor() {
    return !hasChildActor();
  }

  private boolean needCacheActorRef(ServiceContext serviceContext) {
    return serviceContext.isSync() && !syncActors.containsKey(serviceContext.getId());
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

  static class RefType {
    private ActorWrapper actorRef;
    private Class<?> messageType;
    private String serviceName;
    private boolean isJoint = false;

    public ActorWrapper getActorRef() {
      return actorRef;
    }

    public void setActorRef(ActorWrapper actorRef) {
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

  /**
   * clear actor
   */
  void clear(String id) {
    syncActors.remove(id);
  }
}
