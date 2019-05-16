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
package com.ly.train.flower.common.akka.router;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.ly.train.flower.common.akka.actor.wrapper.ActorRefWrapper;
import com.ly.train.flower.common.akka.actor.wrapper.ActorSelectionWrapper;
import com.ly.train.flower.common.akka.actor.wrapper.ActorWrapper;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.loadbalance.LoadBalance;
import com.ly.train.flower.common.serializer.Codec;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.AbstractInit;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class ServiceRouter extends AbstractInit implements Router {
  protected static final Logger logger = LoggerFactory.getLogger(ServiceRouter.class);
  private final LoadBalance loadBalance = ExtensionLoader.load(LoadBalance.class).load();
  private int actorNumber = 2 << 6;
  private volatile List<ActorWrapper> actors = new ArrayList<>();
  private final ServiceConfig serviceConfig;
  private final FlowerFactory flowerFactory;
  private final String returnType;

  public ServiceRouter(ServiceConfig serviceConfig, FlowerFactory flowerFactory, int actorNumber) {
    this.serviceConfig = serviceConfig;
    this.flowerFactory = flowerFactory;
    this.returnType = serviceConfig.getServiceMeta().getResultType();
    if (actorNumber > 0) {
      this.actorNumber = actorNumber;
    }
  }

  @Override
  protected void doInit() {
    getServiceActor();
  }

  /**
   * 同步调用
   * 
   * @param serviceContext {@link ServiceContext}
   * @return obj
   * @throws TimeoutException timeout
   */
  public Object syncCallService(ServiceContext serviceContext) throws TimeoutException {
    serviceContext.setSync(true);
    ActorWrapper actorRef = chooseOne(serviceContext);
    Timeout timeout = new Timeout(serviceConfig.getTimeout(), TimeUnit.MILLISECONDS);
    Future<Object> future = null;
    if (actorRef instanceof ActorRefWrapper) {
      future = Patterns.ask(((ActorRefWrapper) actorRef).getActorRef(), serviceContext, timeout);
    } else {
      future = Patterns.ask(((ActorSelectionWrapper) actorRef).getActorSelection(), serviceContext, timeout);
    }
    try {
      Duration duration = Duration.create(serviceConfig.getTimeout(), TimeUnit.MILLISECONDS);
      FlowMessage response = (FlowMessage) Await.result(future, duration);
      if (response.isError()) {
        throw new FlowerException("fail to invoke \r\nCaused by: " + response.getException());
      }
      byte[] messageByte = response.getMessage();
      return Codec.Hessian.decode(messageByte, null);
    } catch (FlowerException e) {
      throw e;
    } catch (TimeoutException e) {
      throw e;
    } catch (Exception e) {
      throw new FlowerException(returnType + ", serviceContext : " + serviceContext, e);
    }
  }

  public void asyncCallService(ServiceContext serviceContext) {
    ActorWrapper actorRef = chooseOne(serviceContext);
    actorRef.tell(serviceContext);
  }

  public void asyncCallService(ServiceContext serviceContext, ActorRef sender) {
    ActorWrapper actorRef = chooseOne(serviceContext);
    actorRef.tell(serviceContext, sender);
  }

  private List<ActorWrapper> getServiceActor() {
    if (actors.isEmpty()) {
      synchronized (this) {
        if (actors.isEmpty()) {
          for (int i = 0; i < actorNumber; i++) {
            ActorWrapper actor = flowerFactory.getActorFactory().buildServiceActor(serviceConfig, i);
            actors.add(actor);
          }
        }
      }
    }
    return actors;
  }

  private ActorWrapper chooseOne(ServiceContext serviceContext) {
    return loadBalance.choose(getServiceActor(), serviceContext);
  }

  public ServiceConfig getServiceConfig() {
    return serviceConfig;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ServiceRouter [loadBalance=");
    builder.append(loadBalance);
    builder.append(", number=");
    builder.append(actorNumber);
    builder.append(", serviceConfig=");
    builder.append(serviceConfig);
    builder.append("]");
    return builder.toString();
  }

}
