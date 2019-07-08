package com.ly.train.flower.tools.http;

import java.util.concurrent.CompletableFuture;
import com.ly.train.flower.tools.http.config.RequestContext;
import com.ly.train.flower.tools.http.factory.httpclient.HttpClientFactory;
import com.ly.train.flower.tools.http.factory.okhttp.OKHttpFactory;

/**
 * @author leeyazhou
 */
public interface HttpFactory {


  CompletableFuture<String> get(RequestContext requestContext);


  CompletableFuture<String> post(RequestContext requestContext);

  CompletableFuture<String> put(RequestContext requestContext);

  CompletableFuture<String> patch(RequestContext requestContext);

  CompletableFuture<String> delete(RequestContext requestContext);



  /**
   * 基于OKHttp
   */
  HttpFactory okHttpFactory = OKHttpFactoryHodler.FACTORY;
  
  /**
   * 基于Apache HttpClient
   */
  HttpFactory httpClientFactory = HttpClientFactoryHodler.FACTORY;


  static class OKHttpFactoryHodler {
    private static final HttpFactory FACTORY = new OKHttpFactory();
  }
  static class HttpClientFactoryHodler {
    private static final HttpFactory FACTORY = new HttpClientFactory();
  }

}
