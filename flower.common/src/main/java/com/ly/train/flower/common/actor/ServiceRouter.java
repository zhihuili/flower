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
package com.ly.train.flower.common.actor;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import javax.servlet.AsyncContext;
import com.ly.train.flower.common.service.container.ServiceContext;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;

public class ServiceRouter {

  private int number = 2 << 6;
  private int currentIndex = 0;
  private ActorRef[] ar;
  private String flowName;
  private String serviceName;

  public ServiceRouter(String flowName, String serviceName, int number) {
    this.number = number;
    this.ar = new ActorRef[number];
    this.serviceName = serviceName;
    for (int i = 0; i < number; i++) {
      this.ar[i] = ServiceActorFactory.buildServiceActor(flowName, serviceName, i);
    }
  }

  public void asyncCallService(Object message) throws IOException {
    asyncCallService(message, null);
  }

  /**
   * 异步调用
   * 
   * @param message
   * @param ctx
   * @throws IOException
   */
  public <T> void asyncCallService(T message, AsyncContext ctx) throws IOException {
    ServiceContext serviceContext = ServiceContext.context(message, ctx);
    serviceContext.setFlowName(flowName);
    this.ar[randomIndex()].tell(serviceContext, ActorRef.noSender());
  }

  /**
   * 同步调用
   * 
   * @param message message
   * @return obj
   * @throws Exception
   */
  public Object syncCallService(Object message) throws Exception {
    ServiceContext serviceContext = ServiceContext.context(message);
    serviceContext.setSync(true);
    return Await.result(Patterns.ask(ar[randomIndex()], serviceContext, new Timeout(ServiceFacade.duration)), ServiceFacade.duration);
  }

  private int randomIndex() {
    if (number == 1) {
      return 0;
    }
    int index = ThreadLocalRandom.current().nextInt(number);
    return index;
  }

  protected synchronized int roundIndex() {
    if (number == 1) {
      return 0;
    }
    if (currentIndex < (number - 1)) {
      return ++currentIndex;
    }
    currentIndex = 0;
    return currentIndex;
  }


  /**
   * 当actor个数为2^n个数时才可以使用
   * 
   * @return int 
   */
  protected int bitRandomIndex() {
    if (number == 1) {
      return 0;
    }
    if (currentIndex > 1024) {
      currentIndex = 0;
    }
    return (currentIndex++) & (number - 1);
  }

  protected int moduleRandomIndex() {
    if (number == 1) {
      return 0;
    }
    if (currentIndex > 1024) {
      currentIndex = 0;
    }
    return (currentIndex++) % (number);
  }

  public String getFlowName() {
    return flowName;
  }

  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }


}
