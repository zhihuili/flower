package com.ly.train.flower.tools.http.config;

import java.io.Serializable;

/**
 * @author leeyazhou
 */
public class FlowerHttpConfig implements Serializable {

  private static final long serialVersionUID = 1L;

  private int connectTimeout = 3000;

  private int readTimeout = 6000;

  private int writeTimeout = 3000;

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  public void setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
  }

  public int getWriteTimeout() {
    return writeTimeout;
  }

  public void setWriteTimeout(int writeTimeout) {
    this.writeTimeout = writeTimeout;
  }



}
