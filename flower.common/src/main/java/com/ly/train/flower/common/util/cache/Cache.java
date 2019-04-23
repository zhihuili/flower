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
package com.ly.train.flower.common.util.cache;

import java.io.Serializable;

public class Cache<T> implements Serializable {
  private static final long serialVersionUID = 1L;
  private String key;
  private volatile T value;
  private long timeToLive;
  private boolean expired;

  public Cache() {}

  public Cache(String key, T value, long timeToLive, boolean expired) {
    this.key = key;
    this.value = value;
    this.timeToLive = timeToLive;
    this.expired = expired;
  }

  public Cache(String key, T value, long timeToLive) {
    this(key, value, timeToLive, false);
  }

  public String getKey() {
    return key;
  }

  /**
   * @return the timeToLive
   */
  public long getTimeToLive() {
    return timeToLive;
  }

  public T getValue() {
    return value;
  }

  public Cache<T> setKey(String key) {
    this.key = key;
    return this;
  }

  public Cache<T> setTimeToLive(long timeToLive) {
    this.timeToLive = timeToLive + System.currentTimeMillis();
    return this;
  }

  public Cache<T> setValue(T value) {
    this.value = value;
    return this;
  }

  public boolean isExpired() {
    return expired;
  }

  public Cache<T> setExpired(boolean expired) {
    this.expired = expired;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Cache [key=");
    builder.append(key);
    builder.append(", value=");
    builder.append(value);
    builder.append(", timeToLive=");
    builder.append(timeToLive);
    builder.append(", expired=");
    builder.append(expired);
    builder.append("]");
    return builder.toString();
  }


}
