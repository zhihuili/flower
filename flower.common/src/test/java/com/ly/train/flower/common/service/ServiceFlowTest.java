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
package com.ly.train.flower.common.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import com.ly.train.flower.base.TestBase;
import com.ly.train.flower.base.service.ServiceA;
import com.ly.train.flower.base.service.ServiceB;
import com.ly.train.flower.base.service.ServiceC1;
import com.ly.train.flower.base.service.ServiceC2;
import com.ly.train.flower.base.service.ServiceD;
import com.ly.train.flower.common.service.container.ServiceFlow;
import com.ly.train.flower.common.util.Pair;

/**
 * @author leeyazhou
 *
 */
public class ServiceFlowTest extends TestBase {
  final String flowName = "demo";

  @Test
  public void testBuildFlowSimple() throws Exception {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName);
    serviceFlow.buildFlow(ServiceA.class, ServiceB.class);
    serviceFlow.buildFlow(ServiceB.class, ServiceC1.class);
    serviceFlow.buildFlow(ServiceC1.class, ServiceC2.class);
    serviceFlow.build();
  }


  @Test
  public void testBuildFlowAggregate() throws Exception {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName);
    serviceFlow.buildFlow(ServiceA.class, ServiceB.class);
    serviceFlow.buildFlow(ServiceB.class, Arrays.asList(ServiceC1.class, ServiceC2.class));
    serviceFlow.buildFlow("ServiceC1", Arrays.asList("ServiceD"));
    serviceFlow.buildFlow("ServiceC2", Arrays.asList("ServiceD"));
    serviceFlow.buildFlow(Arrays.asList("ServiceC1", "ServiceC2"), "ServiceD");
    serviceFlow.build();
  }

  @Test
  public void testBuildFlowManyToOne() {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName);
    serviceFlow.buildFlow(ServiceA.class, ServiceB.class);
    serviceFlow.buildFlow(ServiceA.class, ServiceC1.class);
    serviceFlow.buildFlow(ServiceA.class, ServiceC2.class);
    serviceFlow.buildFlow(Arrays.asList(ServiceB.class, ServiceC1.class, ServiceC2.class), ServiceD.class);
    serviceFlow.build();
  }
  @Test
  public void testBuildFlowManyToOneString() {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName);
    serviceFlow.buildFlow(ServiceA.class, ServiceB.class);
    serviceFlow.buildFlow(ServiceA.class, ServiceC1.class);
    serviceFlow.buildFlow(ServiceA.class, ServiceC2.class);
    serviceFlow.buildFlow(Arrays.asList("ServiceB", "ServiceC1", "ServiceC2"), "ServiceD");
    serviceFlow.build();
  }

  @Test
  public void testBuildFlowOneToMany() {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName);
    serviceFlow.buildFlow(ServiceA.class, Arrays.asList(ServiceB.class, ServiceC1.class, ServiceC2.class));
    serviceFlow.build();
  }

  @Test
  public void testBuildFlowOneToManyString() {
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName);
    serviceFlow.buildFlow("ServiceA", Arrays.asList("ServiceB", "ServiceC1", "ServiceC2"));
    serviceFlow.build();
  }

  @Test
  public void testBuildFlowList() {
    List<Pair<String, String>> list = new ArrayList<>();
    list.add(new Pair<String, String>("ServiceA", "ServiceB"));
    list.add(new Pair<String, String>("ServiceA", "ServiceC1"));
    list.add(new Pair<String, String>("ServiceA", "ServiceC2"));
    
    ServiceFlow serviceFlow = ServiceFlow.getOrCreate(flowName);
    serviceFlow.buildFlow(list);
    serviceFlow.build();
  }
}
