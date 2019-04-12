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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.web.Web;

/**
 * @author leeyazhou
 *
 */
public class ServiceContextUtil {

  private ServiceContextUtil() {}

  private static final ConcurrentMap<String, Web> serviceContextCache = new ConcurrentHashMap<>();

  public static void record(ServiceContext serviceContext) {
    serviceContextCache.putIfAbsent(serviceContext.getId(), serviceContext.getWeb());
  }

  public static void fillServiceContext(ServiceContext serviceContext) {
    serviceContext.setWeb(serviceContextCache.get(serviceContext.getId()));
  }

  public static void cleanServiceContext(ServiceContext serviceContext) {
    serviceContext.setWeb(null);
  }

}
