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

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author leeyazhou
 */
public class HttpCallback extends CompletableFuture<String> implements Callback {

  @Override
  public void onFailure(Call arg0, IOException ex) {
    completeExceptionally(ex);
  }

  @Override
  public void onResponse(Call arg0, Response response) throws IOException {
    try {
      complete(response.body().string());
    } catch (Exception e) {
      completeExceptionally(e);
    }
  }

}
