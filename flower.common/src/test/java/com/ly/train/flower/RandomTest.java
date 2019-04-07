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
package com.ly.train.flower;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.base.service.ServiceA;
import com.ly.train.flower.common.service.container.ServiceLoader;
import com.ly.train.flower.common.service.container.ServiceMeta;

public class RandomTest {

  @Test
  public void testJSON() {

    ServiceLoader.getInstance().registerServiceType("aaa", ServiceA.class);
    ServiceMeta meta = ServiceLoader.getInstance().loadServiceMeta("aaa");

    String json = JSONObject.toJSONString(meta);
    System.out.println("前对象：" + meta);
    System.out.println("前JSON" + json);
    System.out.println("后对象：" + JSONObject.parseObject(json, ServiceMeta.class));

  }

  @Test
  public void main() {
    System.out.println("threadNum,Random,ThreadLocalRandom");
    for (int threadNum = 50; threadNum <= 500; threadNum += 50) {
      ExecutorService poolR = Executors.newFixedThreadPool(threadNum);
      long RStartTime = System.currentTimeMillis();
      for (int i = 0; i < threadNum; i++) {
        poolR.execute(new UtilRandom());
      }
      try {
        poolR.shutdown();
        poolR.awaitTermination(100, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      String str = "" + threadNum + "," + (System.currentTimeMillis() - RStartTime) + ",";

      ExecutorService poolTLR = Executors.newFixedThreadPool(threadNum);
      long TLRStartTime = System.currentTimeMillis();
      for (int i = 0; i < threadNum; i++) {
        poolTLR.execute(new LocalThreadRandom());
      }
      try {
        poolTLR.shutdown();
        poolTLR.awaitTermination(60, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println(str + (System.currentTimeMillis() - TLRStartTime));
    }
  }

  private static final int NUMBER = 10000;

  // from java.util.concurrent.
  private static class LocalThreadRandom implements Runnable {
    @Override
    public void run() {
      long index = 0;
      for (int i = 0; i < NUMBER; i++) {
        index += ThreadLocalRandom.current().nextInt(400);
      }
    }
  }

  // from java.util
  private static class UtilRandom implements Runnable {
    Random random = new Random();

    @Override
    public void run() {
      long index = 0;
      for (int i = 0; i < NUMBER; i++) {
        index += random.nextInt(400);
      }
    }
  }
}
