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

import java.util.concurrent.CompletableFuture;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;

/**
 * @author leeyazhou
 */
public class HttpClientCallback extends CompletableFuture<String> implements FutureCallback<HttpResponse> {

  @Override
  public void completed(HttpResponse result) {
    try {
      String r = EntityUtils.toString(result.getEntity());
      complete(r);
    } catch (Exception e) {
      completeExceptionally(e);
    }
  }

  @Override
  public void failed(Exception ex) {
    completeExceptionally(ex);
  }

  @Override
  public void cancelled() {
    completeExceptionally(new IllegalStateException("it is canceled."));
  }

}
