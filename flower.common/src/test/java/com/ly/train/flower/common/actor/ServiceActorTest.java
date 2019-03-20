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
package com.ly.train.flower.common.actor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import com.ly.train.flower.common.actor.model.User;
import com.ly.train.flower.common.actor.service.ServiceA;
import com.ly.train.flower.common.actor.service.ServiceB;
import com.ly.train.flower.common.actor.service.ServiceC1;
import com.ly.train.flower.common.actor.service.ServiceC2;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.container.ServiceFactory;

/**
 * @author leeyazhou
 *
 */
public class ServiceActorTest {

  @Test
  public void test() throws Exception {

    ServiceFactory.registerService("ServiceA", ServiceA.class);
    ServiceFactory.registerService("ServiceB", ServiceB.class);
    ServiceFactory.registerService("ServiceC1", ServiceC1.class);
    ServiceFactory.registerService("ServiceC2", ServiceC2.class);

    ServiceFlow.buildFlow("test", ServiceA.class, ServiceB.class);
    ServiceFlow.buildFlow("test", ServiceB.class, ServiceC1.class);
    ServiceFlow.buildFlow("test", ServiceB.class, ServiceC2.class);
    final ServiceRouter router = ServiceFacade.buildServiceRouter("test", ServiceA.class.getSimpleName(), 2 << 4);


    for (int i = 0; i < 100; i++) {
      new Thread(() -> {
        User user = new User();
        user.setName("响应式编程");
        user.setAge(2);
        try {
          router.asyncCallService(user);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();
    }
    Thread.sleep(TimeUnit.SECONDS.toMillis(5));

  }
}
