package com.ly.train.flower.common.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * 
 * @author leeyazhou
 *
 * @param <K>
 * @param <V>
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
