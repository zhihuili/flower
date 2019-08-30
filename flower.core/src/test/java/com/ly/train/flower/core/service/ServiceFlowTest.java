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

  @Test
  public void testBuildFlowSimple() throws Exception {
    final String flowName = generateFlowName();
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC1.class);
    serviceFlow.buildFlow(UserServiceC1.class, UserServiceC2.class);
    serviceFlow.build();
  }


  @Test
  public void testBuildFlowAggregate() throws Exception {
    final String flowName = generateFlowName();
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, Arrays.asList(UserServiceC1.class, UserServiceC2.class));
    serviceFlow.buildFlow(Arrays.asList(UserServiceC1.class, UserServiceC2.class), UserServiceD.class);
    serviceFlow.buildFlow(Arrays.asList("UserServiceC1", "UserServiceC2"), "UserServiceD");
    serviceFlow.build();
  }

  @Test
  public void testBuildFlowManyToOne() {
    final String flowName = generateFlowName();
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
    final String flowName = generateFlowName();
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceC1.class);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceC2.class);
    serviceFlow.buildFlow(Arrays.asList("UserServiceB", "UserServiceC1", "UserServiceC2"), "UserServiceD");
    serviceFlow.build();
  }

  @Test
  public void testBuildFlowOneToMany() {
    final String flowName = generateFlowName();
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);
    serviceFlow.buildFlow(UserServiceA.class,
        Arrays.asList(UserServiceB.class, UserServiceC1.class, UserServiceC2.class));
    serviceFlow.build();
  }

  @Test
  public void testBuildFlowOneToManyString() {
    final String flowName = generateFlowName();
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);
    serviceFlow.buildFlow("UserServiceA", Arrays.asList("UserServiceB", "UserServiceC1", "UserServiceC2"));
    serviceFlow.build();
  }

  @Test
  public void testBuildFlowList() {
    final String flowName = generateFlowName();
    List<Pair<String, String>> list = new ArrayList<>();
    list.add(new Pair<String, String>("UserServiceA", "UserServiceB"));
    list.add(new Pair<String, String>("UserServiceA", "UserServiceC1"));
    list.add(new Pair<String, String>("UserServiceA", "UserServiceC2"));

    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName, serviceFactory);
    serviceFlow.buildFlow(list);
    serviceFlow.build();
  }
}
