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
/**
 * 
 */
package com.ly.train.flower.common.actor;

import java.util.concurrent.TimeUnit;
import org.junit.Test;
import com.ly.train.flower.base.TestBase;
import com.ly.train.flower.base.model.User;
import com.ly.train.flower.base.service.ServiceA;
import com.ly.train.flower.base.service.ServiceB;
import com.ly.train.flower.base.service.ServiceC1;
import com.ly.train.flower.base.service.ServiceC2;
import com.ly.train.flower.common.service.container.ServiceFlow;

/**
 * @author leeyazhou
 *
 */
public class ServiceRouterTest extends TestBase {


  @Test
  public void testSyncCallServiceSimple() throws Exception {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName);
    serviceFlow.buildFlow(ServiceA.class, ServiceB.class);
    serviceFlow.buildFlow(ServiceB.class, ServiceC1.class);
    serviceFlow.buildFlow(ServiceB.class, ServiceC2.class);
    final ServiceRouter router = ServiceFacade.buildServiceRouter(flowName, ServiceA.class.getSimpleName(), 2 << 4);

    User user = new User();
    user.setName("响应式编程 ");
    user.setAge(2);
    try {
      Object o = router.syncCallService(user);
      System.out.println("响应结果： " + o);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Thread.sleep(TimeUnit.SECONDS.toMillis(2));
  }

  @Test
  public void testAsyncCallServiceSimple() throws Exception {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName);
    serviceFlow.buildFlow(ServiceA.class, ServiceB.class);
    serviceFlow.buildFlow(ServiceB.class, ServiceC1.class);
    serviceFlow.buildFlow(ServiceB.class, ServiceC2.class);
    final ServiceRouter router = ServiceFacade.buildServiceRouter(flowName, ServiceA.class.getSimpleName(), 2 << 4);

    User user = new User();
    user.setName("响应式编程 ");
    user.setAge(2);
    router.asyncCallService(user);
    Thread.sleep(TimeUnit.SECONDS.toMillis(2));
  }


  @Test
  public void testSyncCallServiceMutliThread() throws Exception {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName);
    serviceFlow.buildFlow(ServiceA.class, ServiceB.class);
    serviceFlow.buildFlow(ServiceB.class, ServiceC1.class);
    serviceFlow.buildFlow(ServiceB.class, ServiceC2.class);
    final ServiceRouter router = ServiceFacade.buildServiceRouter(flowName, ServiceA.class.getSimpleName(), 2 << 4);

    final int threadNum = 10;
    final int numPerThread = 10;
    for (int i = 0; i < threadNum; i++) {
      new Thread(() -> {
        for (int j = 0; j < numPerThread; j++) {

          User user = new User();
          user.setName("响应式编程 - " + j);
          user.setAge(2);
          try {
            Object o = router.syncCallService(user);
            System.out.println("响应结果 ： " + o);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }).start();
    }
    Thread.sleep(TimeUnit.SECONDS.toMillis(5));
  }

  @Test
  public void testAsyncCallServiceMutliThread() throws Exception {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName);
    serviceFlow.buildFlow(ServiceA.class, ServiceB.class);
    serviceFlow.buildFlow(ServiceB.class, ServiceC1.class);
    serviceFlow.buildFlow(ServiceB.class, ServiceC2.class);
    final ServiceRouter router = ServiceFacade.buildServiceRouter(flowName, ServiceA.class.getSimpleName(), 2 << 4);

    final int threadNum = 10;
    final int numPerThread = 10;
    for (int i = 0; i < threadNum; i++) {
      new Thread(() -> {
        for (int j = 0; j < numPerThread; j++) {

          User user = new User();
          user.setName("响应式编程 - " + j);
          user.setAge(2);
          try {
            router.asyncCallService(user);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }).start();
    }
    Thread.sleep(TimeUnit.SECONDS.toMillis(5));
  }
}
