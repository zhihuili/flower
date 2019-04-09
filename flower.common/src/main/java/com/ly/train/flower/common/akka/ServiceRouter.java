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

import java.util.concurrent.TimeUnit;
import com.ly.train.flower.common.akka.actor.wrapper.ActorRefWrapper;
import com.ly.train.flower.common.akka.actor.wrapper.ActorSelectionWrapper;
import com.ly.train.flower.common.akka.actor.wrapper.ActorWrapper;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.loadbalance.LoadBalance;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.AbstractInit;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class ServiceRouter extends AbstractInit {
  protected static final Logger logger = LoggerFactory.getLogger(ServiceRouter.class);
  private final LoadBalance loadBalance = ExtensionLoader.load(LoadBalance.class).load();
  private int number = 2 << 6;
  private volatile ActorWrapper[] ar;
  private final ServiceConfig serviceConfig;
  private final ServiceActorFactory serviceActorFactory;

  public ServiceRouter(ServiceConfig serviceConfig, ServiceActorFactory serviceActorFactory, int number) {
    this.serviceConfig = serviceConfig;
    this.serviceActorFactory = serviceActorFactory;
    if (number > 0) {
      this.number = number;
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
   * @throws Exception
   */
  public Object syncCallService(ServiceContext serviceContext) {
    ActorWrapper actorRef = chooseOne(serviceContext);
    try {
      Timeout timeout = new Timeout(serviceConfig.getServiceMeta().getTimeout() - 1, TimeUnit.MILLISECONDS);
      Duration duration = Duration.create(serviceConfig.getServiceMeta().getTimeout(), TimeUnit.MILLISECONDS);
      Future<Object> future = null;
      if (actorRef instanceof ActorRefWrapper) {
        future = Patterns.ask(((ActorRefWrapper) actorRef).getActorRef(), serviceContext, timeout);
      } else {
        future = Patterns.ask(((ActorSelectionWrapper) actorRef).getActorSelection(), serviceContext, timeout);
      }
      return Await.result(future, duration);
    } catch (Exception e) {
      throw new FlowerException(" serviceContext : " + serviceContext, e);
    }
  }

  public void asyncCallService(ServiceContext serviceContext, ActorRef sender) {
    ActorWrapper actorRef = chooseOne(serviceContext);
    if (sender == null) {
      sender = ActorRef.noSender();
    }
    actorRef.tell(serviceContext, sender);
  }

  private ActorWrapper[] getServiceActor() {
    if (ar == null) {
      synchronized (this) {
        if (ar == null) {
          ActorWrapper[] t = new ActorWrapper[number];
          for (int i = 0; i < number; i++) {
            t[i] = serviceActorFactory.buildServiceActor(serviceConfig, i, number);
          }
          ar = t;
        }
      }
    }
    return ar;
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
    builder.append(number);
    builder.append(", serviceConfig=");
    builder.append(serviceConfig);
    builder.append("]");
    return builder.toString();
  }

}
