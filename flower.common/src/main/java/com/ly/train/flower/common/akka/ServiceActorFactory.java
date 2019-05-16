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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.ly.train.flower.common.akka.actor.ServiceActor;
import com.ly.train.flower.common.akka.actor.SupervisorActor;
import com.ly.train.flower.common.akka.actor.command.Command;
import com.ly.train.flower.common.akka.actor.command.CreateCommand;
import com.ly.train.flower.common.akka.actor.command.GetContextCommand;
import com.ly.train.flower.common.akka.actor.wrapper.ActorRefWrapper;
import com.ly.train.flower.common.akka.actor.wrapper.ActorSelectionWrapper;
import com.ly.train.flower.common.akka.actor.wrapper.ActorWrapper;
import com.ly.train.flower.common.akka.router.FlowRouter;
import com.ly.train.flower.common.akka.router.ServiceRouter;
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
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class ServiceActorFactory extends AbstractLifecycle implements ActorFactory {
  private static final Logger logger = LoggerFactory.getLogger(ServiceActorFactory.class);
  private static final Long DEFAULT_TIMEOUT = 5000L;
  private static final Duration timeout = Duration.create(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
  private final ConcurrentMap<String, ActorWrapper> serviceActorCache = new ConcurrentHashMap<>();

  private volatile ConcurrentMap<String, ServiceRouter> serviceRoutersCache = new ConcurrentHashMap<>();
  private volatile ConcurrentMap<String, FlowRouter> flowRoutersCache = new ConcurrentHashMap<>();
  private volatile ConcurrentMap<String, ActorSelection> superActorCache =
      new ConcurrentHashMap<String, ActorSelection>();
  public static final String actorPathFormat = "akka.tcp://flower@%s:%s/user/flower/%s_%s";
  public static final String superActorPathFormat = "akka.tcp://flower@%s:%s/user/flower";
  private final int defaultFlowIndex = 0;


  private final FlowerFactory flowerFactory;
  private final ServiceFactory serviceFactory;
  private volatile ActorSystem actorSystem;
  private volatile ActorRef supervierActor;
  private volatile ActorContext actorContext;
  protected final FlowerConfig flowerConfig;

  private volatile Lock actorLock = new ReentrantLock();
  private volatile Lock flowRouterLock = new ReentrantLock();
  private volatile Lock serviceRouterLock = new ReentrantLock();

  public ServiceActorFactory(FlowerFactory flowerFactory) {
    this.flowerFactory = flowerFactory;
    this.flowerConfig = flowerFactory.getFlowerConfig();
    this.serviceFactory = flowerFactory.getServiceFactory();
  }


  @Override
  protected void doInit() {}

  @Override
  public ActorWrapper buildServiceActor(ServiceConfig serviceConfig) {
    return buildServiceActor(serviceConfig, defaultFlowIndex);
  }

  @Override
  public ActorWrapper buildServiceActor(ServiceConfig serviceConfig, int index) {
    final String serviceName = serviceConfig.getServiceName();
    // TODO 缓存key的考量，是否要添加flowNumber
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
          ActorRef actorRef = getActorContext()
              .actorOf(ServiceActor.props(serviceName, flowerFactory, index).withDispatcher("dispatcher"), cacheKey);
          actorWrapper = new ActorRefWrapper(actorRef).setServiceName(serviceName);
        } else {
          // "akka.tcp://flower@127.0.0.1:2551/user/$a"
          URL url = serviceConfig.getAddresses().iterator().next();
          createRemoteActor(url, new CreateCommand(serviceName, index));
          String actorPath = String.format(actorPathFormat, url.getHost(), url.getPort(), serviceName, index);
          ActorSelection actorSelection = getActorContext().actorSelection(actorPath);
          actorWrapper = new ActorSelectionWrapper(actorSelection).setServiceName(serviceName);
        }
        // logger.info("创建Actor {} : {}", serviceName, flowerConfig.getPort());
        if (logger.isTraceEnabled()) {
          logger.trace("create actor {} ： {}", serviceName, actorWrapper);
        }
        serviceActorCache.put(cacheKey, actorWrapper);
      }
    } catch (Exception e) {
      throw new FlowerException(
          "fail to create flowerService, flowName : " + serviceConfig.getFlowName() + ", serviceName : " + serviceName
              + ", serviceClassName : " + serviceConfig.getServiceMeta().getServiceClassName(),
          e);
    } finally {
      actorLock.unlock();
    }
    return actorWrapper;
  }

  private void createRemoteActor(URL url, Command command) throws Exception {
    final String cacheKey = url.getHost() + ":" + url.getPort();
    ActorSelection superActor = superActorCache.get(cacheKey);
    if (superActor == null) {
      String actorPath = String.format(superActorPathFormat, url.getHost(), url.getPort());
      superActor = getActorContext().actorSelection(actorPath);
      ActorSelection temp = superActorCache.putIfAbsent(cacheKey, superActor);
      if (temp != null) {
        superActor = temp;
      }
    }
    Future<Object> future = Patterns.ask(superActor, command, DEFAULT_TIMEOUT - 1);
    Await.result(future, timeout);
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
    try {
      this.actorSystem = createActorSystem();
      this.supervierActor = actorSystem.actorOf(SupervisorActor.props(this), "flower");
      Future<Object> future = Patterns.ask(getSupervierActor(), new GetContextCommand(), DEFAULT_TIMEOUT - 1);
      this.actorContext = (ActorContext) Await.result(future, timeout);
    } catch (Exception e) {
      logger.error("fail to start flower", e);
      stop();
      throw new FlowerException("", e);
    }
  }

  private ActorSystem createActorSystem() {
    FlowerConfig flowerConfig = flowerFactory.getFlowerConfig();
    StringBuffer configBuilder = new StringBuffer();

    final String sepator = "\r\n";
    // @formatter:off
    if (StringUtil.isNotBlank(flowerConfig.getHost())) {
      configBuilder.append(getFormatString("akka.actor.provider = %s", "remote")).append(sepator);
      configBuilder.append(getFormatString("akka.remote.enabled-transports = [%s]", "akka.remote.netty.tcp")).append(sepator);
      configBuilder.append(getFormatString("akka.remote.netty.tcp.hostname = %s", flowerConfig.getHost())).append(sepator);
      configBuilder.append(getFormatString("akka.remote.netty.tcp.port = %s", flowerConfig.getPort())).append(sepator);
    }
    configBuilder.append(getFormatString("dispatcher.fork-join-executor.parallelism-min = %s", flowerConfig.getParallelismMin())).append(sepator);
    configBuilder.append(getFormatString("dispatcher.fork-join-executor.parallelism-max = %s", flowerConfig.getParallelismMax())).append(sepator);
    configBuilder.append(getFormatString("dispatcher.fork-join-executor.parallelism-factor = %s", flowerConfig.getParallelismFactor())).append(sepator);
    // @formatter:on
    logger.info("akka config ：{}", configBuilder.toString());
    Config config = ConfigFactory.parseString(configBuilder.toString()).withFallback(ConfigFactory.load());
    ActorSystem actorSystem = ActorSystem.create("flower", config);
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
    return actorSystem;
  }

  private String getFormatString(String format, Object data) {
    return String.format(format, "\"" + data + "\"");
  }

  @Override
  protected void doStop() {
    logger.info("stop flower, config : {}", flowerConfig);
    if (actorSystem != null) {
      actorSystem.terminate();
    }
  }


  protected ActorContext getActorContext() {
    return actorContext;
  }

  protected ActorSystem getActorSystem() {
    return actorSystem;
  }

  protected ActorRef getSupervierActor() {
    return supervierActor;
  }
}
