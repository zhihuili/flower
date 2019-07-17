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
package com.ly.train.flower.tools.http.factory.httpasyncclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.tools.http.HttpFactory;
import com.ly.train.flower.tools.http.config.FlowerHttpConfig;
import com.ly.train.flower.tools.http.config.RequestContext;
import com.ly.train.flower.tools.http.enums.RequestMethod;
import com.ly.train.flower.tools.http.util.NamedThreadFactory;

/**
 * @author leeyazhou
 */
public class HttpAsyncClientFactory implements HttpFactory {
  private static final Logger logger = LoggerFactory.getLogger(HttpAsyncClientFactory.class);
  private FlowerHttpConfig httpConfig = new FlowerHttpConfig();
  private PoolingNHttpClientConnectionManager connectionManager;
  private CloseableHttpAsyncClient asyncHttpClient = null;

  public HttpAsyncClientFactory() {
    try {
      initHttpClient();
    } catch (Exception e) {
      logger.error("", e);
    }
  }



  private void initHttpClient() throws Exception {
    IOReactorConfig ioReactorConfig =
        IOReactorConfig.custom().setIoThreadCount(Runtime.getRuntime().availableProcessors() * 8)
            .setConnectTimeout(httpConfig.getConnectTimeout()).setSoTimeout(httpConfig.getReadTimeout()).build();

    ConnectingIOReactor ioReactor =
        new DefaultConnectingIOReactor(ioReactorConfig, new NamedThreadFactory("flower.http"));
    this.connectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
    this.connectionManager.setMaxTotal(256);
    this.connectionManager.setDefaultMaxPerRoute(Math.max(32, Runtime.getRuntime().availableProcessors() * 8));
    this.asyncHttpClient = HttpAsyncClients.custom().setConnectionManager(connectionManager).build();
    this.asyncHttpClient.start();
  }


  @Override
  public CompletableFuture<String> get(RequestContext requestContext) {
    requestContext.setMethod(RequestMethod.GET);
    HttpRequestBase request = buildRequest(requestContext);
    HttpClientCallback future = new HttpClientCallback();
    asyncHttpClient.execute(request, future);
    return future;
  }

  @Override
  public CompletableFuture<String> post(RequestContext requestContext) {
    requestContext.setMethod(RequestMethod.POST);
    HttpRequestBase request = buildRequest(requestContext);
    HttpClientCallback callback = new HttpClientCallback();
    asyncHttpClient.execute(request, callback);
    return callback;
  }


  @Override
  public CompletableFuture<String> put(RequestContext requestContext) {
    requestContext.setMethod(RequestMethod.PUT);
    HttpRequestBase request = buildRequest(requestContext);
    HttpClientCallback callback = new HttpClientCallback();
    asyncHttpClient.execute(request, callback);
    return callback;
  }

  @Override
  public CompletableFuture<String> patch(RequestContext requestContext) {
    requestContext.setMethod(RequestMethod.PATCH);
    HttpRequestBase request = buildRequest(requestContext);
    HttpClientCallback callback = new HttpClientCallback();
    asyncHttpClient.execute(request, callback);
    return callback;
  }

  @Override
  public CompletableFuture<String> delete(RequestContext requestContext) {
    requestContext.setMethod(RequestMethod.DELETE);
    HttpRequestBase request = buildRequest(requestContext);
    HttpClientCallback callback = new HttpClientCallback();
    asyncHttpClient.execute(request, callback);
    return callback;
  }

  private HttpRequestBase buildRequest(RequestContext requestContext) {
    RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(requestContext.getConnectTimeout())
        .setConnectTimeout(requestContext.getConnectTimeout()).setSocketTimeout(requestContext.getReadTimeout())
        .build();
    HttpRequestBase request = new HttpPost(requestContext.getUrl());
    switch (requestContext.getMethod()) {
      case GET:
        request = new HttpGet(requestContext.getUrl());
        break;
      case POST:
        request = new HttpPost(requestContext.getUrl());
        break;
      case PUT:
        request = new HttpPut(requestContext.getUrl());
        break;
      case PATCH:
        request = new HttpPatch(requestContext.getUrl());
        break;
      case DELETE:
        request = new HttpDelete(requestContext.getUrl());
        break;
      default:
        break;
    }
    request.setConfig(config);

    for (Map.Entry<String, String> entry : requestContext.getHeaders().entrySet()) {
      request.addHeader(entry.getKey(), entry.getValue());
    }
    HttpEntity entity = null;
    try {
      if (requestContext.getRequestBody() != null) {
        request.addHeader("Content-type", "application/json; charset=utf-8");
        request.setHeader("Accept", "application/json");
        entity = new StringEntity(requestContext.getRequestBody(), "UTF-8");
      } else {
        List<NameValuePair> parameters = new ArrayList<>();
        for (Map.Entry<String, String> entry : requestContext.getParameters().entrySet()) {
          parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        entity = new UrlEncodedFormEntity(parameters);
      }
    } catch (Exception e) {
      logger.error("", e);
    }
    if (entity != null && request instanceof HttpEntityEnclosingRequestBase) {
      ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
    }

    return request;
  }
}
