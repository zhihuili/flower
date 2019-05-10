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
import com.ly.train.flower.base.service.user.UserServiceA;
import com.ly.train.flower.base.service.user.UserServiceB;
import com.ly.train.flower.base.service.user.UserServiceC1;
import com.ly.train.flower.base.service.user.UserServiceC2;
import com.ly.train.flower.base.service.user.UserServiceD;
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
    flowerFactory2 = new SimpleFlowerFactory(configLocation4);
    flowerFactory2.start();
    // flowerFactory2.getServiceFactory().registerService(ServiceA.class.getSimpleName(),
    // ServiceA.class);
    flowerFactory2.getServiceFactory().registerService(UserServiceB.class.getSimpleName(), UserServiceB.class);
    flowerFactory2.getServiceFactory().registerService(UserServiceC1.class.getSimpleName(), UserServiceC1.class);
    flowerFactory2.getServiceFactory().registerService(UserServiceC2.class.getSimpleName(), UserServiceC2.class);
    flowerFactory2.getServiceFactory().registerService(UserServiceD.class.getSimpleName(), UserServiceD.class);



    System.out.println("初始化服务2完成，开始初始化服务1");
    System.out.println("初始化服务2完成，开始初始化服务1");
    String configLocation3 =
        Thread.currentThread().getContextClassLoader().getResource("conf/flower_25003.yml").getPath();
    flowerFactory1 = new SimpleFlowerFactory(configLocation3);
    flowerFactory1.start();
    flowerFactory1.getServiceFactory().registerService(UserServiceA.class.getSimpleName(), UserServiceA.class);
  }

  @AfterClass
  public static void after() {
    flowerFactory2.stop();
    flowerFactory1.stop();
  }

  @Test
  public void testActorSelectionSyncCall() throws Exception {
    User message = new User();
    message.setAge(0);
    message.setName("A");
    final String flowName = "actorSelection";

    ServiceFlow serviceFlow = flowerFactory1.getServiceFactory().getOrCreateServiceFlow(flowName);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC1.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC2.class);
    serviceFlow.buildFlow(Arrays.asList(UserServiceC1.class, UserServiceC2.class), UserServiceD.class);
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
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC1.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC2.class);
    serviceFlow.buildFlow(Arrays.asList(UserServiceC1.class, UserServiceC2.class), UserServiceD.class);
    serviceFlow.build();
    FlowRouter flowRouter = flowerFactory1.getServiceActorFactory().buildFlowRouter(flowName, 8);
    flowRouter.asyncCallService(message);
    Thread.sleep(3000);
  }
}
