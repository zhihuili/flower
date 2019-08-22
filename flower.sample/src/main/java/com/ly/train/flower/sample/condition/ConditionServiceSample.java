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
package com.ly.train.flower.sample.condition;

import com.ly.train.flower.core.service.impl.ConditionService;
import org.junit.Test;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.core.service.container.ServiceFactory;
import com.ly.train.flower.core.service.container.ServiceFlow;
import com.ly.train.flower.sample.TestBase;
import com.ly.train.flower.sample.condition.service.ServiceA;
import com.ly.train.flower.sample.condition.service.ServiceB;
import com.ly.train.flower.sample.condition.service.ServiceC;
import com.ly.train.flower.sample.condition.service.ServiceD;
import com.ly.train.flower.sample.condition.service.ServiceE;
import com.ly.train.flower.sample.condition.service.ServiceF;
import com.ly.train.flower.sample.condition.service.ServiceG;

public class ConditionServiceSample extends TestBase {

  private String flowName = "conditionSample";

  @Test
  public void main() throws Exception {
    buildServiceEnv(flowerFactory);
    flowerFactory.getServiceFacade().asyncCallService(flowName, "c");
    Thread.sleep(3000);
  }

  public void buildServiceEnv(FlowerFactory flowerFactory) {
    ServiceFactory serviceFactory = flowerFactory.getServiceFactory();
    serviceFactory.registerService("serviceA", ServiceA.class);
    serviceFactory.registerService("serviceB", ServiceB.class);
    serviceFactory.registerService("serviceC", ServiceC.class);
    serviceFactory.registerService("serviceE", ServiceE.class);
    serviceFactory.registerService("serviceD", ServiceD.class);
    serviceFactory.registerService("serviceF", ServiceF.class);
    serviceFactory.registerService("serviceG", ServiceG.class);
    serviceFactory.registerService("serviceCondition",
        ConditionService.class.getCanonicalName() + ";serviceF,serviceG");

    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);

    serviceFlow.buildFlow("serviceA", "serviceB");
    serviceFlow.buildFlow("serviceA", "serviceC");
    serviceFlow.buildFlow("serviceC", "serviceD");
    serviceFlow.buildFlow("serviceC", "serviceE");
    serviceFlow.buildFlow("serviceE", "serviceCondition");
    serviceFlow.buildFlow("serviceCondition", "serviceF");
    serviceFlow.buildFlow("serviceCondition", "serviceG");
    serviceFlow.build();
  }

}
