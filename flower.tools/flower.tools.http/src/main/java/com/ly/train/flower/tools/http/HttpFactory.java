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
package com.ly.train.flower.tools.http;

import java.util.concurrent.CompletableFuture;
import com.ly.train.flower.tools.http.config.RequestContext;
import com.ly.train.flower.tools.http.factory.httpasyncclient.HttpAsyncClientFactory;
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
  HttpFactory httpAsyncClientFactory = HttpClientFactoryHodler.FACTORY;


  static class OKHttpFactoryHodler {
    private static final HttpFactory FACTORY = new OKHttpFactory();
  }

  static class HttpClientFactoryHodler {
    private static final HttpFactory FACTORY = new HttpAsyncClientFactory();
  }

}
