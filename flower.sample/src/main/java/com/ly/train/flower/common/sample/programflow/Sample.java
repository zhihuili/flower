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
package com.ly.train.flower.common.sample.programflow;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.service.container.ServiceLoader;

public class Sample {

  public static void main(String[] args) throws Exception {
    buildServiceEnv();
    // ActorRef actor = ServiceActorFactory.buildServiceActor("serviceA");
    // actor.tell(" Hello World!", null);

    for (int i = 0; i < 5; i++) {
      ServiceFacade.asyncCallService("sample", "serviceA", " Hello World! ");
    }
    Thread.sleep(1000);
    System.out.println((ServiceLoader.getInstance().getServiceMessageType("serviceB")));
    System.exit(0);
  }

  public static void buildServiceEnv() {
    ServiceFactory.registerService("serviceA",
        "com.ly.train.flower.common.sample.programflow.ServiceA");
    ServiceFactory.registerService("serviceB",
        "com.ly.train.flower.common.sample.programflow.ServiceB");
    ServiceFactory.registerService("serviceC",
        "com.ly.train.flower.common.sample.programflow.ServiceC");

    // serviceA -> serviceB -> serviceC
    ServiceFlow.buildFlow("sample", "serviceA", "serviceB");
    ServiceFlow.buildFlow("sample", "serviceB", "serviceC");

  }

}
