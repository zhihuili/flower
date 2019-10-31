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
package com.ly.train.flower.core.service.container;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.core.proxy.MethodProxy;
import com.ly.train.flower.common.core.service.Service;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.config.FlowerConfig;

/**
 * @author leeyazhou
 */
public class ServiceFactoryTest {
  private FlowerFactory flowerFactory = Mockito.mock(FlowerFactory.class);

  private ServiceFactory serviceFactory;

  @Before
  public void before() {
    Mockito.when(flowerFactory.getFlowerConfig()).then(an -> {
      return Mockito.mock(FlowerConfig.class);
    });
    serviceFactory = new ServiceFactory(flowerFactory);
    serviceFactory.init();
  }

  @Test
  public void testRegisterService() {
    serviceFactory.registerService(DemoService.class);

    MethodProxy methodProxy = serviceFactory.getService(DemoService.class.getName() + ".say");
    Assert.assertNotNull(methodProxy);
    Assert.assertNotNull(serviceFactory.getService(DemoService.class.getSimpleName()));
    methodProxy.process("aaa", null);
  }
}


@FlowerService
class DemoService implements Service<String, String> {

  @FlowerService
  public String say(String mess, ServiceContext context) {
    return mess;
  }

  @Override
  public String process(String message, ServiceContext context) throws Throwable {
    return message;
  }

}

@FlowerService
class DemoService2{
  public String handle(String message, ServiceContext context) {
    return message;
  }
}
