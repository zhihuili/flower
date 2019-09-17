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
package com.ly.train.flower.core.akka.actor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import com.ly.train.flower.common.core.config.FlowConfig;
import com.ly.train.flower.common.core.config.ServiceConfig;
import com.ly.train.flower.common.core.config.ServiceMeta;
import com.ly.train.flower.common.core.message.FlowMessage;
import com.ly.train.flower.common.core.proxy.MethodProxy;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.core.web.Web;
import com.ly.train.flower.common.exception.ServiceException;
import com.ly.train.flower.common.util.ClassUtil;
import com.ly.train.flower.common.util.CollectionUtil;
import com.ly.train.flower.common.util.ExceptionUtil;
import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.common.util.cache.Cache;
import com.ly.train.flower.common.util.cache.CacheManager;
import com.ly.train.flower.core.akka.actor.wrapper.ActorRemoteRouterWrapper;
import com.ly.train.flower.core.akka.actor.wrapper.ActorWrapper;
import com.ly.train.flower.core.service.Aggregate;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.core.service.container.util.ServiceContextUtil;
import com.ly.train.flower.core.service.impl.AggregateService;
import com.ly.train.flower.core.service.message.Condition;
import com.ly.train.flower.filter.Filter;
import com.ly.train.flower.filter.FilterChain;
import com.ly.train.flower.filter.FilterChainApplication;
import com.ly.train.flower.serializer.Serializer;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Wrap service by actor, make service driven by message.
 * 
 * @author zhihui.li
 * @author leeyazhou
 * 
 */
@SuppressWarnings({"unchecked"})
public class ServiceActor extends AbstractFlowerActor {

  public static final Long defaultTimeToLive = TimeUnit.SECONDS.toMillis(60);
  private static final String serviceActorCachePrefix = "FLOWER_SERVICE_ACTOR_";
  private static final ConcurrentMap<String, Set<RefType>> nextServiceActorCache = new ConcurrentHashMap<>();
  private MethodProxy methodProxy;
  private String paramType;
  private int index;
  private final FlowerFactory flowerFactory;

  public static Props props(String serviceName, FlowerFactory flowerFactory, int index) {
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

  @Override
  public void onServiceContextReceived(ServiceContext serviceContext) throws Throwable {
    FlowMessage flowMessage = serviceContext.getFlowMessage();
    if (serviceContext.isSync()) {
      CacheManager.get(serviceActorCachePrefix + serviceContext.getFlowName()).add(serviceContext.getId(), getSender(),
          defaultTimeToLive);
    }
    Serializer serializer = ExtensionLoader.load(Serializer.class).load(serviceContext.getCodec());

    Object result = null;
    Object param = null;
    try {
      ServiceContextUtil.fillServiceContext(serviceContext);
      param = getAndDecodeParam(serviceContext);
      FilterChain filterChain = buildFilterChain(serviceContext);
      // logger.info("服务参数类型 {} : {}", pType, getService(serviceContext));
      result = filterChain.doFilter(param, serviceContext);
    } catch (Throwable e) {
      handleException(serviceContext, e, param, serializer);
    }
    if (result != null && result instanceof CompletableFuture) {
      final Object tempParam = param;
      ((CompletableFuture<Object>) result).whenComplete((re, ex) -> {
        if (ex != null) {
          handleException(serviceContext, ex, tempParam, serializer);
          return;
        }
        handleNextServices(serviceContext, re, flowMessage.getTransactionId(), serializer);
      });
    } else {
      handleNextServices(serviceContext, result, flowMessage.getTransactionId(), serializer);
    }
  }

  private void handleException(ServiceContext serviceContext, Throwable throwable, Object param,
      Serializer serializer) {
    Web web = serviceContext.getWeb();
    if (web != null) {
      web.complete();
    }

    ServiceException e2 = new ServiceException(
        "invoke service " + serviceContext.getCurrentServiceName() + " : " + methodProxy + "\r\n, param : " + param,
        throwable);
    if (serviceContext.isSync()) {
      handleSyncResult(serviceContext, ExceptionUtil.getErrorMessage(e2), true, serializer);
    } else {
      throw e2;
    }

  }

  /**
   * 处理同步消息
   * 
   * @param serviceContext 上下文 {@link ServiceContext}
   * @param result 消息内容
   */
  private void handleSyncResult(ServiceContext serviceContext, Object result, boolean error, Serializer serializer) {
    CacheManager cacheManager = CacheManager.get(serviceActorCachePrefix + serviceContext.getFlowName());
    Cache<ActorRef> cache = cacheManager.getCache(serviceContext.getId());
    if (cache == null) {
      logger.warn("maybe it's timeout. serviceContext : {}", serviceContext);
      return;
    }
    ActorRef actor = cache.getValue();
    if (actor != null) {
      FlowMessage resultMessage = new FlowMessage();
      if (result != null) {
        resultMessage.setMessageType(result.getClass().getName());
      }
      if (error) {
        resultMessage.setException((String) result);
      } else {
        resultMessage.setMessage(serializer.encode(result));
      }


      actor.tell(resultMessage, getSelf());
      cacheManager.invalidate(serviceContext.getId());
    }
  }

  private void handleNextServices(ServiceContext serviceContext, Object result, final String oldTransactionId,
      Serializer serializer) {
    try {
      doHandleNextServices(serviceContext, result, oldTransactionId, serializer);
    } catch (Exception e) {
      logger.error("fail to handle next services ", e);
    }
  }

  /**
   * 处理当前服务的下行服务节点
   * 
   * @param serviceContext 上下文 {@link ServiceContext}
   * @param result 消息内容
   * @param oldTransactionId oldTransactionId 聚合服务时会用到
   */
  private void doHandleNextServices(ServiceContext serviceContext, Object result, final String oldTransactionId,
      Serializer serializer) {
    Set<RefType> nextActorRef = getNextServiceActors(serviceContext);
    if (serviceContext.isSync() && CollectionUtil.isEmpty(nextActorRef)) {
      handleSyncResult(serviceContext, result, false, serializer);
      return;
    }

    Web web = serviceContext.getWeb();
    if (web != null) {
      if (methodProxy.isFlush()) {
        web.flush();
      }
      if (methodProxy.isCompleted()) {
        web.complete();
      }
    }

    if (result == null) {
      // for joint service
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
        if (con != null && String.class.isInstance(con)) {
          if (StringUtil.stringNotInStrings(refType.getServiceName(), con.toString())) {
            flag = false;
            // TODO how to log it
          }
        }
      }

      if (flag) {
        FlowMessage resultMessage = new FlowMessage();
        resultMessage.setMessage(serializer.encode(result));
        resultMessage.setMessageType(result.getClass().getName());
        resultMessage.setTransactionId(oldTransactionId);

        ServiceContext context = serviceContext.newContext();
        context.setFlowMessage(resultMessage);
        context.setCurrentServiceName(refType.getServiceName());
        refType.getActorWrapper(serviceContext).tell(context, getSelf());
      }
    }
  }

