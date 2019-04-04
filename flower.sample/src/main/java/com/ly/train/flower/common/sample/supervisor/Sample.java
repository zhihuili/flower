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
package com.ly.train.flower.common.sample.supervisor;

import java.util.concurrent.TimeoutException;
import org.junit.Test;
import com.ly.train.flower.common.akka.ServiceFacade;
import com.ly.train.flower.common.sample.supervisor.model.Message1;
import com.ly.train.flower.common.sample.supervisor.model.Message2;

public class Sample {

  @Test
  public void main() throws Exception {
    {
      Message2 m2 = new Message2(10000, "Zhihui");
      Message1 m1 = new Message1();
      m1.setM2(m2);
      try {
        System.out.println(ServiceFacade.syncCallService("supervisor", m1));
      } catch (TimeoutException e) {
        e.printStackTrace();
      }
    }

    Thread.sleep(2000);
    System.out.println("20000");
    {
      Message2 m2 = new Message2(10, "Zhihzzzzui");
      Message1 m1 = new Message1();
      m1.setM2(m2);

      try {
        System.out.println(ServiceFacade.syncCallService("supervisor", m1));
      } catch (TimeoutException e) {
        e.printStackTrace();
      }
    }
    Thread.sleep(2000);
    System.out.println("30000");
  }

}
