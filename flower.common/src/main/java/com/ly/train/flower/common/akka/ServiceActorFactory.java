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
package com.ly.train.flower.common.akka;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.ly.train.flower.common.akka.actor.ServiceActor;
import com.ly.train.flower.common.akka.actor.SupervisorActor;
import com.ly.train.flower.common.akka.actor.wrapper.ActorRefWrapper;
import com.ly.train.flower.common.akka.actor.wrapper.ActorSelectionWrapper;
import com.ly.train.flower.common.akka.actor.wrapper.ActorWrapper;
import com.ly.train.flower.common.exception.FlowNotFoundException;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.service.container.lifecyle.AbstractLifecycle;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

public class ServiceActorFactory extends AbstractLifecycle {
  private static final Logger logger = LoggerFactory.getLogger(ServiceActorFactory.class);
  private static final Long DEFAULT_TIMEOUT = 5000L;
  private static final Duration timeout = Duration.create(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
  private final ConcurrentMap<String, ActorWrapper> serviceActorCache = new ConcurrentHashMap<>();

  private volatile Map<String, ServiceRouter> serviceRoutersCache = new ConcurrentHashMap<>();
  private volatile Map<String, FlowRouter> flowRoutersCache = new ConcurrentHashMap<>();
  public static final String actorPathFormat = "akka.tcp://%s@%s:%s/user/flower/%s_0";
  private final int defaultFlowIndex = 0;


  private final FlowerFactory flowerFactory;
  private final ServiceFactory serviceFactory;
  private volatile ActorSystem actorSystem;
  private volatile ActorRef supervierActor;
  private volatile ActorContext actorContext;
  private final FlowerConfig flowerConfig;

  public ServiceActorFactory(FlowerFactory flowerFactory) {
    this.flowerFactory = flowerFactory;
    this.flowerConfig = flowerFactory.getFlowerConfig();
    this.serviceFactory = flowerFactory.getServiceFactory();
  }

  private volatile Lock actorLock = new ReentrantLock();
  private volatile Lock flowRouterLock = new ReentrantLock();
  private volatile Lock serviceRouterLock = new ReentrantLock();


  @Override
  protected void doInit() {}

  public ActorWrapper buildServiceActor(ServiceConfig serviceConfig) {
    return buildServiceActor(serviceConfig, defaultFlowIndex);
  }

  public ActorWrapper buildServiceActor(ServiceConfig serviceConfig, int index) {
    return buildServiceActor(serviceConfig, index, -1);
  }

  public ActorWrapper buildServiceActor(ServiceConfig serviceConfig, int index, int count) {
    final String serviceName = serviceConfig.getServiceName();
    final String cacheKey = serviceName + "_" + index;
    ActorWrapper actorWrapper = serviceActorCache.get(cacheKey);
    if (actorWrapper != null) {
      return actorWrapper;
    }

    try {
      actorLock.lock();
      actorWrapper = serviceActorCache.get(cacheKey);
      if (actorWrapper == null) {
        if (serviceConfig.isLocal()) {
          ActorRef actorRef =
              getActorContext().actorOf(ServiceActor.props(serviceName, flowerFactory, count), cacheKey);
          actorWrapper = new ActorRefWrapper(actorRef).setServiceName(serviceName);
        } else {
          // "akka.tcp://flower@127.0.0.1:2551/user/$a"
          URL url = serviceConfig.getAddresses().iterator().next();
          String actorPath =
              String.format(actorPathFormat, flowerConfig.getName(), url.getHost(), url.getPort(), serviceName);
          ActorSelection actorSelection = getActorContext().actorSelection(actorPath);
          actorWrapper = new ActorSelectionWrapper(actorSelection).setServiceName(serviceName);
        }
        if (logger.isTraceEnabled()) {
          logger.trace("create actor {} ： {}", serviceName, actorWrapper);
        }
        serviceActorCache.put(cacheKey, actorWrapper);
      }
    } catch (Exception e) {
      throw new FlowerException(
          "fail to create flowerService,flowName : " + serviceConfig.getFlowName() + ", serviceName : " + serviceName
              + ", serviceClassName : " + serviceConfig.getServiceMeta().getServiceClassName(),
          e);
    } finally {
      actorLock.unlock();
    }
    return actorWrapper;
  }


  protected ActorContext getActorContext() {
    if (actorContext == null) {
      synchronized (this) {
        if (actorContext == null) {
          try {
            actorContext = (ActorContext) Await.result(
                Patterns.ask(getSupervierActor(), new SupervisorActor.GetActorContext(), DEFAULT_TIMEOUT - 1), timeout);
          } catch (Exception e) {
            logger.error("", e);
            throw new FlowerException("", e);
          }
        }
      }
    }
    return actorContext;

  }

  private ActorSystem getActorSystem() {
    if (actorSystem == null) {
      synchronized (this) {
        if (actorSystem == null) {
          FlowerConfig flowerConfig = flowerFactory.getFlowerConfig();
          StringBuffer configBuilder = new StringBuffer();

          if (StringUtil.isNotBlank(flowerConfig.getHost())) {
            configBuilder.append("akka.actor.provider=\"remote\"").append("\r\n");
            configBuilder.append("akka.remote.enabled-transports = [\"akka.remote.netty.tcp\"]").append("\r\n");
            configBuilder.append("akka.remote.netty.tcp.hostname = ").append("\"").append(flowerConfig.getHost())
                .append("\"").append("\r\n");
            configBuilder.append("akka.remote.netty.tcp.port = \"").append(flowerConfig.getPort()).append("\"");
          }
          logger.info("akka config ：{}", configBuilder.toString());
          Config config = ConfigFactory.parseString(configBuilder.toString()).withFallback(ConfigFactory.load());
          actorSystem = ActorSystem.create(flowerConfig.getName(), config);
          Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
              try {
                flowerFactory.stop();
              } catch (Exception e) {
                // nothing
              }
            }
          });

        }
      }
    }

    return actorSystem;
  }


  /**
   * will be cached by flowName + "_" + serviceName
   * 
   * @param flowName flowName
   * @param flowNumbe 数量
   * @return {@link ServiceRouter}
   */
  public FlowRouter buildFlowRouter(String flowName, int flowNumbe) {
    final ServiceConfig serviceConfig = serviceFactory.getOrCreateServiceFlow(flowName).getHeadServiceConfig();
    if (serviceConfig == null) {
      throw new FlowNotFoundException("flowName : " + flowName + ", flowNumbe : " + flowNumbe);
    }
    final String serviceName = serviceConfig.getServiceName();
    final String routerCacheKey = flowName + "_" + serviceName;

    FlowRouter flowRouter = flowRoutersCache.get(routerCacheKey);
    if (flowRouter == null) {
      flowRouterLock.lock();
      try {
        flowRouter = flowRoutersCache.get(routerCacheKey);
        if (flowRouter == null) {
          flowRouter = new FlowRouter(serviceConfig, flowNumbe, flowerFactory);
          flowRouter.init();
          flowRoutersCache.put(routerCacheKey, flowRouter);
          logger.info("build service Router. flowName : {}, serviceName : {}, flowNumbe : {}", flowName, serviceName,
              flowNumbe);
        }

      } finally {
        flowRouterLock.unlock();
      }
    }
    return flowRouter;
  }

  public ServiceRouter buildServiceRouter(ServiceConfig serviceConfig, int flowNumbe) {
    final String serviceName = serviceConfig.getServiceName();
    final String routerName = serviceName + "_" + flowNumbe;

    ServiceRouter serviceRouter = serviceRoutersCache.get(routerName);
    if (serviceRouter == null) {
      serviceRouterLock.lock();
      try {
        serviceRouter = serviceRoutersCache.get(routerName);
        if (serviceRouter == null) {
          serviceRouter = new ServiceRouter(serviceConfig, flowerFactory, flowNumbe);
          serviceRouter.init();
          serviceRoutersCache.put(routerName, serviceRouter);
          logger.info("build service Router. serviceName : {}, flowNumbe : {}", serviceName, flowNumbe);
        }
      } finally {
        serviceRouterLock.unlock();
      }
    }
    return serviceRouter;
  }

  public ActorRef getSupervierActor() {
    if (supervierActor == null) {
      synchronized (this) {
        if (supervierActor == null) {
          this.supervierActor = getActorSystem().actorOf(SupervisorActor.props(), "flower");
        }
      }
    }
    return supervierActor;
  }

  @Override
  protected void doStart() {
    logger.info("start Akka Factory");
    this.actorSystem = getActorSystem();
    this.supervierActor = getSupervierActor();
    this.actorContext = getActorContext();
  }

  @Override
  protected void doStop() {
    logger.info("akka system terminate, system : {}", actorSystem);
    if (actorSystem != null) {
      actorSystem.terminate();
    }
  }


}
