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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.ly.train.flower.base.service.StringService;
import com.ly.train.flower.base.service.StringService2;
import com.ly.train.flower.common.akka.FlowRouter;
import com.ly.train.flower.common.service.container.FlowerFactory;
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
    flowerFactory2.init();
    flowerFactory2.getServiceFactory().registerService("StringService2", StringService2.class);
    flowerFactory2.getServiceFactory().registerService("StringService", StringService.class.getName());
    flowerFactory2.getServiceFactory().getOrCreateServiceFlow("aaa").buildFlow("StringService2", "StringService");
    flowerFactory2.getServiceActorFactory().buildFlowRouter("aaa", 2);
    System.out.println("初始化服务2完成，开始初始化服务1");
    System.out.println("初始化服务2完成，开始初始化服务1");
    flowerFactory1 = new SimpleFlowerFactory(configLocation3);
    flowerFactory1.init();
    flowerFactory1.getServiceFactory().registerService("StringService", StringService.class);
  }

  @AfterClass
  public static void after() {
    flowerFactory1.stop();
    flowerFactory2.stop();
  }

  @Test
  public void testActorSelectionSyncCall() throws Exception {
    String message = "我是测试远程消息";
    final String flowName = "actorSelection";


    flowerFactory1.getServiceFactory().getOrCreateServiceFlow(flowName).buildFlow("StringService", "StringService2");

    Object ret = flowerFactory1.getServiceFacade().syncCallService(flowName, message);
    System.out.println("返回结果：" + ret);
    Thread.sleep(1000);
  }

  @Test
  public void testActorSelectionAsyncCall() throws Exception {
    String message = "我是测试远程消息";
    final String flowName = "actorSelection";
    flowerFactory1.getServiceFactory().getOrCreateServiceFlow(flowName).buildFlow("StringService", "StringService2")
        .build();
    FlowRouter flowRouter = flowerFactory1.getServiceActorFactory().buildFlowRouter(flowName, 1);
    flowRouter.asyncCallService(message);
    Thread.sleep(3000);
  }
}
