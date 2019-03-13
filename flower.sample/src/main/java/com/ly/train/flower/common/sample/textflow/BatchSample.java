/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ly.train.flower.common.sample.textflow;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.util.EnvBuilder;
import com.ly.train.flower.common.util.FileUtil;


public class BatchSample {
  private static final Logger logger = LoggerFactory.getLogger(BatchSample.class);

  public static void main(String[] args) throws Exception {
    EnvBuilder.buildEnv(BatchSample.class);

    int count = 0;
    Map<String, Message1> message1Map = new HashMap<>();
    for (int i = 20; i < 1111; ++i) {
      Message2 m2 = new Message2(i, String.valueOf(i));
      Message1 m1 = new Message1();
      m1.setM2(m2);
      message1Map.put(String.valueOf(i), m1);
      count++;
    }

    int resultCount = 0;
    for (String key : message1Map.keySet()) {
      Message1 m1 = message1Map.get(key);
      Object o = ServiceFacade.syncCallService("sample", "service1", m1);
      logger.info("" + o);
      Assert.assertEquals(((Message3) o).getM2().getName(), m1.getM2().getName());
      Assert.assertEquals(Integer.parseInt(key), ((Message3) o).getM2().getAge() - 1);
      resultCount++;
    }
    Assert.assertEquals(count, resultCount);
    logger.info("count:" + count + " resultCount:" + resultCount);
    logger.info("test ok");
    System.exit(0);
  }

  public static void buildServiceEnv() throws Exception {

    ServiceFactory.registerService(FileUtil.readService("/sample.services"));
    ServiceFlow.buildFlow("sample", FileUtil.readFlow("/sample.flow"));

  }
}
