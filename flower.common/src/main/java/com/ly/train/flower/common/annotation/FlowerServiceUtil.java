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
package com.ly.train.flower.common.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.util.StringUtil;

/**
 * @author leeyazhou
 * 
 */
public class FlowerServiceUtil {
  public static String getServiceName(Class<?> clazz) {
    if (clazz == null) {
      return null;
    }
    String serviceName = clazz.getSimpleName();
    FlowerService flowerService = clazz.getAnnotation(FlowerService.class);
    if (flowerService != null) {
      if (StringUtil.isNotBlank(flowerService.value())) {
        serviceName = flowerService.value();
      }
    }
    return serviceName;
  }

  public static Method getProcessMethod(Class<?> clazz) {
    if (clazz == null) {
      return null;
    }
    Method[] methods = clazz.getMethods();
    if (methods == null) {
      return null;
    }
    for (int i = methods.length; i > 0; i--) {
      Method method = methods[i - 1];
      if ("process".equals(method.getName()) && method.getParameterCount() == 2) {
        Parameter pa = method.getParameters()[1];
        if (pa.getType().equals(ServiceContext.class)) {
          return method;
        }
      }
    }
    return null;
  }

  /**
   * 注意： 不是内部聚合类,是指服务的类型标记为聚合<br/>
   * 服务是否是聚合类型<br/>
   * 
   * @param clazz clazz
   * @return {@link FlowerType}
   */
  public static FlowerType getFlowerType(Class<?> clazz) {
    FlowerService flowerService = null;
    if (clazz == null || (flowerService = clazz.getAnnotation(FlowerService.class)) == null) {
      return null;
    }
    return flowerService.type();
  }


  public static int getTimeout(Class<?> clazz) {
    FlowerService flowerService = null;
    if (clazz == null || (flowerService = clazz.getAnnotation(FlowerService.class)) == null) {
      return 10000;
    }
    int timeout = flowerService.timeout();
    return timeout;
  }
}
