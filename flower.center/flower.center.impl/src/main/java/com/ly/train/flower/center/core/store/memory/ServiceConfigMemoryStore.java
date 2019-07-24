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
import com.ly.train.flower.center.core.store.ServiceConfigStore;
import com.ly.train.flower.common.core.config.ServiceConfig;
import com.ly.train.flower.common.util.cache.Cache;
import com.ly.train.flower.common.util.cache.CacheManager;
import com.ly.train.flower.registry.config.ServiceInfo;

/**
 * @author leeyazhou
 */
public class ServiceConfigMemoryStore implements ServiceConfigStore {


  static final Logger logger = LoggerFactory.getLogger(ServiceConfigMemoryStore.class);

  private CacheManager cacheManager = CacheManager.get("flower_center_config");



  public boolean addServiceConfig(ServiceConfig serviceConfig) {
    String cacheKey = serviceConfig.getApplication() + "_" + serviceConfig.getFlowName();
    Cache<ServiceConfig> cache = cacheManager.getCache(cacheKey);
    if (cache == null) {
      cacheManager.add(cacheKey, serviceConfig, 6000L);
    } else {
      cache.setTimeToLive(6000L);
      cache.setValue(serviceConfig);
    }
    return true;
  }

  @Override
  public ServiceConfig getServiceConfig(ServiceConfig serviceConfig) {
    String key = serviceConfig.getApplication() + "_" + serviceConfig.getFlowName();
    Cache<Object> cache = cacheManager.getCache(key);
    if (cache != null && cache.getValue() instanceof ServiceConfig) {
      return (ServiceConfig) cache.getValue();
    }
    return null;
  }


}
