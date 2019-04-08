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

import com.ly.train.flower.common.util.StringUtil;

/**
 * @author leeyazhou
 *
 */
public class FlowerServiceUtil {

  public static String getServiceName(Class<?> clazz) {
    FlowerService flowerService = null;
    if (clazz == null || (flowerService = clazz.getAnnotation(FlowerService.class)) == null) {
      return null;
    }
    String serviceName = flowerService.value();
    if (StringUtil.isBlank(serviceName)) {
      serviceName = clazz.getSimpleName();
    }
    return serviceName;
  }

  /**
   * 注意： 不是内部聚合类,是指服务的类型标记为聚合<br/>
   * 服务是否是聚合类型<br/>
   * 
   * @param clazz clazz
   * @return true/false
   */
  public static boolean isAggregateType(Class<?> clazz) {
    FlowerService flowerService = null;
    if (clazz == null || (flowerService = clazz.getAnnotation(FlowerService.class)) == null) {
      return false;
    }
    return FlowerType.AGGREGATE.equals(flowerService.type());
  }

  public static int getTimeout(Class<?> clazz) {
    FlowerService flowerService = null;
    if (clazz == null || (flowerService = clazz.getAnnotation(FlowerService.class)) == null) {
      return 3000;
    }
    int timeout = flowerService.timeout();
    return timeout;
  }
}
