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
package com.ly.train.flower.registry.redis;

import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.registry.AbstractRegistry;
import com.ly.train.flower.registry.config.ServiceInfo;
import com.ly.train.flower.registry.redis.util.RedisClient;
import com.ly.train.flower.registry.redis.util.RedisManager;

/**
 * @author leeyazhou
 * 
 */
public class RedisRegistry extends AbstractRegistry {

  private final String providerKeyFormatter = root + ":%s:providers:%s";
  private final String consumerKeyFormatter = root + ":%s:consumers:%s";
  private RedisClient redisClient;

  public RedisRegistry(URL url) {
    super(url);
    this.redisClient = new RedisManager(url).getRedisClient();
  }

  @Override
  public boolean doRegister(ServiceInfo serviceInfo) {
    URL u = serviceInfo.getAddresses().iterator().next();
    String key = String.format(providerKeyFormatter, serviceInfo.getClassName(), u.getHost() + ":" + u.getPort());
    redisClient.setex(key, 10, JSONObject.toJSONString(serviceInfo));
    return true;
  }

  @Override
  public List<ServiceInfo> doGetProvider(ServiceInfo serviceInfo) {
    return null;
  }

  @Override
  public boolean doRegisterServiceConfig(ServiceConfig serviceConfig) {
    return false;
  }

  @Override
  public List<ServiceConfig> doGetServiceConfig(ServiceConfig serviceConfig) {
    return null;
  }


}
