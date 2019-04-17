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
package com.ly.train.flower.base;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import com.ly.train.flower.base.service.ExceptionService;
import com.ly.train.flower.base.service.ServiceA;
import com.ly.train.flower.base.service.ServiceB;
import com.ly.train.flower.base.service.ServiceC1;
import com.ly.train.flower.base.service.ServiceC2;
import com.ly.train.flower.base.service.ServiceD;
import com.ly.train.flower.common.akka.ServiceFacade;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.service.container.ServiceLoader;
import com.ly.train.flower.common.service.container.simple.SimpleFlowerFactory;

/**
 * @author leeyazhou
 *
 */
public class TestBase {
  protected static final String flowName = "sample";
  protected static FlowerFactory flowerFactory;
  protected static ServiceFactory serviceFactory;
  protected static ServiceLoader serviceLoader;
  protected static ServiceFacade serviceFacade;

  @BeforeClass
  public static void before() {
    flowerFactory = new SimpleFlowerFactory();
//    flowerFactory.start();
    serviceFactory = flowerFactory.getServiceFactory();
    serviceLoader = serviceFactory.getServiceLoader();
    serviceFacade = flowerFactory.getServiceFacade();

    serviceFactory.registerService(ServiceA.class.getSimpleName(), ServiceA.class);
    serviceFactory.registerService(ServiceB.class.getSimpleName(), ServiceB.class);
    serviceFactory.registerService(ServiceC1.class.getSimpleName(), ServiceC1.class);
    serviceFactory.registerService(ServiceC2.class.getSimpleName(), ServiceC2.class);
    serviceFactory.registerService(ServiceD.class.getSimpleName(), ServiceD.class);
    serviceFactory.registerService(ExceptionService.class.getSimpleName(), ExceptionService.class);
  }

  @AfterClass
  public static void stop() {
    flowerFactory.stop();
  }
}
