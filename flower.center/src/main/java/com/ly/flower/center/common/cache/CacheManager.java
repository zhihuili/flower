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
package com.ly.flower.center.common.cache;

import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.util.concurrent.NamedThreadFactory;

public class CacheManager {
  private static ScheduledExecutorService executorService =
      Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("CacheScanner"));
  private static final Logger log = LoggerFactory.getLogger(CacheManager.class);
  private static ConcurrentMap<String, Cache<?>> cacheMap = new ConcurrentHashMap<String, Cache<?>>();
  static {
    try {
      executorService.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
          try {
            log.info("CacheManager clear cache start");
            clearCache();
          } catch (Exception ex) {
            log.error("clearCache error ", ex);
          } finally {
            log.info("CacheManager clear cache finish");
          }
        }
      }, 4, 2 << 4, TimeUnit.SECONDS);
    } catch (Exception e) {
      log.error("CacheManager init timer error", e);
    }
  }

  private static void clearCache() {
    Iterator<Entry<String, Cache<?>>> it = cacheMap.entrySet().iterator();
    while (it.hasNext()) {
      Entry<String, Cache<?>> cache = it.next();
      if (cache.getValue().isExpired() || cacheExpired(cache.getValue())) {
        it.remove();
      }
    }
  }

  /**
   * This class is singleton so private constructor is used.
   */
  private CacheManager() {}

  /**
   * returns cache item from hashmap
   * 
   * @param key
   * @return Cache
   */
  @SuppressWarnings("unchecked")
  private synchronized static <T> Cache<T> getCache(String key) {
    return (Cache<T>) cacheMap.get(key);
  }

  /**
   * Looks at the hashmap if a cache item exists or not
   * 
   * @param key
   * @return Cache
   */
  private synchronized static boolean hasCache(String key) {
    return cacheMap.containsKey(key);
  }

  /**
   * Invalidates all cache
   */
  public synchronized static void invalidateAll() {
    cacheMap.clear();
  }

  /**
   * Invalidates a single cache item
   * 
   * @param key
   */
  public synchronized static void invalidate(String key) {
    cacheMap.remove(key);
  }

  /**
   * Adds new item to cache hashmap
   * 
   * @param key
   * @return Cache
   */
  private synchronized static <T> void putCache(String key, Cache<T> object) {
    cacheMap.put(key, object);
  }

  /**
   * Reads a cache item's content
   * 
   * @param key
   * @return
   */
  public static <T> Cache<T> getContent(String key) {
    if (hasCache(key)) {
      Cache<T> cache = getCache(key);
      if (cacheExpired(cache)) {
        cache.setExpired(true);
        return null;
      }
      return cache;
    } else {
      return null;
    }
  }

  /**
   * 
   * @param key
   * @param content
   * @param ttl ms
   */
  public static <T> void putContent(String key, T content, long ttl) {
    Cache<T> cache = new Cache<>();
    cache.setKey(key);
    cache.setValue(content);
    cache.setTimeToLive(ttl);
    cache.setExpired(false);
    putCache(key, cache);
  }

  public static Set<String> getAllKey() {
    return cacheMap.keySet();
  }


  private static <T> boolean cacheExpired(Cache<T> cache) {
    if (cache == null) {
      return false;
    }
    long milisNow = new Date().getTime();
    long milisExpire = cache.getTimeToLive();
    if (milisExpire < 0) {
      return false;
    } else if (milisNow >= milisExpire) {
      return true;
    }
    return false;
  }

}
