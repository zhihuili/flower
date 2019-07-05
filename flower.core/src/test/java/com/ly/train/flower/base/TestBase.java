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
import com.ly.train.flower.base.service.user.UserServiceA;
import com.ly.train.flower.base.service.user.UserServiceB;
import com.ly.train.flower.base.service.user.UserServiceC1;
import com.ly.train.flower.base.service.user.UserServiceC2;
import com.ly.train.flower.base.service.user.UserServiceD;
import com.ly.train.flower.core.akka.ServiceFacade;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.core.service.container.ServiceFactory;
import com.ly.train.flower.core.service.container.ServiceLoader;
import com.ly.train.flower.core.service.container.simple.SimpleFlowerFactory;

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
    serviceFactory = flowerFactory.getServiceFactory();
    serviceLoader = serviceFactory.getServiceLoader();
    serviceFacade = flowerFactory.getServiceFacade();

    serviceFactory.registerService(UserServiceA.class.getSimpleName(), UserServiceA.class);
    serviceFactory.registerService(UserServiceB.class.getSimpleName(), UserServiceB.class);
    serviceFactory.registerService(UserServiceC1.class.getSimpleName(), UserServiceC1.class);
    serviceFactory.registerService(UserServiceC2.class.getSimpleName(), UserServiceC2.class);
    serviceFactory.registerService(UserServiceD.class.getSimpleName(), UserServiceD.class);
    serviceFactory.registerService(ExceptionService.class.getSimpleName(), ExceptionService.class);
  }

  @AfterClass
  public static void stop() throws InterruptedException {
    Thread.sleep(2000);
    flowerFactory.stop();
  }
}
