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
import com.ly.train.flower.common.sample.TestBase;
import com.ly.train.flower.common.sample.programflow.service.ServiceA;
import com.ly.train.flower.common.sample.programflow.service.ServiceB;
import com.ly.train.flower.common.sample.programflow.service.ServiceC;
import com.ly.train.flower.core.service.container.ServiceFlow;

public class Sample extends TestBase {
  private static final String flowName = "programFlow";

  @Test
  public void main() throws Exception {
    buildServiceEnv();

    for (int i = 0; i < 5; i++) {
      flowerFactory.getServiceFacade().asyncCallService(flowName, " Hello World! ");
    }
    Thread.sleep(1000);
    System.out.println((serviceLoader.loadServiceMeta("programServiceB").getParamType()));
  }

  public void buildServiceEnv() {
    serviceFactory.registerService("programServiceA", ServiceA.class);
    serviceFactory.registerService("programServiceB", ServiceB.class);
    serviceFactory.registerService("programServiceC", ServiceC.class);

    // serviceA -> serviceB -> serviceC
    ServiceFlow.getOrCreate(flowName, serviceFactory).buildFlow("programServiceA", "programServiceB")
        .buildFlow("programServiceB", "programServiceC");

  }

}
