package com.ly.train.flower.tools.http.factory.okhttp;

import java.util.concurrent.CompletableFuture;
import org.junit.Test;
import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.tools.http.HttpFactory;
import com.ly.train.flower.tools.http.config.RequestContext;

/**
 * @author leeyazhou
 */
public class TestHttp2 {
  // private String baseUrl = "http://127.0.0.1:12051/flowerdsfasync/";
  private String baseUrl = "http://10.100.216.147:12051/flowerdsfasync/";

  @Test
  public void testHttp() {
    int i = 0;
    while (i++ < 500) {
      testDead();
      System.out.println("请求次数：" + i);
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    int j = 0;
    while (j++ < 500) {
      testPost();
      System.out.println("请求次数：" + j);
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void testPost() {
    RequestContext requestContext = new RequestContext("http://www.baidu.com");
//    RequestContext requestContext = new RequestContext(baseUrl + "demo/post");
    setRequestBody(requestContext);
    CompletableFuture<String> result = HttpFactory.okHttpFactory.post(requestContext);
    result.whenComplete((r, e) -> {
      System.out.println("post结果：" + (e == null));
      if (e != null) {
        System.err.println("post error : " + e.getMessage());
      }
    });
  }

  public void testDead() {
    RequestContext requestContext = new RequestContext(baseUrl + "demo/dead");
    setRequestBody(requestContext);
    requestContext.setConnectTimeout(60000);
    requestContext.setWriteTimeout(60000);
    requestContext.setReadTimeout(60000);
    CompletableFuture<String> result = HttpFactory.okHttpFactory.post(requestContext);
    result.whenComplete((r, e) -> {
      System.out.println("dead结果：" + r);
      if (e != null) {
        System.err.println("dead error : " + e.getMessage());
//        e.printStackTrace();
      }
    });
  }

  private void setRequestBody(RequestContext requestContext) {
    JSONObject body = new JSONObject();
    body.put("name", "liyazhou");
    body.put("age", 201);

    requestContext.setRequestBody(body.toJSONString());
  }
}
