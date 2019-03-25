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

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import com.ly.train.flower.base.service.ServiceA;
import com.ly.train.flower.base.service.ServiceB;
import com.ly.train.flower.base.service.ServiceC1;
import com.ly.train.flower.base.service.ServiceC2;
import com.ly.train.flower.common.service.container.ServiceFactory;

/**
 * @author leeyazhou
 *
 */
public class ServiceFlowTest {

  @Before
  public void before() {
    ServiceFactory.registerService(ServiceA.class.getSimpleName(), ServiceA.class);
    ServiceFactory.registerService(ServiceB.class.getSimpleName(), ServiceB.class);
    ServiceFactory.registerService(ServiceC1.class.getSimpleName(), ServiceC1.class);
    ServiceFactory.registerService(ServiceC2.class.getSimpleName(), ServiceC2.class);
  }

  @Test
  public void testBuild() {

    ServiceFlow serviceFlow = ServiceFlow.getOrCreate("demo");
    serviceFlow.buildFlow(ServiceA.class, ServiceB.class);
    serviceFlow.buildParelelFlow(ServiceB.class, Arrays.asList(ServiceC1.class, ServiceC2.class));
    serviceFlow.buildParelelFlow("ServiceC1", Arrays.asList("ServiceC2"));
    System.out.println(serviceFlow.toString());
  }

}
