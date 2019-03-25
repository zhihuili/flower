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
/**
 * 
 */
package com.ly.train.flower.common.util;

import java.io.Serializable;

/**
 * @author leeyazhou
 *
 */
public class Pair<K, V> implements Serializable {

  private static final long serialVersionUID = 1L;

  public Pair() {}

  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  private K key;
  private V value;

  public K getKey() {
    return key;
  }

  public void setKey(K key) {
    this.key = key;
  }

  public V getValue() {
    return value;
  }

  public void setValue(V value) {
    this.value = value;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Pair [key=");
    builder.append(key);
    builder.append(", value=");
    builder.append(value);
    builder.append("]");
    return builder.toString();
  }



}
