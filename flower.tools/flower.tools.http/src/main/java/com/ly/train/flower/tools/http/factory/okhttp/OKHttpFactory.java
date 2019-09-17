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

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.ly.train.flower.tools.http.HttpFactory;
import com.ly.train.flower.tools.http.config.FlowerHttpConfig;
import com.ly.train.flower.tools.http.config.RequestContext;
import com.ly.train.flower.tools.http.enums.RequestMethod;
import com.ly.train.flower.tools.http.util.NamedThreadFactory;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author leeyazhou
 */
public class OKHttpFactory implements HttpFactory {

  private FlowerHttpConfig httpConfig = new FlowerHttpConfig();

  private OkHttpClient template;

  private final ConcurrentMap<String, OkHttpClient> okHttpClientCache = new ConcurrentHashMap<String, OkHttpClient>();


  public OKHttpFactory() {
    initOKHttp();
  }

  private void initOKHttp() {
    Builder builder = new OkHttpClient().newBuilder();
    builder.connectTimeout(httpConfig.getConnectTimeout(), TimeUnit.MILLISECONDS);
    builder.writeTimeout(httpConfig.getWriteTimeout(), TimeUnit.MILLISECONDS);
    builder.readTimeout(httpConfig.getReadTimeout(), TimeUnit.MILLISECONDS);

    ExecutorService executorService = new ThreadPoolExecutor(8, Runtime.getRuntime().availableProcessors() * 8, 60,
        TimeUnit.SECONDS, new SynchronousQueue<>(), new NamedThreadFactory("flower.http"));

    Dispatcher dispatcher = new Dispatcher(executorService);
    dispatcher.setMaxRequests(256);
    dispatcher.setMaxRequestsPerHost(Math.max(32, Runtime.getRuntime().availableProcessors() * 8));

    builder.dispatcher(dispatcher);
    builder.retryOnConnectionFailure(true);
    builder.connectionPool(new ConnectionPool());

    this.template = builder.build();
  }


  @Override
  public CompletableFuture<String> get(RequestContext requestContext) {
    requestContext.setMethod(RequestMethod.GET);
    return invokeHttp(requestContext);
  }

  @Override
  public CompletableFuture<String> post(RequestContext requestContext) {
    requestContext.setMethod(RequestMethod.POST);
    return invokeHttp(requestContext);
  }

  @Override
  public CompletableFuture<String> put(RequestContext requestContext) {
    requestContext.setMethod(RequestMethod.PUT);
    return invokeHttp(requestContext);
  }

  @Override
  public CompletableFuture<String> patch(RequestContext requestContext) {
    requestContext.setMethod(RequestMethod.PATCH);
    return invokeHttp(requestContext);
  }

  @Override
  public CompletableFuture<String> delete(RequestContext requestContext) {
    requestContext.setMethod(RequestMethod.DELETE);
    return invokeHttp(requestContext);
  }

  protected CompletableFuture<String> invokeHttp(RequestContext requestContext) {
    OkHttpClient client = getOrCreateClient(requestContext);
    Request request = buildRequest(requestContext);
    Call call = client.newCall(request);
    HttpCallback callback = new HttpCallback();
    call.enqueue(callback);
    return callback;
  }

  private Request buildRequest(RequestContext requestContext) {
    Request.Builder builder = new Request.Builder().url(requestContext.getUrl());
    for (Map.Entry<String, String> header : requestContext.getHeaders().entrySet()) {
      builder.addHeader(header.getKey(), header.getValue());
    }
    RequestBody body = null;
    if (requestContext.getRequestBody() == null) {
      FormBody.Builder bodyBuilder = new FormBody.Builder();
      for (Map.Entry<String, String> entry : requestContext.getParameters().entrySet()) {
        bodyBuilder.add(entry.getKey(), entry.getValue());
      }
      body = bodyBuilder.build();
    } else {
      body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), requestContext.getRequestBody());
    }

    switch (requestContext.getMethod()) {
      case POST:
        builder.post(body);
        break;
      case DELETE:
        builder.delete(body);
        break;
      case PUT:
        builder.put(body);
        break;
      case PATCH:
        builder.patch(body);
        break;
      default:
        break;
    }

    return builder.build();
  }

  private OkHttpClient getOrCreateClient(RequestContext requestContext) {
    final String key = requestContext.getConnectTimeout() + "_" + requestContext.getWriteTimeout() + "_"
        + requestContext.getReadTimeout();
    OkHttpClient httpClient = okHttpClientCache.get(key);
    if (httpClient == null) {
      httpClient = template.newBuilder().connectTimeout(requestContext.getConnectTimeout(), TimeUnit.MILLISECONDS)
          .writeTimeout(requestContext.getWriteTimeout(), TimeUnit.MILLISECONDS)
          // .callTimeout(60, TimeUnit.SECONDS)
          .readTimeout(requestContext.getReadTimeout(), TimeUnit.MILLISECONDS).build();
      OkHttpClient acliClient = okHttpClientCache.putIfAbsent(key, httpClient);
      if (acliClient != null) {
        httpClient = acliClient;
      }

    }
    return httpClient;
  }
}
