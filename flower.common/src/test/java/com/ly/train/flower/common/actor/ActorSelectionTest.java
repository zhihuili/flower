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
package com.ly.train.flower.common.actor;

import org.junit.Test;
import com.ly.train.flower.base.TestBase;
import com.ly.train.flower.common.akka.ServiceFacade;
import com.ly.train.flower.common.service.container.ServiceFlow;

/**
 * @author leeyazhou
 *
 */
public class ActorSelectionTest extends TestBase {

  @Test
  public void testActorSelection() throws Exception {
    final String flowName = "actorSelection";
    ServiceFlow.getOrCreate(flowName).buildFlow("StringService", "UserService")
        .buildFlow("UserService", "StringService2").build();
    Object ret = ServiceFacade.syncCallService(flowName, "我是测试远程消息。");
    System.out.println("返回结果：" + ret);
    Thread.sleep(10000);
  }

  @Test
  public void testActorSelectionAsyncCall() throws Exception {
    final String flowName = "actorSelection";
    ServiceFlow.getOrCreate(flowName).buildFlow("StringService", "UserService").build();
    int i = 0;
    while (i++ < 100) {
      ServiceFacade.asyncCallService(flowName, "我是测试远程消息。" + i);
      Thread.sleep(1000);
    }
    Thread.sleep(30000);
  }
}
