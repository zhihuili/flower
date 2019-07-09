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
package com.ly.train.flower.sample.supervisor;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import com.ly.train.flower.sample.TestBase;
import com.ly.train.flower.sample.supervisor.model.Message1;
import com.ly.train.flower.sample.supervisor.model.Message2;

public class BatchSample extends TestBase {

  @Test
  public void main() throws Exception {
    int count = 0;
    Map<String, Message1> message1Map = new HashMap<>();
    for (int i = 10; i < 20; ++i) {
      Message2 m2 = new Message2(i, String.valueOf(i));
      Message1 m1 = new Message1();
      m1.setM2(m2);
      message1Map.put(String.valueOf(i), m1);
      count++;
    }

    int resultCount = 0;
    for (Map.Entry<String, Message1> entry : message1Map.entrySet()) {
      Object o = serviceFacade.syncCallService("supervisor", entry.getValue());
      System.out.println(o);
      Assert.assertEquals(((Message1) o).getM2().getName(), entry.getValue().getM2().getName());
      Assert.assertEquals(Integer.parseInt(entry.getKey()), ((Message1) o).getM2().getAge() - 1);

      resultCount++;
    }
    Assert.assertEquals(count, resultCount);
    System.out.println("count:" + count + " resultCount:" + resultCount);
    System.out.println("test ok");
  }

}
