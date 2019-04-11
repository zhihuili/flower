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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import com.ly.train.flower.common.akka.ServiceRouter;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.service.Aggregate;
import com.ly.train.flower.common.service.Complete;
import com.ly.train.flower.common.service.FlowerService;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.container.util.ServiceContextUtil;
import com.ly.train.flower.common.service.impl.AggregateService;
import com.ly.train.flower.common.service.message.Condition;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.web.Flush;
import com.ly.train.flower.common.service.web.HttpComplete;
import com.ly.train.flower.common.service.web.Web;
import com.ly.train.flower.common.util.ClassUtil;
import com.ly.train.flower.common.util.CloneUtil;
import com.ly.train.flower.common.util.CollectionUtil;
import com.ly.train.flower.common.util.StringUtil;
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
public class ServiceActor extends AbstractFlowerActor {
  /**
   * 同步要求结果的actor
   */
  private static final Map<String, ActorRef> syncActors = new ConcurrentHashMap<String, ActorRef>();

  protected final Future<String> delayFuture = Futures.successful("delay");
  protected final FiniteDuration maxTimeout = Duration.create(9999, TimeUnit.DAYS);

  private FlowerService service;
  private int count;
  private final FlowerFactory flowerFactory;

  static public Props props(String serviceName, FlowerFactory flowerFactory, int count) {
    return Props.create(ServiceActor.class, serviceName, flowerFactory, count);
  }

  /**
   * 当前Actor绑定的服务
   */
  private String serviceName;

  public ServiceActor(String serviceName, FlowerFactory flowerFactory, int count) {
    this.serviceName = serviceName;
    this.count = count;
    this.flowerFactory = flowerFactory;
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
      ServiceContextUtil.fillServiceContext(serviceContext);

      result = ((Service) getService(serviceContext)).process(fm.getMessage(), serviceContext);
    } catch (Throwable e) {
      Web web = serviceContext.getWeb();
      if (web != null) {
        web.complete();
      }
      throw new FlowerException("fail to invoke service " + serviceContext.getCurrentServiceName() + " : " + service
          + ", param : " + fm.getMessage(), e);
    }

    // logger.info("同步处理 ： {}, hasChild : {}", serviceContext.isSync(),
    // hasChildActor());
    Set<RefType> nextActorRef = getNextServiceActors(serviceContext);
    if (serviceContext.isSync() && CollectionUtil.isEmpty(nextActorRef)) {
      ActorRef actor = syncActors.get(serviceContext.getId());
      if (actor != null) {
        actor.tell(result, getSelf());
        syncActors.remove(serviceContext.getId());
      }
      return;
    }

    Web web = serviceContext.getWeb();
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
    Set<RefType> refTypes = getNextServiceActors(serviceContext);
    if (refTypes == null) {
      return;
    }
    ServiceContextUtil.cleanServiceContext(serviceContext);
    for (RefType refType : refTypes) {
      Object resultClone = CloneUtil.clone(result);
      ServiceContext context = serviceContext.newInstance();
      context.getFlowMessage().setMessage(resultClone);
      // condition fork for one-service to multi-service
      if (refType.getMessageType().isInstance(result)) {
        if (!(result instanceof Condition) || !(((Condition) result).getCondition() instanceof String)
            || stringInStrings(refType.getServiceName(), ((Condition) result).getCondition().toString())) {
          context.setCurrentServiceName(refType.getServiceName());
          refType.getServiceRouter().asyncCallService(context, getSelf());
        }
      }
    }

  }

  /**
   * 懒加载方式获取服务实例
   * 
   * @return {@link FlowerService}
   */
  public FlowerService getService(ServiceContext serviceContext) {
    if (this.service == null) {
      this.service = flowerFactory.getServiceFactory().getServiceLoader().loadService(serviceName);
      if (service instanceof Aggregate) {
        int num = flowerFactory.getServiceFactory()
            .getOrCreateServiceFlow(serviceContext.getFlowName()).getServiceConfig(serviceName).getJointSourceNumber();
        ((AggregateService) service).setSourceNumber(num);
      }
    }
    return service;
  }

  private static final ConcurrentMap<String, Set<RefType>> nextServiceActorCache = new ConcurrentHashMap<>();

  private Set<RefType> getNextServiceActors(ServiceContext serviceContext) {
    final String cacheKey = serviceContext.getFlowName() + "_" + serviceContext.getCurrentServiceName();
    Set<RefType> nextServiceActors = nextServiceActorCache.get(cacheKey);
    if (nextServiceActors == null && StringUtil.isNotBlank(serviceContext.getFlowName())) {
      nextServiceActors = new HashSet<>();
      Set<ServiceConfig> serviceConfigs = flowerFactory.getServiceFactory()
          .getOrCreateServiceFlow(serviceContext.getFlowName()).getNextFlow(serviceContext.getCurrentServiceName());
      if (serviceConfigs != null) {
        for (ServiceConfig serviceConfig : serviceConfigs) {
          flowerFactory.getServiceFactory().loadServiceMeta(serviceConfig);// 内部对serviceConfig的数据进行填充
          RefType refType = new RefType();
          refType.setAggregate(serviceConfig.isAggregateService());
          refType.setServiceRouter(flowerFactory.getServiceActorFactory().buildServiceRouter(serviceConfig, count));
          refType.setMessageType(ClassUtil.forName(serviceConfig.getServiceMeta().getParamType()));
          refType.setServiceName(serviceConfig.getServiceName());
          nextServiceActors.add(refType);
        }
        nextServiceActorCache.put(cacheKey, nextServiceActors);
      }
    }

    return nextServiceActors;
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
    private ServiceRouter serviceRouter;
    private Class<?> messageType;
    private String serviceName;
    private boolean aggregate;

    public void setServiceRouter(ServiceRouter serviceRouter) {
      this.serviceRouter = serviceRouter;
    }

    public ServiceRouter getServiceRouter() {
      return serviceRouter;
    }

    public boolean isAggregate() {
      return aggregate;
    }

    public void setAggregate(boolean aggregate) {
      this.aggregate = aggregate;
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
