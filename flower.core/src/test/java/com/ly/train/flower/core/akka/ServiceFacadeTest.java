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
package com.ly.train.flower.core.akka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import com.ly.train.flower.base.TestBase;
import com.ly.train.flower.base.model.User;
import com.ly.train.flower.base.service.ExceptionService;
import com.ly.train.flower.base.service.user.UserServiceA;
import com.ly.train.flower.base.service.user.UserServiceB;
import com.ly.train.flower.base.service.user.UserServiceC1;
import com.ly.train.flower.base.service.user.UserServiceC2;
import com.ly.train.flower.base.service.user.UserServiceD;
import com.ly.train.flower.common.exception.FlowException;
import com.ly.train.flower.core.service.container.ServiceFlow;

/**
 * @author leeyazhou
 * 
 */
public class ServiceFacadeTest extends TestBase {

  @Test
  public void testSyncCallService() throws Exception {
    String flowName = generateFlowName();
    ServiceFlow serviceFlow = serviceFactory.getOrCreateServiceFlow(flowName);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, Arrays.asList(UserServiceC1.class, UserServiceC2.class));
    serviceFlow.buildFlow(Arrays.asList(UserServiceC1.class, UserServiceC2.class), UserServiceD.class);
    serviceFlow.build();
    User user = new User();
    user.setName("响应式编程");
    user.setAge(2);

    Object o = serviceFacade.syncCallService(flowName, user);
    System.out.println(o);
  }

  @Test
  public void testSyncCallAggregateService() throws Exception {
    String flowName = generateFlowName();
    ServiceFlow serviceFlow = serviceFactory.getOrCreateServiceFlow(flowName);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, Arrays.asList(UserServiceC1.class, UserServiceC2.class));
    serviceFlow.buildFlow(Arrays.asList(UserServiceC1.class, UserServiceC2.class), UserServiceD.class);
    serviceFlow.build();
    User user = new User();
    user.setName("响应式编程");
    user.setAge(2);

    List<User> ret = (List<User>) serviceFacade.syncCallService(flowName, user);
    assertNotNull(ret);
    assertEquals(ret.size(), 2);
    assertEquals(5, ret.get(0).getAge());
    assertEquals(5, ret.get(1).getAge());
  }

  @Test
  public void testAsyncCallService() throws Exception {
    String flowName = generateFlowName();
    ServiceFlow serviceFlow = serviceFactory.getOrCreateServiceFlow(flowName);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC1.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC2.class);

    User user = new User();
    user.setName("响应式编程");
    user.setAge(2);
    serviceFacade.asyncCallService(flowName, user);
  }

  @Test(expected = FlowException.class)
  public void testSyncException() throws TimeoutException {
    String flowName = generateFlowName();
    ServiceFlow serviceFlow = serviceFactory.getOrCreateServiceFlow(flowName);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC1.class);
    serviceFlow.buildFlow(UserServiceC1.class, ExceptionService.class);
    serviceFlow.buildFlow(ExceptionService.class, UserServiceC2.class);
    User user = new User();
    user.setName("响应式编程");
    user.setAge(2);

    Object o = serviceFacade.syncCallService(flowName, user);
    System.out.println(o);
  }

  @Test
  public void testAsyncCall() {
    serviceFacade.asyncCall("UserServiceA", new User());
  }

  @Test
  public void testSyncCall() {
    serviceFacade.asyncCall("UserServiceA", new User());
  }
}
