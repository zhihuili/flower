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
package com.ly.train.flower.center.core.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.ly.train.flower.center.core.store.FlowConfigStore;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.core.config.FlowConfig;
import com.ly.train.flower.common.core.service.Service;
import com.ly.train.flower.common.core.service.ServiceContext;

/**
 * @author leeyazhou
 * 
 */
@FlowerService(timeout = 1000)
public class FlowConfigListService implements Service<FlowConfig, FlowConfig> {

  @Autowired
  protected FlowConfigStore flowConfigStore;

  @Override
  public FlowConfig process(FlowConfig message, ServiceContext context) throws Throwable {
    FlowConfig result = null;
    if (message != null) {
      result = flowConfigStore.getFlowConfig(message);
    }
    if (result == null) {
      return new FlowConfig();
    }
    return result;
  }

}
