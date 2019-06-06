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
package com.ly.train.flower.registry;

import java.util.List;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.registry.config.ServiceInfo;

/**
 * @author leeyazhou
 * 
 */
public interface Registry {

  /**
   * 注册服务
   * 
   * @param serviceInfo {@link ServiceInfo}
   * @return true / false
   */
  boolean register(ServiceInfo serviceInfo);

  boolean registerServiceConfig(ServiceConfig serviceConfig);

  /**
   * 查询服务提供者
   * 
   * @param serviceInfo {@link ServiceInfo}
   * @return {@link ServiceInfo}
   */
  List<ServiceInfo> getProvider(ServiceInfo serviceInfo);

  List<ServiceConfig> getServiceConfig(ServiceConfig serviceConfig);

  URL getUrl();
}
