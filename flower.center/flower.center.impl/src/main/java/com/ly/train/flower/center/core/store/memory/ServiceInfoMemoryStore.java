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
package com.ly.train.flower.center.core.store.memory;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.center.core.store.ServiceInfoStore;
import com.ly.train.flower.common.util.cache.Cache;
import com.ly.train.flower.common.util.cache.CacheManager;
import com.ly.train.flower.registry.config.ServiceInfo;

/**
 * @author leeyazhou
 */
public class ServiceInfoMemoryStore implements ServiceInfoStore {
  static final Logger logger = LoggerFactory.getLogger(ServiceInfoMemoryStore.class);

  private CacheManager cacheManager = CacheManager.get("flower_center_info");


  public boolean addServiceInfo(ServiceInfo serviceInfo) {
    final String cacheKey = serviceInfo.getApplication() + "_" + serviceInfo.getServiceName();
    Cache<Set<ServiceInfo>> cache = cacheManager.getCache(cacheKey);
    if (cache == null) {
      Set<ServiceInfo> c = new HashSet<ServiceInfo>();
      c.add(serviceInfo);
      cacheManager.add(cacheKey, c, 6000L);
      cache = cacheManager.getCache(cacheKey);
    } else {
      cache.getValue().add(serviceInfo);
      cache.setTimeToLive(6000);
    }
    return true;
  }

  @Override
  public Set<ServiceInfo> getServiceInfo(ServiceInfo serviceInfo) {
    final String cacheKey = serviceInfo.getApplication() + "_" + serviceInfo.getServiceName();
    Cache<Object> cache = cacheManager.getCache(cacheKey);
    if (cache == null) {
      return null;
    }
    return (Set<ServiceInfo>) cache.getValue();
  }
}
