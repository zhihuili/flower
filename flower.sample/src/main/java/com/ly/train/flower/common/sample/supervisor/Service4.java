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

import java.util.Set;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;

public class Service4 implements Service<Set,Message3> {

  @Override
  public Message3 process(Set message, ServiceContext context) {
    Message2 m = new Message2();
    for (Object o : message) {

      if (o instanceof Integer) {
        m.setAge((Integer) o);
      }
      if (o instanceof String) {
        m.setName(String.valueOf(o));
      }
    }
    Message3 m3 = new Message3();
    m3.setM2(m);
    // pi();
    // sleep();
    // System.out.println(System.currentTimeMillis());
    return m3;
  }

  /**
   * calculate PI, only waste CPU time
   */
  private void pi() {
    double y = 1.0;
    for (int i = 0; i <= 100; i++) {
      double π = 3 * Math.pow(2, i) * y;
      y = Math.sqrt(2 - Math.sqrt(4 - y * y));
    }
  }

  private void sleep() {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
