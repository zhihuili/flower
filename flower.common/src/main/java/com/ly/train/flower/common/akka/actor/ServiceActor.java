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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import com.ly.train.flower.common.akka.actor.wrapper.ActorWrapper;
import com.ly.train.flower.common.exception.ServiceException;
import com.ly.train.flower.common.serializer.Codec;
import com.ly.train.flower.common.service.Aggregate;
import com.ly.train.flower.common.service.Complete;
import com.ly.train.flower.common.service.FlowerService;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.container.ServiceMeta;
import com.ly.train.flower.common.service.container.util.ServiceContextUtil;
import com.ly.train.flower.common.service.impl.AggregateService;
import com.ly.train.flower.common.service.message.Condition;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.web.Flush;
import com.ly.train.flower.common.service.web.HttpComplete;
import com.ly.train.flower.common.service.web.Web;
import com.ly.train.flower.common.util.ClassUtil;
import com.ly.train.flower.common.util.CollectionUtil;
import com.ly.train.flower.common.util.ExceptionUtil;
import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.common.util.cache.Cache;
import com.ly.train.flower.common.util.cache.CacheManager;
import com.ly.train.flower.filter.Filter;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Wrap service by actor, make service driven by message.
 * 
 * @author zhihui.li
 * 
 */
public class ServiceActor extends AbstractFlowerActor {

  public static final Long defaultTimeToLive = TimeUnit.SECONDS.toMillis(60);
  private static final String serviceActorCachePrefix = "FLOWER_SERVICE_ACTOR_";
  private static final ConcurrentMap<String, Set<RefType>> nextServiceActorCache = new ConcurrentHashMap<>();
  private FlowerService service;
  private String paramType;
  private int index;
  private final FlowerFactory flowerFactory;
  private Filter<Object, ?> filter;

  static public Props props(String serviceName, FlowerFactory flowerFactory, int index) {
    return Props.create(ServiceActor.class, serviceName, flowerFactory, index);
  }

  /**
   * 当前Actor绑定的服务
   */
  private String serviceName;

