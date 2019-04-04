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

import com.ly.train.flower.common.akka.actor.ActorRefWrapper;
import com.ly.train.flower.common.loadbalance.LoadBalance;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;

public class ServiceRouter {
  protected static final Logger logger = LoggerFactory.getLogger(ServiceRouter.class);
  private static final LoadBalance loadBalance = ExtensionLoader.load(LoadBalance.class).load();
  private int number = 2 << 6;
  private ActorRefWrapper[] ar;
  private String serviceName;

  public ServiceRouter(String serviceName, int number) {
    this.serviceName = serviceName;
    if (number > 0) {
      this.number = number;
    }
  }


  /**
   * 同步调用
   * 
   * @param serviceContext {@link ServiceContext}
   * @return obj
   * @throws Exception
   */
  public Object syncCallService(ServiceContext serviceContext) throws Exception {
    ActorRef actorRef = chooseOne(serviceContext).getActorRef();
    return Await.result(Patterns.ask(actorRef, serviceContext, new Timeout(Constant.defaultTimeout_3S)), Constant.defaultTimeout_3S);
  }

  public void asyncCallService(ServiceContext serviceContext) {
    ActorRefWrapper actorRef = chooseOne(serviceContext);
    actorRef.tell(serviceContext, actorRef.getActorRef());
  }

  private ActorRefWrapper chooseOne(ServiceContext serviceContext) {
    if (ar == null) {
      synchronized (this) {
        if (ar == null) {
          ActorRefWrapper[] t = new ActorRefWrapper[number];
          for (int i = 0; i < number; i++) {
            t[i] = ServiceActorFactory.buildServiceActor(serviceName, i, number);
          }
          ar = t;
        }
      }
    }
    return loadBalance.choose(ar, serviceContext);
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }


}
