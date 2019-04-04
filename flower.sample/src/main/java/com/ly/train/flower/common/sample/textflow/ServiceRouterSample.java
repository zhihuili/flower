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
package com.ly.train.flower.common.sample.textflow;

import org.junit.Test;
import com.ly.train.flower.common.akka.FlowRouter;
import com.ly.train.flower.common.akka.ServiceFacade;
import com.ly.train.flower.common.sample.textflow.model.Message1;
import com.ly.train.flower.common.sample.textflow.model.Message2;

public class ServiceRouterSample {

  @Test
  public void main() throws Exception {

    int loopNumber = 500;

    Message2 m2 = new Message2(10, "Zhihui");
    Message1 m1 = new Message1();
    m1.setM2(m2);

    // 200 flows

    FlowRouter sr = ServiceFacade.buildFlowRouter("sample", 200);
    long begin = System.currentTimeMillis();
    for (int i = 0; i < loopNumber; i++) {
      sr.asyncCallService(m1);
    }
    long end = System.currentTimeMillis();
    System.out.println("200 flows cost time: " + (end - begin));

    // single flow
    begin = System.currentTimeMillis();
    for (int i = 0; i < loopNumber; i++) {
      // ServiceFacade.asyncCallService("sample", "service1", m1);
    }
    end = System.currentTimeMillis();
    System.out.println("single flow cost time: " + (end - begin));
  }

}
