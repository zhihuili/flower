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
package com.ly.train.flower.core.akka.router;

import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import com.ly.train.flower.base.TestBase;
import com.ly.train.flower.base.model.User;
import com.ly.train.flower.base.service.user.UserServiceA;
import com.ly.train.flower.base.service.user.UserServiceB;
import com.ly.train.flower.base.service.user.UserServiceC1;
import com.ly.train.flower.base.service.user.UserServiceC2;
import com.ly.train.flower.common.annotation.FlowerServiceUtil;
import com.ly.train.flower.common.core.config.ServiceConfig;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.core.akka.router.ServiceRouter;
import com.ly.train.flower.core.service.container.ServiceFlow;
import com.ly.train.flower.core.service.container.util.ServiceContextUtil;

/**
 * @author leeyazhou
 * 
 */
public class ServiceRouterTest extends TestBase {

  private ServiceRouter serviceRouter;


  private ServiceRouter getServiceRouter() {
    if (serviceRouter != null) {
      return serviceRouter;
    }
    ServiceConfig serviceConfig = new ServiceConfig();
    serviceConfig.setServiceMeta(serviceLoader.loadServiceMeta("UserServiceB"));
    serviceConfig.setFlowName(flowName);
    serviceConfig.setServiceName("UserServiceB");
    serviceRouter = serviceFacade.buildServiceRouter(serviceConfig, -1);
    return serviceRouter;
  }

  @Test
  public void testSyncCallServiceSimple() throws Exception {

    ServiceFlow serviceFlow = serviceFactory.getOrCreateServiceFlow(flowName);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC1.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC2.class);
    serviceFlow.build();


    User user = new User();
    user.setName("响应式编程 ");
    user.setAge(2);

    ServiceContext serviceContext = ServiceContextUtil.context(user);
    serviceContext.setFlowName(flowName);
    serviceContext.setCurrentServiceName(FlowerServiceUtil.getServiceName(UserServiceA.class));
    Object o = getServiceRouter().syncCallService(serviceContext);
    System.out.println("响应结果： " + o);
  }

  @Test
  public void testAsyncCallServiceSimple() throws Exception {
    ServiceFlow serviceFlow = serviceFactory.getOrCreateServiceFlow(flowName);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC1.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC2.class);


    User user = new User();
    user.setName("响应式编程 ");
    user.setAge(2);
    ServiceContext serviceContext = ServiceContextUtil.context(user);
    serviceContext.setFlowName(flowName);
    serviceContext.setCurrentServiceName(FlowerServiceUtil.getServiceName(UserServiceA.class));
    getServiceRouter().asyncCallService(serviceContext);
    Thread.sleep(TimeUnit.SECONDS.toMillis(2));
  }


  @Test
  public void testSyncCallServiceMutliThread() throws Exception {
    ServiceFlow serviceFlow = serviceFactory.getOrCreateServiceFlow(flowName);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC1.class);

    final int threadNum = 100;
    final int numPerThread = 100;
    for (int i = 0; i < threadNum; i++) {
      final int temp = i;
      new Thread(() -> {
        for (int j = 0; j < numPerThread; j++) {
          final String name = "响应式编程 - " + temp + "-" + j;
          User user = new User();
          user.setName(name);
          user.setAge(2);
          try {
            ServiceContext serviceContext = ServiceContextUtil.context(user);
            serviceContext.setCurrentServiceName(FlowerServiceUtil.getServiceName(UserServiceA.class));
            serviceContext.setFlowName(flowName);
            User o = (User) getServiceRouter().syncCallService(serviceContext);

            Assert.assertEquals(name, o.getName());
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
    ServiceFlow serviceFlow = serviceFactory.getOrCreateServiceFlow(flowName);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC1.class);

    final int threadNum = 10;
    final int numPerThread = 10;
    for (int i = 0; i < threadNum; i++) {
      new Thread(() -> {
        for (int j = 0; j < numPerThread; j++) {

          User user = new User();
          user.setName("响应式编程 - " + j);
          user.setAge(2);
          try {
            ServiceContext serviceContext = ServiceContextUtil.context(user);
            serviceContext.setFlowName(flowName);
            serviceContext.setCurrentServiceName(FlowerServiceUtil.getServiceName(UserServiceA.class));
            getServiceRouter().asyncCallService(serviceContext);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }).start();
    }
    Thread.sleep(TimeUnit.SECONDS.toMillis(5));
  }
}
