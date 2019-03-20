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
package com.ly.train.flower.common.service.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.service.FlowerService;

public class ServiceLoader {
  private static final Logger logger = LoggerFactory.getLogger(ServiceLoader.class);
  private ClassLoader cl;
  private Map<String, Class<?>> messageTypeMap = new ConcurrentHashMap<String, Class<?>>();

  private static ServiceLoader sl = new ServiceLoader();

  private ServiceLoader() {
    this.cl = this.getClass().getClassLoader();
  }

  public static ServiceLoader getInstance() {
    return sl;
  }

  public FlowerService loadService(String serviceName) {
    FlowerService service;
    try {
      Class<?> serviceClass = cl.loadClass(ServiceFactory.getServiceClassName(serviceName));
      String param = ServiceFactory.getServiceClassParameter(serviceName);
      if (param != null) {
        Constructor<?> constructor = serviceClass.getConstructor(String.class);
        service = (FlowerService) constructor.newInstance(param);
        return service;
      }
      service = (FlowerService) serviceClass.newInstance();
      return service;
    } catch (Exception e) {
      logger.error("fail load service : " + serviceName, e);
      System.exit(0);
    }
    return null;
  }

  public Class<?> getServiceMessageType(String serviceName) {
    Class<?> messageType = messageTypeMap.get(serviceName);
    if (messageType == null) {
      synchronized (this) {
        messageType = messageTypeMap.get(serviceName);
        if (messageType == null) {
          try {
            Class<?> serviceClass = cl.loadClass(ServiceFactory.getServiceClassName(serviceName));

            messageType =
                (Class<?>) ((ParameterizedType) serviceClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
            messageTypeMap.put(serviceName, messageType);
          } catch (Exception e) {
            messageType = Object.class;
          }
        }
      }
    }
    return messageType;
  }
}
