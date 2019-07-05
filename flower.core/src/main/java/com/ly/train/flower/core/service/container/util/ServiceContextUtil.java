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
package com.ly.train.flower.core.service.container.util;

import com.ly.train.flower.common.core.message.FlowMessage;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.core.web.Web;
import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.common.util.cache.Cache;
import com.ly.train.flower.common.util.cache.CacheManager;
import com.ly.train.flower.core.akka.actor.ServiceActor;
import com.ly.train.flower.serializer.Serializer;

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


  public static <T> ServiceContext context(T message, Web web) {
    ServiceContext context = new ServiceContext();
    FlowMessage flowMessage = new FlowMessage();
    if (message != null) {
      Serializer codec = ExtensionLoader.load(Serializer.class).load();
      flowMessage.setMessageType(message.getClass().getName());
      flowMessage.setMessage(codec.encode(message));
    }
    context.setFlowMessage(flowMessage);
    context.setWeb(web);
    return context;
  }

  public static <T> ServiceContext context(T message) {
    return context(message, null);
  }

}
