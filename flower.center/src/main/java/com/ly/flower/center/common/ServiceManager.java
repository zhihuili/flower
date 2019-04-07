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
package com.ly.flower.center.common;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ly.flower.center.common.cache.Cache;
import com.ly.flower.center.common.cache.CacheManager;
import com.ly.train.flower.registry.config.ServiceInfo;

/**
 * @author leeyazhou
 *
 */
@Service
public class ServiceManager {
  private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);

  private Timer timer = new Timer("flower-service-scanner");

  /**
   * 
   */
  public ServiceManager() {
    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        Set<String> keys = CacheManager.getAllKey();
        logger.info(
            "---------------------scan service list start--------------------------------------------------------------");
        for (String key : keys) {
          Cache<ServiceInfo> cache = CacheManager.getContent(key);
          logger.info("缓存中的数据 {} : {}", key, cache);
        }
        logger.info(
            "----------------------scan service list end---------------------------------------------------------------");
      }
    }, 3000, TimeUnit.SECONDS.toMillis(4));
  }


  public boolean addServiceInfo(ServiceInfo serviceInfo) {
    Cache<ServiceInfo> cache = CacheManager.getContent(serviceInfo.getClassName());
    if (cache == null) {
      CacheManager.putContent(serviceInfo.getClassName(), serviceInfo, 6000);
      cache = CacheManager.getContent(serviceInfo.getClassName());
    } else {
      cache.getValue().getAddresses().addAll(serviceInfo.getAddresses());
    }
    return true;
  }

  public Set<ServiceInfo> getAll() {

    Set<ServiceInfo> ret = new HashSet<ServiceInfo>();
    Set<String> keys = CacheManager.getAllKey();
    for (String key : keys) {
      Cache<ServiceInfo> cache = CacheManager.getContent(key);
      if (cache != null) {
        ret.add(cache.getValue());
      }
    }
    return ret;
  }
}
