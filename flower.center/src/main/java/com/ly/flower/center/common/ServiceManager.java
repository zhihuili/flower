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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.ehcache.Cache;
import org.ehcache.Cache.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.registry.ServiceInfo;

/**
 * @author leeyazhou
 *
 */
@Component
public class ServiceManager {
  private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);
  @Autowired
  protected Cache<String, String> cache;

  private Timer timer = new Timer("flower-service-scanner");

  /**
   * 
   */
  public ServiceManager() {
    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        Iterator<Entry<String, String>> it = cache.iterator();
        logger.info("---------------------scan service list start--------------------------------------------------------------");
        while (it.hasNext()) {
          Entry<String, String> entry = it.next();
          logger.info("缓存中的数据 {} : {}", entry.getKey(), entry.getValue());
        }
        logger.info("----------------------scan service list end---------------------------------------------------------------");
      }
    }, 3000, TimeUnit.SECONDS.toMillis(4));
  }


  public boolean addServiceInfo(ServiceInfo serviceInfo) {
    String value = cache.get(serviceInfo.getClassName());
    List<ServiceInfo> v = new ArrayList<>();
    if (value != null) {
      v = JSONObject.parseArray(value, ServiceInfo.class);
    }
    v.add(serviceInfo);
    cache.remove(serviceInfo.getClassName());
    cache.put(serviceInfo.getClassName(), JSONObject.toJSONString(v));
    return true;
  }

  public Map<String, List<ServiceInfo>> getAll() {
    Map<String, List<ServiceInfo>> ret = new java.util.HashMap<>();
    Iterator<Entry<String, String>> it = cache.iterator();
    while (it.hasNext()) {
      Entry<String, String> entry = it.next();
      ret.put(entry.getKey(), JSONObject.parseArray(entry.getValue(), ServiceInfo.class));
    }
    return ret;
  }
}
