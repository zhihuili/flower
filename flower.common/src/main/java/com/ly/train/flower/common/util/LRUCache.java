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

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * 
 * @author leeyazhou
 * 
 * @param <K> key
 * @param <V> value
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
  private static final long serialVersionUID = 1L;
  protected int maxElements;

  public LRUCache(int maxSize) {
    super(maxSize, 0.75F, true);
    this.maxElements = maxSize;
  }

  @Override
  protected boolean removeEldestEntry(Entry<K, V> eldest) {
    return (size() > this.maxElements);
  }
}
