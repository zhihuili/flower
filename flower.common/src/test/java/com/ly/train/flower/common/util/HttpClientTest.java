/**
 * 
 */
package com.ly.train.flower.common.util;

import org.junit.Test;

/**
 * @author leeyazhou
 *
 */
public class HttpClientTest {
  @Test
  public void testGet() {
    String ret = HttpClient.builder().setUrl("https://www.baidu.com").build().get();
    System.out.println(ret);
  }

  @Test
  public void testPost() {
    String ret = HttpClient.builder().setUrl("http://127.0.0.1:8080/service/list").setParam("name=liyazhou").build().post();
    System.out.println(ret);
  }
}
