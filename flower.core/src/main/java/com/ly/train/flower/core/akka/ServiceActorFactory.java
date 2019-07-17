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
package com.ly.train.flower.core.akka;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.ly.train.flower.common.core.config.ServiceConfig;
import com.ly.train.flower.common.exception.FlowException;
import com.ly.train.flower.common.exception.FlowNotFoundException;
import com.ly.train.flower.common.lifecyle.AbstractLifecycle;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.common.util.cache.CacheManager;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.core.akka.actor.command.ActorCommand;
import com.ly.train.flower.core.akka.actor.command.Command;
import com.ly.train.flower.core.akka.actor.wrapper.ActorLocalWrapper;
import com.ly.train.flower.core.akka.actor.wrapper.ActorRemoteWrapper;
import com.ly.train.flower.core.akka.actor.wrapper.ActorWrapper;
import com.ly.train.flower.core.akka.router.FlowRouter;
import com.ly.train.flower.core.akka.router.ServiceRouter;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.core.service.container.ServiceFactory;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import scala.concurrent.Future;

public class ServiceActorFactory extends AbstractLifecycle implements ActorFactory {
  private static final Logger logger = LoggerFactory.getLogger(ServiceActorFactory.class);
  /**
   * for example : akka.tcp://flower@127.0.0.1:2551/user/flower
   */
  public static final String actorPathFormat = "akka.tcp://flower@%s:%s/user/flower/%s_%s";
  /**
   * for example : akka.tcp://flower@127.0.0.1:2551/user/flower/userserivce_1
   */
  public static final String superActorPathFormat = "akka.tcp://flower@%s:%s/user/flower";


  private final ConcurrentMap<String, FlowRouter> flowRoutersCache = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, ServiceRouter> serviceRoutersCache = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, ActorWrapper> serviceRouterActorsCache = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, ActorRemoteWrapper> remoteSupervisorActorsCache = new ConcurrentHashMap<>();
  private static final int defaultFlowerNumber = 1 << 7;


  private final FlowerFactory flowerFactory;
  private final ServiceFactory serviceFactory;
  private final FlowerConfig flowerConfig;

  private volatile Lock actorLock = new ReentrantLock();
  private volatile Lock flowRouterLock = new ReentrantLock();
  private volatile Lock serviceRouterLock = new ReentrantLock();
  private final FlowerActorSystem flowerActorSystem;

  public ServiceActorFactory(FlowerFactory flowerFactory) {
    this.flowerFactory = flowerFactory;
    this.flowerConfig = flowerFactory.getFlowerConfig();
    this.serviceFactory = flowerFactory.getServiceFactory();
    this.flowerActorSystem = new FlowerActorSystem(flowerConfig, this, flowerFactory);
  }

  @Override
  protected void doInit() {}

  @Override
  public ActorWrapper buildServiceActor(ServiceConfig serviceConfig, int index) {
    final String serviceName = serviceConfig.getServiceName();
    // TODO 缓存key的考量，是否要添加flowNumber
    final String cacheKey = serviceName + "_" + index;
    ActorWrapper actorWrapper = serviceRouterActorsCache.get(cacheKey);
    if (actorWrapper != null) {
      return actorWrapper;
    }

    try {
      actorLock.lock();
      actorWrapper = serviceRouterActorsCache.get(cacheKey);
      if (actorWrapper == null) {
        if (serviceConfig.isLocal()) {
          ActorRef actorRef = flowerActorSystem.createLocalActor(serviceName, index, cacheKey);
          actorWrapper = new ActorLocalWrapper(actorRef).setServiceName(serviceName);
        } else {
          // "akka.tcp://flower@127.0.0.1:2551/user/$a"
          URL url = serviceConfig.getAddresses().iterator().next();
          createRemoteActor(url, new ActorCommand(serviceName, index));
          String actorPath = String.format(actorPathFormat, url.getHost(), url.getPort(), serviceName, index);
          ActorRef actorSelection = flowerActorSystem.createRemoteActor(actorPath);
          actorWrapper = new ActorRemoteWrapper(actorSelection).setServiceName(serviceName);
        }
        // logger.info("创建Actor {} : {}", serviceName, flowerConfig.getPort());
        if (logger.isTraceEnabled()) {
          logger.trace("create actor {} ： {}", serviceName, actorWrapper);
        }
        serviceRouterActorsCache.put(cacheKey, actorWrapper);
      }
    } catch (Exception e) {
      throw new FlowException(
          "fail to create flowerService, flowName : " + serviceConfig.getFlowName() + ", serviceName : " + serviceName
              + ", serviceClassName : " + serviceConfig.getServiceMeta().getServiceClassName(),
          e);
    } finally {
      actorLock.unlock();
    }
    return actorWrapper;
  }

