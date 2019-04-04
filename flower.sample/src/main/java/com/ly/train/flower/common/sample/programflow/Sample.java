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

import org.junit.Test;
import com.ly.train.flower.common.akka.ServiceFacade;
import com.ly.train.flower.common.sample.programflow.service.ServiceA;
import com.ly.train.flower.common.sample.programflow.service.ServiceB;
import com.ly.train.flower.common.sample.programflow.service.ServiceC;
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.service.container.ServiceFlow;
import com.ly.train.flower.common.service.container.ServiceLoader;

public class Sample {
  private static final String flowName = "programFlow";

  @Test
  public void main() throws Exception {
    buildServiceEnv();

    for (int i = 0; i < 5; i++) {
      ServiceFacade.asyncCallService(flowName, " Hello World! ");
    }
    Thread.sleep(1000);
    System.out.println((ServiceLoader.getInstance().loadServiceMeta("serviceB").getParamType()));
  }

  public static void buildServiceEnv() {
    ServiceFactory.registerService("programServiceA", ServiceA.class);
    ServiceFactory.registerService("programServiceB", ServiceB.class);
    ServiceFactory.registerService("programServiceC", ServiceC.class);

    // serviceA -> serviceB -> serviceC
    ServiceFlow.getOrCreate(flowName).buildFlow("programServiceA", "programServiceB").buildFlow("programServiceB", "programServiceC");

  }

}
