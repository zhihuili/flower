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
package com.ly.train.flower.center.common;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.util.cache.Cache;
import com.ly.train.flower.common.util.cache.CacheManager;
import com.ly.train.flower.registry.config.ServiceInfo;

/**
 * @author leeyazhou
 * 
 */
@Service
public class ServiceManager {
  static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);

  protected Timer timer = new Timer("flower-service-scanner");
  private CacheManager cacheManager = CacheManager.get("flower_center");

  public ServiceManager() {
  }


  public boolean addServiceInfo(ServiceInfo serviceInfo) {
    Cache<Set<ServiceInfo>> cache = cacheManager.getCache(serviceInfo.getClassName());
    if (cache == null) {
      Set<ServiceInfo> c = new HashSet<ServiceInfo>();
      c.add(serviceInfo);
      cacheManager.add(serviceInfo.getClassName(), c, 6000L);
      cache = cacheManager.getCache(serviceInfo.getClassName());
    } else {
      cache.getValue().add(serviceInfo);
      cache.setTimeToLive(6000);
    }
    return true;
  }

  public Set<ServiceInfo> getAllServiceInfo() {
    Set<ServiceInfo> ret = new HashSet<ServiceInfo>();
    Set<String> keys = cacheManager.getAllKey();
    for (String key : keys) {
      Cache<Object> cache = cacheManager.getCache(key);
      if (cache != null && cache.getValue() instanceof Set) {
        ret.addAll((Set<ServiceInfo>) cache.getValue());
      }
    }
    return ret;
  }

  public boolean addServiceConfig(ServiceConfig serviceConfig) {
    cacheManager.add(serviceConfig.getFlowName(), serviceConfig, 6000);
    return true;
  }

  public Set<ServiceConfig> getAllServiceConfig() {
    Set<ServiceConfig> ret = new HashSet<ServiceConfig>();
    Set<String> keys = cacheManager.getAllKey();
    for (String key : keys) {
      Cache<Object> cache = cacheManager.getCache(key);
      if (cache != null && cache.getValue() instanceof ServiceConfig) {
        ret.add((ServiceConfig) cache.getValue());
      }
    }
    return ret;
  }
}