  private void createRemoteActor(URL url, Command command) throws Exception {
    ActorRemoteWrapper superActor = getOrCreateRemoteSupervisorActor(url.getHost(), url.getPort());
    Future<Object> future = Patterns.ask(superActor.getActorRef(), command, FlowerActorSystem.DEFAULT_TIMEOUT - 1);
    Await.result(future, FlowerActorSystem.timeout);
  }

  private ActorRemoteWrapper getOrCreateRemoteSupervisorActor(String host, int port) {
    final String cacheKey = host + ":" + port;
    return remoteSupervisorActorsCache.computeIfAbsent(cacheKey, a -> {
      String actorPath = String.format(superActorPathFormat, host, port);
      ActorRef actorRef = flowerActorSystem.createRemoteActor(actorPath);
      return new ActorRemoteWrapper(actorRef);
    });
  }


  @Override
  public FlowRouter buildFlowRouter(String flowName, int flowNumber) {
    final ServiceConfig serviceConfig = serviceFactory.getOrCreateServiceFlow(flowName).getHeadServiceConfig();
    if (serviceConfig == null) {
      throw new FlowNotFoundException("flowName : " + flowName + ", flowNumbe : " + flowNumber);
    }
    final String serviceName = serviceConfig.getServiceName();
    final String routerCacheKey = flowName + "_" + serviceName + "_" + flowNumber;

    FlowRouter flowRouter = flowRoutersCache.get(routerCacheKey);
    if (flowRouter == null) {
      flowRouterLock.lock();
      try {
        flowRouter = flowRoutersCache.get(routerCacheKey);
        if (flowRouter == null) {
          flowRouter = new FlowRouter(serviceConfig, flowNumber, flowerFactory);
          flowRouter.init();
          flowRoutersCache.put(routerCacheKey, flowRouter);
          logger.info("build service Router. flowName : {}, serviceName : {}, flowNumber : {}", flowName, serviceName,
              flowNumber);
        }

      } finally {
        flowRouterLock.unlock();
      }
    }
    return flowRouter;
  }

  @Override
  public ServiceRouter buildServiceRouter(ServiceConfig serviceConfig, int flowNumber) {
    if (flowNumber <= 0) {
      flowNumber = defaultFlowerNumber;
    }
    final String serviceName = serviceConfig.getServiceName();
    final String routerName = serviceName + "_" + flowNumber;

    ServiceRouter serviceRouter = serviceRoutersCache.get(routerName);
    if (serviceRouter == null) {
      serviceRouterLock.lock();
      try {
        serviceRouter = serviceRoutersCache.get(routerName);
        if (serviceRouter == null) {
          serviceRouter = new ServiceRouter(serviceConfig, flowerFactory, flowNumber);
          serviceRouter.init();
          serviceRoutersCache.put(routerName, serviceRouter);
          logger.info("build service Router. serviceName : {}, actorNumber : {}", serviceName, flowNumber);
        }
      } finally {
        serviceRouterLock.unlock();
      }
    }
    return serviceRouter;
  }

  @Override
  protected void doStart() {
    flowerActorSystem.start();
  }

  @Override
  protected void doStop() {
    flowerActorSystem.stop();
    CacheManager.stop();
  }

}
