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
package com.ly.train.flower.center.core.store.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.center.core.store.FlowConfigStore;
import com.ly.train.flower.common.core.config.FlowConfig;
import com.ly.train.flower.common.util.cache.Cache;
import com.ly.train.flower.common.util.cache.CacheManager;

/**
 * @author leeyazhou
 */
public class FlowConfigMemoryStore implements FlowConfigStore {


  static final Logger logger = LoggerFactory.getLogger(FlowConfigMemoryStore.class);

  private CacheManager cacheManager = CacheManager.get("flower_center_flow_config");



  @Override
  public boolean addFlowConfig(FlowConfig flowConfig) {
    String cacheKey = flowConfig.getApplication() + "_" + flowConfig.getFlowName();
    Cache<FlowConfig> cache = cacheManager.getCache(cacheKey);
    if (cache == null) {
      cacheManager.add(cacheKey, flowConfig, 6000L);
    } else {
      cache.setTimeToLive(6000L);
      cache.setValue(flowConfig);
    }
    return true;
  }

  @Override
  public FlowConfig getFlowConfig(FlowConfig flowConfig) {
    String cacheKey = flowConfig.getApplication() + "_" + flowConfig.getFlowName();
    Cache<Object> cache = cacheManager.getCache(cacheKey);
    if (cache != null && cache.getValue() instanceof FlowConfig) {
      return (FlowConfig) cache.getValue();
    }
    return null;
  }


}
