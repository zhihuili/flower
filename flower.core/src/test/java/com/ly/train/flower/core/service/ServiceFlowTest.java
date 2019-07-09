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
package com.ly.train.flower.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import com.ly.train.flower.base.TestBase;
import com.ly.train.flower.base.service.user.UserServiceA;
import com.ly.train.flower.base.service.user.UserServiceB;
import com.ly.train.flower.base.service.user.UserServiceC1;
import com.ly.train.flower.base.service.user.UserServiceC2;
import com.ly.train.flower.base.service.user.UserServiceD;
import com.ly.train.flower.common.util.Pair;
import com.ly.train.flower.core.service.container.ServiceFlow;

/**
 * @author leeyazhou
 * 
 */
public class ServiceFlowTest extends TestBase {
  final String flowName = "demo";

  @Test
  public void testBuildFlowSimple() throws Exception {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC1.class);
    serviceFlow.buildFlow(UserServiceC1.class, UserServiceC2.class);
    serviceFlow.build();
  }


  @Test
  public void testBuildFlowAggregate() throws Exception {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, Arrays.asList(UserServiceC1.class, UserServiceC2.class));
    serviceFlow.buildFlow(Arrays.asList(UserServiceC1.class, UserServiceC2.class), UserServiceD.class);
    serviceFlow.buildFlow(Arrays.asList("ServiceC1", "ServiceC2"), "ServiceD");
    serviceFlow.build();
  }

  @Test
  public void testBuildFlowManyToOne() {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceC1.class);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceC2.class);
    serviceFlow.buildFlow(Arrays.asList(UserServiceB.class, UserServiceC1.class, UserServiceC2.class),
        UserServiceD.class);
    serviceFlow.build();
  }

  @Test
  public void testBuildFlowManyToOneString() {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceC1.class);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceC2.class);
    serviceFlow.buildFlow(Arrays.asList("ServiceB", "ServiceC1", "ServiceC2"), "ServiceD");
    serviceFlow.build();
  }

  @Test
  public void testBuildFlowOneToMany() {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);
    serviceFlow.buildFlow(UserServiceA.class,
        Arrays.asList(UserServiceB.class, UserServiceC1.class, UserServiceC2.class));
    serviceFlow.build();
  }

  @Test
  public void testBuildFlowOneToManyString() {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);
    serviceFlow.buildFlow("ServiceA", Arrays.asList("ServiceB", "ServiceC1", "ServiceC2"));
    serviceFlow.build();
  }

  @Test
  public void testBuildFlowList() {
    List<Pair<String, String>> list = new ArrayList<>();
    list.add(new Pair<String, String>("ServiceA", "ServiceB"));
    list.add(new Pair<String, String>("ServiceA", "ServiceC1"));
    list.add(new Pair<String, String>("ServiceA", "ServiceC2"));

    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);
    serviceFlow.buildFlow(list);
    serviceFlow.build();
  }
}
