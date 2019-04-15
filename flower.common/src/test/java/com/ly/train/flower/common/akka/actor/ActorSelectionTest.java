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
package com.ly.train.flower.common.akka.actor;

import java.util.Arrays;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.ly.train.flower.base.model.User;
import com.ly.train.flower.base.service.ServiceA;
import com.ly.train.flower.base.service.ServiceB;
import com.ly.train.flower.base.service.ServiceC1;
import com.ly.train.flower.base.service.ServiceC2;
import com.ly.train.flower.base.service.ServiceD;
import com.ly.train.flower.common.akka.FlowRouter;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceFlow;
import com.ly.train.flower.common.service.container.simple.SimpleFlowerFactory;

/**
 * @author leeyazhou
 *
 */
public class ActorSelectionTest {

  private static FlowerFactory flowerFactory1;
  private static FlowerFactory flowerFactory2;

  @BeforeClass
  public static void before() {
    String configLocation4 =
        Thread.currentThread().getContextClassLoader().getResource("conf/flower_25004.yml").getPath();
    String configLocation3 =
        Thread.currentThread().getContextClassLoader().getResource("conf/flower_25003.yml").getPath();
    flowerFactory2 = new SimpleFlowerFactory(configLocation4);
    flowerFactory2.start();
    // flowerFactory2.getServiceFactory().registerService(ServiceA.class.getSimpleName(),
    // ServiceA.class);
    flowerFactory2.getServiceFactory().registerService(ServiceB.class.getSimpleName(), ServiceB.class);
    flowerFactory2.getServiceFactory().registerService(ServiceC1.class.getSimpleName(), ServiceC1.class);
    flowerFactory2.getServiceFactory().registerService(ServiceC2.class.getSimpleName(), ServiceC2.class);
    flowerFactory2.getServiceFactory().registerService(ServiceD.class.getSimpleName(), ServiceD.class);



    System.out.println("初始化服务2完成，开始初始化服务1");
    System.out.println("初始化服务2完成，开始初始化服务1");
    flowerFactory1 = new SimpleFlowerFactory(configLocation3);
    flowerFactory1.start();
    flowerFactory1.getServiceFactory().registerService(ServiceA.class.getSimpleName(), ServiceA.class);
  }

  @AfterClass
  public static void after() {
    flowerFactory1.stop();
    flowerFactory2.stop();
  }

  @Test
  public void testActorSelectionSyncCall() throws Exception {
    User message = new User();
    message.setAge(0);
    message.setName("A");
    final String flowName = "actorSelection";

    ServiceFlow serviceFlow = flowerFactory1.getServiceFactory().getOrCreateServiceFlow(flowName);
    serviceFlow.buildFlow(ServiceA.class, ServiceB.class);
    serviceFlow.buildFlow(ServiceB.class, ServiceC1.class);
    serviceFlow.buildFlow(ServiceB.class, ServiceC2.class);
    serviceFlow.buildFlow(Arrays.asList(ServiceC1.class, ServiceC2.class), ServiceD.class);
    serviceFlow.build();

    Object ret = flowerFactory1.getServiceFacade().syncCallService(flowName, message);
    System.out.println("返回结果：" + ret);
  }

  @Test
  public void testActorSelectionAsyncCall() throws Exception {
    User message = new User();
    message.setAge(0);
    message.setName("A");
    final String flowName = "actorSelection";
    ServiceFlow serviceFlow = flowerFactory1.getServiceFactory().getOrCreateServiceFlow(flowName);
    serviceFlow.buildFlow(ServiceA.class, ServiceB.class);
    serviceFlow.buildFlow(ServiceB.class, ServiceC1.class);
    serviceFlow.buildFlow(ServiceB.class, ServiceC2.class);
    serviceFlow.buildFlow(Arrays.asList(ServiceC1.class, ServiceC2.class), ServiceD.class);
    serviceFlow.build();
    FlowRouter flowRouter = flowerFactory1.getServiceActorFactory().buildFlowRouter(flowName, 8);
    flowRouter.asyncCallService(message);
    Thread.sleep(3000);
  }
}
