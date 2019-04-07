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

import com.ly.train.flower.common.akka.actor.wrapper.ActorRefWrapper;
import com.ly.train.flower.common.akka.actor.wrapper.ActorSelectionWrapper;
import com.ly.train.flower.common.akka.actor.wrapper.ActorWrapper;
import com.ly.train.flower.common.loadbalance.LoadBalance;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.container.simple.SimpleFlowerFactory;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;

public class ServiceRouter {
  protected static final Logger logger = LoggerFactory.getLogger(ServiceRouter.class);
  private static final LoadBalance loadBalance = ExtensionLoader.load(LoadBalance.class).load();
  private int number = 2 << 6;
  private ActorWrapper[] ar;
  private final ServiceConfig serviceConfig;
  private final ServiceActorFactory serviceActorFactory;

  public ServiceRouter(ServiceConfig serviceConfig, int number) {
    this.serviceConfig = serviceConfig;
    this.serviceActorFactory = SimpleFlowerFactory.get().getServiceActorFactory();
    if (number > 0) {
      this.number = number;
    }
    initServiceActor();
  }


  /**
   * 同步调用
   * 
   * @param serviceContext {@link ServiceContext}
   * @return obj
   * @throws Exception
   */
  public Object syncCallService(ServiceContext serviceContext) throws Exception {
    ActorWrapper actorRef = chooseOne(serviceContext);
    if (actorRef instanceof ActorRefWrapper) {
      return Await.result(Patterns.ask(((ActorRefWrapper) actorRef).getActorRef(), serviceContext,
          new Timeout(Constant.defaultTimeout_10S)), Constant.defaultTimeout_10S);
    } else {
      return Await.result(Patterns.ask(((ActorSelectionWrapper) actorRef).getActorSelection(), serviceContext,
          new Timeout(Constant.defaultTimeout_10S)), Constant.defaultTimeout_10S);
    }
  }

  public void asyncCallService(ServiceContext serviceContext) {
    ActorWrapper actorRef = chooseOne(serviceContext);
    actorRef.tell(serviceContext);
  }

  private void initServiceActor() {
    ar = new ActorWrapper[number];
    for (int i = 0; i < number; i++) {
      ar[i] = serviceActorFactory.buildServiceActor(serviceConfig, i, number);
    }
  }

  private ActorWrapper chooseOne(ServiceContext serviceContext) {
    return loadBalance.choose(ar, serviceContext);
  }

  public ServiceConfig getServiceConfig() {
    return serviceConfig;
  }

}
