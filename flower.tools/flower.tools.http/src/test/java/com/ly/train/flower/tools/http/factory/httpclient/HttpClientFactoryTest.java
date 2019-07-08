package com.ly.train.flower.tools.http.factory.httpclient;

import java.util.concurrent.CompletableFuture;
import org.junit.AfterClass;
import org.junit.Test;
import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.tools.http.HttpFactory;
import com.ly.train.flower.tools.http.config.RequestContext;

/**
 * @author leeyazhou
 */
public class HttpClientFactoryTest {
  private String baseUrl = "http://10.100.216.147:12051/flowerdsfasync/";
  // private String baseUrl = "http://127.0.0.1:12051/flowerdsfasync/";

  @Test
  public void testGet() {
    RequestContext requestContext = new RequestContext(baseUrl + "demo/get");
    int i = 0;
    while (i++ < 1000) {
      CompletableFuture<String> result = HttpFactory.httpClientFactory.get(requestContext);
      result.whenComplete((r, e) -> {
        System.out.println(Thread.currentThread().getName() + "-结果：" + r);
        if (e != null) {
          e.printStackTrace();
        }
      });
    }
  }

  @Test
  public void tesPost() {
    RequestContext requestContext = new RequestContext(baseUrl + "demo/post");
    setRequestBody(requestContext);
    CompletableFuture<String> result = HttpFactory.httpClientFactory.post(requestContext);
    result.whenComplete((r, e) -> {
      System.out.println("结果：" + r);
      if (e != null) {
        e.printStackTrace();
      }
    });
  }

  @Test
  public void tesPut() {
    RequestContext requestContext = new RequestContext(baseUrl + "demo/put");
    setRequestBody(requestContext);
    CompletableFuture<String> result = HttpFactory.httpClientFactory.put(requestContext);
    result.whenComplete((r, e) -> {
      System.out.println("结果：" + r);
      if (e != null) {
        e.printStackTrace();
      }
    });
  }

  private void setRequestBody(RequestContext requestContext) {
    JSONObject body = new JSONObject();
    body.put("name", "liyazhou");
    body.put("age", 201);

    requestContext.setRequestBody(body.toJSONString());
  }

  @Test
  public void testDelete() {
    RequestContext requestContext = new RequestContext(baseUrl + "demo/delete");
    setRequestBody(requestContext);
    CompletableFuture<String> result = HttpFactory.httpClientFactory.delete(requestContext);
    result.whenComplete((r, e) -> {
      System.out.println("结果：" + r);
      if (e != null) {
        e.printStackTrace();
      }
    });
  }

  @Test
  public void testPatch() {
    RequestContext requestContext = new RequestContext(baseUrl + "demo/patch");
    setRequestBody(requestContext);
    CompletableFuture<String> result = HttpFactory.httpClientFactory.patch(requestContext);
    result.whenComplete((r, e) -> {
      System.out.println("结果：" + r);
      if (e != null) {
        e.printStackTrace();
      }
    });
  }

  @AfterClass
  public static void afterClass() throws InterruptedException {
    Thread.sleep(10000);
  }
}
