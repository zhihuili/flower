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
package com.ly.train.flower.common.util;

public class HttpClientBuilder {
  private String url;
  private String param;

  private int connectionTime = 15000;
  private int readTimeout = 6000;
  private int retryTimes = 3;

  public HttpClientBuilder setUrl(String url) {
    this.url = url;
    return this;
  }

  public HttpClientBuilder setParam(String param) {
    this.param = param;
    return this;
  }


  public int getConnectionTime() {
    return connectionTime;
  }

  public HttpClientBuilder setConnectionTime(int connectionTime) {
    this.connectionTime = connectionTime;
    return this;
  }

  public HttpClientBuilder setRetryTimes(int retryTimes) {
    this.retryTimes = retryTimes;
    return this;
  }

  public int getRetryTimes() {
    return retryTimes;
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  public HttpClientBuilder setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
    return this;
  }


  public String getUrl() {
    return url;
  }

  public String getParam() {
    return param;
  }

  public HttpClient build() {
    return new HttpClient(this);
  }

}
