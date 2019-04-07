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
import com.ly.train.flower.common.akka.actor.ServiceActor;
import com.ly.train.flower.common.akka.actor.SupervisorActor;
import com.ly.train.flower.common.akka.actor.wrapper.ActorRefWrapper;
import com.ly.train.flower.common.akka.actor.wrapper.ActorSelectionWrapper;
import com.ly.train.flower.common.akka.actor.wrapper.ActorWrapper;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceFlow;
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

public class ServiceActorFactory {
  private static final Logger logger = LoggerFactory.getLogger(ServiceActorFactory.class);
  private static final Long DEFAULT_TIMEOUT = 5000L;
  private static final Duration timeout = Duration.create(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
  private final ConcurrentMap<String, ActorWrapper> serviceActorCache = new ConcurrentHashMap<>();

  private static final Map<String, ServiceRouter> serviceRoutersCache = new ConcurrentHashMap<>();
  private static final Map<String, FlowRouter> flowRoutersCache = new ConcurrentHashMap<>();
  private static final String actorPathFormat = "akka.tcp://%s@%s:%s/user/flower/%s_0";
  private final int defaultFlowIndex = 0;


  private final FlowerFactory flowerFactory;
  private final ActorSystem actorSystem;
  protected final ActorRef supervierActor;
  protected final ActorContext actorContext;
  private final FlowerConfig flowerConfig;

  public ServiceActorFactory(FlowerFactory flowerFactory) {
    this.flowerFactory = flowerFactory;
    this.flowerConfig = flowerFactory.getFlowerConfig();
    this.actorSystem = getActorSystem();
    this.supervierActor = this.actorSystem.actorOf(SupervisorActor.props(), "flower");
    this.actorContext = getActorContext();
  }

  public ActorWrapper buildServiceActor(ServiceConfig serviceConfig) {
    return buildServiceActor(serviceConfig, defaultFlowIndex);
  }

  public ActorWrapper buildServiceActor(ServiceConfig serviceConfig, int index) {
    return buildServiceActor(serviceConfig, index, 0);
  }

  public ActorWrapper buildServiceActor(ServiceConfig serviceConfig, int index, int count) {
    final String serviceName = serviceConfig.getServiceName();
    final String cacheKey = serviceName + "_" + index;
    ActorWrapper actorRefWrapper = serviceActorCache.get(cacheKey);
    if (actorRefWrapper != null) {
      return actorRefWrapper;
    }

    if (serviceConfig.isLocal()) {
      ActorRef actorRef = getActorContext().actorOf(ServiceActor.props(serviceName, count), cacheKey);
      actorRefWrapper = new ActorRefWrapper(actorRef).setServiceName(serviceName);
    } else {
      // "akka.tcp://flower@127.0.0.1:2551/user/$a"
      URL url = serviceConfig.getAddresses().iterator().next();
      String actorPath =
          String.format(actorPathFormat, flowerConfig.getName(), url.getHost(), url.getPort(), serviceName, index);
      ActorSelection actorSelection = getActorContext().actorSelection(actorPath);
      actorRefWrapper = new ActorSelectionWrapper(actorSelection).setServiceName(serviceName);
    }

    logger.info("创建服务{}:{}", cacheKey, actorRefWrapper);
    serviceActorCache.put(cacheKey, actorRefWrapper);
    return actorRefWrapper;
  }

  protected ActorContext getActorContext() {
    if (actorContext != null) {
      return actorContext;
    }
    try {
      return (ActorContext) Await
          .result(Patterns.ask(supervierActor, new SupervisorActor.GetActorContext(), DEFAULT_TIMEOUT - 1), timeout);
    } catch (Exception e) {
      logger.error("", e);
      throw new FlowerException("", e);
    }
  }

  private ActorSystem getActorSystem() {
    if (actorSystem != null) {
      return actorSystem;
    }
    FlowerConfig flowerConfig = flowerFactory.getFlowerConfig();
    StringBuffer configBuilder = new StringBuffer();

    if (StringUtil.isNotBlank(flowerConfig.getHost())) {
      configBuilder.append("akka.actor.provider=\"remote\"").append("\r\n");
      configBuilder.append("akka.remote.enabled-transports = [\"akka.remote.netty.tcp\"]").append("\r\n");
      configBuilder.append("akka.remote.netty.tcp.hostname = ").append("\"").append(flowerConfig.getHost()).append("\"")
          .append("\r\n");
      configBuilder.append("akka.remote.netty.tcp.port = \"").append(flowerConfig.getPort()).append("\"");
    }
    logger.info("akka config ：{}", configBuilder.toString());
    Config config = ConfigFactory.parseString(configBuilder.toString()).withFallback(ConfigFactory.load());
    return ActorSystem.create(flowerConfig.getName(), config);
  }


  /**
   * will be cached by flowName + "_" + serviceName
   * 
   * @param flowName flowName
   * @param flowNumbe 数量
   * @return {@link ServiceRouter}
   */
  public FlowRouter buildFlowRouter(String flowName, int flowNumbe) {
    final ServiceConfig serviceConfig = ServiceFlow.getOrCreate(flowName).getHeadServiceConfig();
    final String serviceName = serviceConfig.getServiceName();
    final String routerName = flowName + "_" + serviceName;

    FlowRouter serviceRouter = flowRoutersCache.get(routerName);
    if (serviceRouter == null) {
      serviceRouter = new FlowRouter(flowName, serviceConfig, flowNumbe);
      flowRoutersCache.put(routerName, serviceRouter);
      logger.info("build service Router. flowName : {}, serviceName : {}, flowNumbe : {}", flowName, serviceName,
          flowNumbe);
    }
    return serviceRouter;
  }

  public ServiceRouter buildServiceRouter(ServiceConfig serviceConfig, int flowNumbe) {
    final String serviceName = serviceConfig.getServiceName();
    final String routerName = serviceName + "_" + flowNumbe;

    ServiceRouter serviceRouter = serviceRoutersCache.get(routerName);
    if (serviceRouter == null) {
      serviceRouter = new ServiceRouter(serviceConfig, flowNumbe);
      serviceRoutersCache.put(routerName, serviceRouter);
      logger.info("build service Router. serviceName : {}, flowNumbe : {}", serviceName, flowNumbe);
    }
    return serviceRouter;
  }

  public void shutdown() {
    logger.info("akka system terminate, system : {}", actorSystem);
    actorSystem.terminate();
  }
}
