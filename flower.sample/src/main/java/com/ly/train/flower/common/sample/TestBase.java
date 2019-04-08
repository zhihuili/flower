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
package com.ly.train.flower.common.sample;

import org.junit.Before;
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

  protected FlowerFactory flowerFactory;
  protected ServiceFacade serviceFacade;

  protected ServiceFactory serviceFactory;
  protected ServiceLoader serviceLoader;

  @Before
  public void before() {
    this.flowerFactory = new SimpleFlowerFactory();
    this.serviceFacade = flowerFactory.getServiceFacade();
    this.serviceFactory = flowerFactory.getServiceFactory();
    this.serviceLoader = serviceFactory.getServiceLoader();
  }
}