  public ServiceActor(String serviceName, FlowerFactory flowerFactory, int index) {
    this.serviceName = serviceName;
    this.index = index;
    this.flowerFactory = flowerFactory;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public void onServiceContextReceived(ServiceContext serviceContext) throws Throwable {
    FlowMessage flowMessage = serviceContext.getFlowMessage();
    if (serviceContext.isSync()) {
      CacheManager.get(serviceActorCachePrefix + serviceContext.getFlowName()).add(serviceContext.getId(), getSender(),
          defaultTimeToLive);
    }

    Object result = null;
    Object param = null;
    try {
      ServiceContextUtil.fillServiceContext(serviceContext);
      String pType = getParamType(serviceContext);
      if (flowMessage.getMessage() != null && ClassUtil.exists(flowMessage.getMessageType())) {
        pType = flowMessage.getMessageType();
      }

      param = Codec.valueOf(flowMessage.getCodec()).decode(flowMessage.getMessage(), pType);
      if (getFilter(serviceContext) != null) {
        getFilter(serviceContext).filter(param, serviceContext);
      }
      // logger.info("服务参数类型 {} : {}", pType, getService(serviceContext));
      result = ((Service) getService(serviceContext)).process(param, serviceContext);
    } catch (Throwable e) {
      Web web = serviceContext.getWeb();
      if (web != null) {
        web.complete();
      }

      Exception e2 = new ServiceException(
          "invoke service " + serviceContext.getCurrentServiceName() + " : " + service + "\r\n, param : " + param, e);
      if (serviceContext.isSync()) {
        handleSyncResult(serviceContext, ExceptionUtil.getErrorMessage(e2), true);
      } else {
        throw e2;
      }
    }

    Set<RefType> nextActorRef = getNextServiceActors(serviceContext);
    if (serviceContext.isSync() && CollectionUtil.isEmpty(nextActorRef)) {
      handleSyncResult(serviceContext, result, false);
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

    handleNextServices(serviceContext, result, flowMessage.getTransactionId());
  }

  /**
   * 处理同步消息
   * 
   * @param serviceContext 上下文 {@link ServiceContext}
   * @param result 消息内容
   */
  private void handleSyncResult(ServiceContext serviceContext, Object result, boolean error) {
    CacheManager cacheManager = CacheManager.get(serviceActorCachePrefix + serviceContext.getFlowName());
    Cache<ActorRef> cache = cacheManager.getCache(serviceContext.getId());
    if (cache == null) {
      logger.warn("maybe it's timeout. serviceContext : {}", serviceContext);
      return;
    }
    ActorRef actor = cache.getValue();
    if (actor != null) {
      FlowMessage resultMessage = new FlowMessage();
      Codec codec = Codec.Hessian;
      resultMessage.setCodec(codec.getCode());
      if (result != null) {
        resultMessage.setMessageType(result.getClass().getName());
      }
      if (error) {
        resultMessage.setException((String) result);
      } else {
        resultMessage.setMessage(codec.encode(result));
      }


      actor.tell(resultMessage, getSelf());
      cacheManager.invalidate(serviceContext.getId());
    }
  }

  /**
   * 处理当前服务的下行服务节点
   * 
   * @param serviceContext 上下文 {@link ServiceContext}
   * @param result 消息内容
   * @param oldTransactionId oldTransactionId 聚合服务时会用到
   */
  private void handleNextServices(ServiceContext serviceContext, Object result, final String oldTransactionId) {
    if (result == null) {// for joint service
      return;
    }
    Set<RefType> refTypes = getNextServiceActors(serviceContext);
    if (refTypes == null) {
      return;
    }
    ServiceContextUtil.cleanServiceContext(serviceContext);
    for (RefType refType : refTypes) {
      // condition fork for one-service to multi-service
      if (!refType.getMessageType().isInstance(result)) {
        logger.warn("result {} is not compatible for {}, so discard it. currentService : {}, nextService : {}",
            refType.getMessageType(), serviceContext.getCurrentServiceName(), refType.getServiceName());
        continue;
      }
      boolean flag = true;
      // check
      if (Condition.class.isInstance(result)) {
        Object con = ((Condition) result).getCondition();
        if (String.class.isInstance(con)) {
          if (StringUtil.stringNotInStrings(refType.getServiceName(), con.toString())) {
            flag = false;
            // TODO how to log it
          }
        }
      }

      if (flag) {
        FlowMessage resultMessage = new FlowMessage();
        resultMessage.setMessage(Codec.Hessian.encode(result));
        resultMessage.setMessageType(result.getClass().getName());
        resultMessage.setCodec(Codec.Hessian.getCode());
        resultMessage.setTransactionId(oldTransactionId);

        ServiceContext context = serviceContext.newInstance();
        context.setFlowMessage(resultMessage);
        context.setCurrentServiceName(refType.getServiceName());
        refType.getActorWrapper().tell(context, getSelf());
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
      ServiceMeta serviceMeta = flowerFactory.getServiceFactory().getServiceLoader().loadServiceMeta(serviceName);
      this.paramType = serviceMeta.getParamType();
      if (service instanceof Aggregate) {
        int num = flowerFactory.getServiceFactory().getOrCreateServiceFlow(serviceContext.getFlowName())
            .getServiceConfig(serviceName).getJointSourceNumber().get();
        ((AggregateService) service).setSourceNumber(num);
      }
    }
    return service;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public Filter getFilter(ServiceContext serviceContext) {
    if (this.filter != null) {
      return filter;
    }
    FlowerService service = getService(serviceContext);
    com.ly.train.flower.common.annotation.FlowerService flowerService =
        service.getClass().getAnnotation(com.ly.train.flower.common.annotation.FlowerService.class);
    if (flowerService == null) {
      return null;
    }
    String[] filters = flowerService.filter();
    if (filters == null) {
      return null;
    }
    Filter ret = null;
    for (String f : filters) {
      if (StringUtil.isBlank(f)) {
        continue;
      }
      Filter temp = ExtensionLoader.load(Filter.class).load(f);
      if (temp == null) {
        continue;
      }
      if (ret == null) {
        ret = temp;
      } else {
        ret.setNext(ret);
      }
    }
    this.filter = ret;

    return filter;
  }

  private String getParamType(ServiceContext serviceContext) {
    if (this.paramType == null) {
      this.service = getService(serviceContext);
      ServiceMeta serviceMeta = flowerFactory.getServiceFactory().getServiceLoader().loadServiceMeta(serviceName);
      this.paramType = serviceMeta.getParamType();
    }
    return paramType;
  }

  private Set<RefType> getNextServiceActors(ServiceContext serviceContext) {
    final String cacheKey = serviceContext.getFlowName() + "_" + serviceContext.getCurrentServiceName() + "_" + index;
    Set<RefType> nextServiceActors = nextServiceActorCache.get(cacheKey);
    if (nextServiceActors == null && StringUtil.isNotBlank(serviceContext.getFlowName())) {
      nextServiceActors = new HashSet<>();
      Set<ServiceConfig> serviceConfigs = flowerFactory.getServiceFactory()
          .getOrCreateServiceFlow(serviceContext.getFlowName()).getNextFlow(serviceContext.getCurrentServiceName());
      if (serviceConfigs != null) {
        for (ServiceConfig serviceConfig : serviceConfigs) {
          flowerFactory.getServiceFactory().loadServiceMeta(serviceConfig);// 内部对serviceConfig的数据进行填充
          RefType refType = new RefType();
          refType.setIndex(index);
          refType.setServiceConfig(serviceConfig);
          refType.setMessageType(ClassUtil.forName(serviceConfig.getServiceMeta().getParamType()));
          refType.setServiceName(serviceConfig.getServiceName());
          nextServiceActors.add(refType);
        }
        nextServiceActorCache.put(cacheKey, nextServiceActors);
      }
    }
    return nextServiceActors;
  }



  class RefType {
    private Class<?> messageType;
    private String serviceName;
    private int index;
    private ServiceConfig serviceConfig;

    public void setServiceConfig(ServiceConfig serviceConfig) {
      this.serviceConfig = serviceConfig;
    }

    public void setIndex(int index) {
      this.index = index;
    }

    public ActorWrapper getActorWrapper() {
      return flowerFactory.getActorFactory().buildServiceActor(serviceConfig, index);
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

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("RefType [messageType=");
      builder.append(messageType);
      builder.append(", serviceName=");
      builder.append(serviceName);
      builder.append("]");
      return builder.toString();
    }

  }

}
