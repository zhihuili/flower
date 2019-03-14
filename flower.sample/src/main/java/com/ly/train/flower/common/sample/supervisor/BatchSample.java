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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.util.EnvBuilder;

public class BatchSample {

  public static void main(String[] args) throws Exception {
    EnvBuilder.buildEnv(BatchSample.class);
    File file = new File("test.file");
    if (file.exists()) {
      System.out.println(file.getAbsolutePath());
      FileUtils.deleteQuietly(file);
    }
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
    for (String key : message1Map.keySet()) {
      Message1 m1 = message1Map.get(key);
      try {
        //
        Object o = ServiceFacade.syncCallService("supervisor", "SupervisorService1", m1);
        System.out.println(o);
        Assert.assertEquals(((Message3)o).getM2().getName(), m1.getM2().getName());
        Assert.assertEquals(Integer.parseInt(key), ((Message3) o).getM2().getAge() - 1) ;
      } catch (TimeoutException e) {
        e.printStackTrace();
      }

      resultCount++;
    }
    Assert.assertEquals(count, resultCount);
    System.out.println("count:" + count + " resultCount:" + resultCount);
    System.out.println("test ok");
    ServiceFacade.shutdown();
  }

}
