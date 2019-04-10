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
package com.ly.train.flower.registry.simple.service;

import java.util.Set;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.ServiceContext;

/**
 * @author leeyazhou
 *
 */
public class ServiceConfigListService implements Service<ServiceConfig, Set<ServiceConfig>> {

  @Override
  public Set<ServiceConfig> process(ServiceConfig message, ServiceContext context) throws Throwable {
    return null;
  }

}
