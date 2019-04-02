package com.ly.train.flower.common.util;

public class HttpClientBuilder {
  private String url;
  private String param;

  private int connectionTime = 15000;
  private int readTimeout = 6000;

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
