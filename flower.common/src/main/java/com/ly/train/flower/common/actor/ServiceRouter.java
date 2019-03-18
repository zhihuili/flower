/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ly.train.flower.common.actor;

import java.io.IOException;
import javax.servlet.AsyncContext;
import com.ly.train.flower.common.service.message.FlowMessage;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;

public class ServiceRouter {

  private int number;
  private int currentIndex = 0;
  private ActorRef[] ar;

  public ServiceRouter(String flowName, String serviceName, int number) {
    this.number = number;
    ar = new ActorRef[number];
    for (int i = 0; i < number; i++) {
      ar[i] = ServiceActorFactory.buildServiceActor(flowName, serviceName, i);
    }
  }

  public void asyncCallService(Object message) throws IOException {
    asyncCallService(message, null);
  }

  public void asyncCallService(Object message, AsyncContext ctx) throws IOException {
    FlowMessage flowMessage = ServiceUtil.buildFlowMessage(message);
    ServiceUtil.makeWebContext(flowMessage, ctx);
    ar[randomIndex()].tell(flowMessage, null);
  }

  public Object syncCallService(Object o) throws Exception {
    FlowMessage flowMessage = ServiceUtil.buildFlowMessage(o);
    ServiceUtil.makeWebContext(flowMessage, null);
    return Await.result(
        Patterns.ask(ar[randomIndex()], flowMessage, new Timeout(ServiceFacade.duration)),
        ServiceFacade.duration);
  }

  private int randomIndex() {
    if (number == 1) {
      return 0;
    }
    int index = (int) (Math.random() * number);
    return index;
  }

  private synchronized int roundIndex() {
    if (currentIndex < (number - 1)) {
      return ++currentIndex;
    }
    currentIndex = 0;
    return currentIndex;
  }
}
