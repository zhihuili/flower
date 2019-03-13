/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ly.train.flower.common.service.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.service.FlowerService;

public class ServiceFactory {
  private static final Logger logger = LoggerFactory.getLogger(ServiceFactory.class);
  private static Map<String, String> serviceMap = new ConcurrentHashMap<String, String>();
  private static Map<String, FlowerService> flowerServiceMap =
      new ConcurrentHashMap<String, FlowerService>();

  public static void registerService(String serviceName, String serviceClass) {
    String ret = serviceMap.put(serviceName, serviceClass);
    if (ret != null) {
      logger.warn("service is alread exist. serviceName : {}, serviceClass : {}", serviceName,
          serviceClass);
    }
  }

  public static void registerService(Map<String, String> map) {
    for (Map.Entry<String, String> entry : map.entrySet()) {
      registerService(entry.getKey().trim(), entry.getValue().trim());
    }

  }

  public static void registerFlowerService(String serviceName, FlowerService flowerService) {
    FlowerService ret = flowerServiceMap.put(serviceName, flowerService);
    if (ret != null) {
      logger.warn("flower service is alread exist. serviceName : {}, flowerService : {}", serviceName,
          flowerService);
    }
  }

  public static FlowerService getService(String serviceName) {
    FlowerService fs = flowerServiceMap.get(serviceName);
    if (fs != null) {
      return fs;
    }
    return ServiceLoader.getInstance().loadService(serviceName);
  }

  public static String getServiceClassName(String serviceName) {
    return getServiceConf(serviceName, 0);
  }

  public static String getServiceClassParameter(String serviceName) {
    return getServiceConf(serviceName, 1);
  }

  private static String getServiceConf(String serviceName, int index) {
    String serviceConfig = serviceMap.get(serviceName);
    if (serviceConfig != null && serviceConfig.length() > 0) {
      String[] conf = serviceConfig.split(";");
      if (conf.length > index) {
        return conf[index].trim();
      }
    }
    return null;
  }

}