  /**
   * 懒加载方式获取服务实例
   * 
   * @return {@link MethodProxy}
   */
  public MethodProxy getService(ServiceContext serviceContext) {
    if (this.methodProxy == null) {
      this.methodProxy = flowerFactory.getServiceFactory().getServiceLoader().loadService(serviceName);
      ServiceMeta serviceMeta = flowerFactory.getServiceFactory().getServiceLoader().loadServiceMeta(serviceName);
      this.paramType = serviceMeta.getParamType();
      if (methodProxy.getFlowerService() instanceof Aggregate) {
        int num = flowerFactory.getServiceFactory().getOrCreateServiceFlow(serviceContext.getFlowName())
            .getServiceConfig(serviceName).getAggregateNumber().get();
        ((AggregateService) methodProxy.getFlowerService()).setSourceNumber(num);
      }
    }
    return methodProxy;
  }

  private FilterChain buildFilterChain(ServiceContext serviceContext) {
    List<Filter> filters = new ArrayList<>();
    if (serviceContext.getFlowName() != null) {
      FlowConfig flowConfig =
          flowerFactory.getServiceFactory().getOrCreateServiceFlow(serviceContext.getFlowName()).getFlowConfig();
      Set<String> filterNames = flowConfig.getFilters();
      if (filterNames != null) {
        for (String f : filterNames) {
          if (StringUtil.isBlank(f)) {
            continue;
          }
          Filter temp = ExtensionLoader.load(Filter.class).load(f);
          if (temp == null) {
            continue;
          }
          filters.add(temp);
        }
      }
    }
    return new FilterChainApplication(filters.toArray(new Filter[filters.size()]), methodProxy);
  }

  private Object getAndDecodeParam(ServiceContext serviceContext) {
    FlowMessage flowMessage = serviceContext.getFlowMessage();
    Serializer serializer = ExtensionLoader.load(Serializer.class).load(serviceContext.getCodec());
    String paramType = getParamType(serviceContext);
    if (flowMessage.getMessage() != null && ClassUtil.exists(flowMessage.getMessageType())) {
      paramType = flowMessage.getMessageType();
    }
    return serializer.decode(flowMessage.getMessage(), paramType);
  }

  private String getParamType(ServiceContext serviceContext) {
    if (this.paramType == null) {
      this.methodProxy = getService(serviceContext);
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
      Set<ServiceConfig> serviceConfigs =
          flowerFactory.getServiceFactory().getOrCreateServiceFlow(serviceContext.getFlowName())
              .getNextServiceConfig(serviceContext.getCurrentServiceName());
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

    public ActorWrapper getActorWrapper(ServiceContext serviceContext) {
      ActorWrapper actorWrapper = flowerFactory.getActorFactory().buildServiceActor(serviceConfig, index);
      if (actorWrapper instanceof ActorRemoteRouterWrapper) {
        return ((ActorRemoteRouterWrapper) actorWrapper).chooseOne(serviceContext);
      }
      return actorWrapper;
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
