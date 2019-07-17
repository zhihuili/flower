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
package com.ly.train.flower.tools.http.factory.okhttp;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Test;
import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.tools.http.HttpFactory;
import com.ly.train.flower.tools.http.config.RequestContext;

/**
 * @author leeyazhou
 */
public class TestHttp {
  // private String baseUrl = "http://127.0.0.1:12051/flowerdsfasync/";
  private String baseUrl = "http://10.100.216.147:12051/flowerdsfasync/";
  AtomicInteger failed = new AtomicInteger();
  AtomicInteger total = new AtomicInteger();

  @Test
  public void testHttp() {
    int i = 0;
    while (i++ < 500) {
      testDead();
      // testPost();
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        // e.printStackTrace();
      }
    }
    i = 0;
    while (i++ < 500) {
      testPost();
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        // e.printStackTrace();
      }
    }
    System.out.println(failed + "/" + total);
  }

  private int post = 0;

  public void testPost() {
    System.out.println("test post : " + post++);
    RequestContext requestContext = new RequestContext("http://www.baidu.com");
    // setRequestBody(requestContext);
    CompletableFuture<String> result = HttpFactory.okHttpFactory.get(requestContext);
    total.incrementAndGet();
    result.whenComplete((r, e) -> {
      if (e != null) {
        failed.incrementAndGet();
        // e.printStackTrace();
        r = e.getMessage();
      }
      System.out.println("post结果：" + (r));
    });
  }

  int dead = 0;

  public void testDead() {
    System.out.println("test dead : " + dead++);
    RequestContext requestContext = new RequestContext(baseUrl + "demo/dead");
    setRequestBody(requestContext);
    requestContext.setConnectTimeout(60000);
    requestContext.setWriteTimeout(60000);
    requestContext.setReadTimeout(60000);
    CompletableFuture<String> result = HttpFactory.okHttpFactory.post(requestContext);
    result.whenComplete((r, e) -> {
      if (e != null) {
        // e.printStackTrace();
        r = e.getMessage();
      }
      System.out.println("dead结果：" + r);
    });
  }

  private void setRequestBody(RequestContext requestContext) {
    JSONObject body = new JSONObject();
    body.put("name", "liyazhou");
    body.put("age", 201);

    requestContext.setRequestBody(body.toJSONString());
  }

  @After
  public void after() throws InterruptedException {
    Thread.sleep(30000);
  }
}
