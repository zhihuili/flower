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
package com.ly.train.flower.test.util;

import com.ly.train.flower.common.akka.router.FlowRouter;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceFlow;
import com.ly.train.flower.common.service.container.simple.SimpleFlowerFactory;
import com.ly.train.flower.test.service.BusinessService;
import com.ly.train.flower.test.service.EmailService;
import com.ly.train.flower.test.service.EndService;
import com.ly.train.flower.test.service.StartService;

/**
 * @author leeyazhou
 */
public class FlowerUtil {
  private static FlowerFactory flowerFactory;

  public static FlowerFactory buildFlowerFactory() {
    if (flowerFactory != null) {
      return flowerFactory;
    }
    flowerFactory = new SimpleFlowerFactory();
    flowerFactory.getServiceFactory().registerService(StartService.class.getSimpleName(), StartService.class);
    flowerFactory.getServiceFactory().registerService(BusinessService.class.getSimpleName(), BusinessService.class);
    flowerFactory.getServiceFactory().registerService(EmailService.class.getSimpleName(), EmailService.class);
    flowerFactory.getServiceFactory().registerService(EndService.class.getSimpleName(), EndService.class);
    return flowerFactory;
  }


  public static FlowRouter buildFlowRouter(String flowName, int threadNum) {
    ServiceFlow serviceFlow = buildFlowerFactory().getServiceFactory().getOrCreateServiceFlow(flowName);
    // serviceFlow.buildFlow(StartService.class, EndService.class);
    serviceFlow.buildFlow(StartService.class, BusinessService.class);
    serviceFlow.buildFlow(BusinessService.class, EmailService.class);
    serviceFlow.buildFlow(BusinessService.class, EndService.class);
    return flowerFactory.getActorFactory().buildFlowRouter(flowName, threadNum);
  }

  public static void stop() {
    flowerFactory.stop();
  }
}
