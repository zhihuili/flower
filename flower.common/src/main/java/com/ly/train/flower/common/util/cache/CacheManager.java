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

import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.util.concurrent.NamedThreadFactory;

public class CacheManager {
  private static final Logger log = LoggerFactory.getLogger(CacheManager.class);
  private static final String defaultCacheManager = "DEFAULT_CACHE_MANAGER";
  private static final ConcurrentMap<String, CacheManager> cacheManagerMap = new ConcurrentHashMap<>();
  private static final AtomicBoolean init = new AtomicBoolean();
  private static ScheduledExecutorService executorService = null;

  private final ConcurrentMap<String, Cache<?>> cacheMap = new ConcurrentHashMap<String, Cache<?>>();

  private void clearCache() {
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

  public static CacheManager get() {
    return get(defaultCacheManager);
  }

  public static CacheManager get(String name) {
    CacheManager cacheManager = cacheManagerMap.get(name);
    if (cacheManager == null) {
      synchronized (cacheManagerMap) {
        cacheManager = cacheManagerMap.get(name);
        if (cacheManager == null) {
          cacheManager = new CacheManager();
          cacheManagerMap.putIfAbsent(name, cacheManager);
          init();
        }
      }
    }
    return cacheManager;
  }

  private static void init() {
    try {
      if (!init.compareAndSet(false, true)) {
        return;
      }
      executorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("CacheScanner"));
      executorService.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
          try {
            // log.info("CacheManager clear cache start");
            // clearCache();
            for (Entry<String, CacheManager> entry : cacheManagerMap.entrySet()) {
              entry.getValue().clearCache();
            }
          } catch (Exception ex) {
            log.error("clearCache error ", ex);
          } finally {
            // log.info("CacheManager clear cache finish");
          }
        }
      }, 4, 2 << 4, TimeUnit.SECONDS);
    } catch (Exception e) {
      log.error("CacheManager init timer error", e);
    }


  }

  /**
   * returns cache item from hashmap
   * 
   * @param key
   * @return Cache
   */
  @SuppressWarnings("unchecked")
  private <T> Cache<T> getCacheWithoutValidate(String key) {
    return (Cache<T>) cacheMap.get(key);
  }

  /**
   * Looks at the hashmap if a cache item exists or not
   * 
   * @param key
   * @return Cache
   */
  private boolean hasCache(String key) {
    return cacheMap.containsKey(key);
  }

  /**
   * Invalidates all cache
   */
  public void invalidateAll() {
    cacheMap.clear();
  }

  /**
   * Invalidates a single cache item
   * 
   * @param key key
   */
  public void invalidate(String key) {
    cacheMap.remove(key);
  }

  /**
   * Adds new item to cache hashmap
   * 
   * @param key
   * @return Cache
   */
  private <T> void putCache(String key, Cache<T> object) {
    cacheMap.put(key, object);
  }

  @SuppressWarnings("unchecked")
  private <T> Cache<T> add(String key, Cache<T> object) {
    return (Cache<T>) cacheMap.putIfAbsent(key, object);
  }

  /**
   * Reads a cache item's content
   * 
   * @param key
   * @return {@code Cache}
   */
  public <T> Cache<T> getCache(String key) {
    if (hasCache(key)) {
      Cache<T> cache = getCacheWithoutValidate(key);
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
   * @param ttl ms 有效时间
   * @return
   */
  public <T> Cache<T> add(String key, T content, long ttl) {
    Cache<T> cache = new Cache<>();
    cache.setKey(key);
    cache.setValue(content);
    cache.setTimeToLive(ttl);
    cache.setExpired(false);
    return (Cache<T>) add(key, cache);
  }

  public <T> void set(String key, T content, long ttl) {
    Cache<T> cache = new Cache<>();
    cache.setKey(key);
    cache.setValue(content);
    cache.setTimeToLive(ttl);
    cache.setExpired(false);
    putCache(key, cache);
  }

  public Set<String> getAllKey() {
    return cacheMap.keySet();
  }


  private <T> boolean cacheExpired(Cache<T> cache) {
    if (cache == null) {
      return false;
    }
    long milisNow = new Date().getTime();
    long milisExpire = cache.getTimeToLive();
    if (milisExpire <= 0) {
      return false;
    } else if (milisNow >= milisExpire) {
      return true;
    }
    return false;
  }

}
