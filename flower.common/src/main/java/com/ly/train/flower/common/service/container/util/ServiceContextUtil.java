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
package com.ly.train.flower.common.service.container.util;

import com.ly.train.flower.common.akka.actor.ServiceActor;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.web.Web;
import com.ly.train.flower.common.util.cache.Cache;
import com.ly.train.flower.common.util.cache.CacheManager;

/**
 * @author leeyazhou
 * 
 */
public class ServiceContextUtil {

  private ServiceContextUtil() {}

  private static final CacheManager cacheManager = CacheManager.get("FLOWER_SERVICE_CONTEXT_HOLDER");

  public static void record(ServiceContext serviceContext) {
    cacheManager.add(serviceContext.getId(), serviceContext.getWeb(), ServiceActor.defaultTimeToLive);
  }

  public static void fillServiceContext(ServiceContext serviceContext) {
    Cache<Web> cache = cacheManager.getCache(serviceContext.getId());
    if (cache != null) {
      serviceContext.setWeb(cache.getValue());
    }
  }

  public static void cleanServiceContext(ServiceContext serviceContext) {
    serviceContext.setWeb(null);
  }

}
