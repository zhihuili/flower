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
package com.ly.train.flower.center.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ly.train.flower.center.core.store.ServiceConfigStore;
import com.ly.train.flower.center.core.store.ServiceInfoStore;
import com.ly.train.flower.center.redis.RedisClient;
import com.ly.train.flower.center.redis.RedisManager;
import com.ly.train.flower.center.store.redis.ServiceConfigRedisStore;
import com.ly.train.flower.center.store.redis.ServiceInfoRedisStore;

/**
 * @author leeyazhou
 */
@Configuration
public class StoreConfig {

  @Bean
  public ServiceConfigStore serviceConfigStore() {
    ServiceConfigRedisStore serviceConfigStore = new ServiceConfigRedisStore();
    serviceConfigStore.setRedisClient(redisClient);
    return serviceConfigStore;
  }

  @Bean
  public ServiceInfoStore serviceInfoStore() {
    ServiceInfoRedisStore serviceConfigStore = new ServiceInfoRedisStore();
    serviceConfigStore.setRedisClient(redisClient);
    return serviceConfigStore;
  }

  @Value("${flower.center.redis.host}")
  private String host;
  @Value("${flower.center.redis.port}")
  private int port;
  @Value("${flower.center.redis.password}")
  private String password;

  @Autowired
  private RedisClient redisClient;

  @Bean
  public RedisClient redisClient() {
    RedisManager manager = new RedisManager();
    manager.setHost(host);
    manager.setPort(port);
    manager.setPassword(password);
    return manager.getRedisClient();
  }
}
