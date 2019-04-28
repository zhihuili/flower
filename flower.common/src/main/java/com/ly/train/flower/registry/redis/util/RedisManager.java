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
package com.ly.train.flower.registry.redis.util;

import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import com.ly.train.flower.registry.redis.util.command.RedisCommand;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author leeyazhou
 * 
 */
public class RedisManager {
  private static final Logger logger = LoggerFactory.getLogger(RedisManager.class);
  private JedisPool jedisPool;

  private URL url;

  public RedisManager(URL url) {
    this.url = url;
  }

  public URL getUrl() {
    return url;
  }

  private JedisPool getJedisPool() {
    if (jedisPool == null) {
      JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
      jedisPoolConfig.setMaxTotal(100);// 最大连接数
      jedisPoolConfig.setMaxIdle(10);
      jedisPoolConfig.setMaxWaitMillis(3000);// 类似于超时时间
      jedisPoolConfig.setTestOnBorrow(true);
      jedisPool =
          new JedisPool(jedisPoolConfig, getUrl().getHost(), getUrl().getPort(), 3000, getUrl().getParam("password"));// 创建连接池
    }
    return jedisPool;
  }

  public RedisClient getRedisClient() {
    return new RedisClient(this);
  }

  public <T> T execute(RedisCommand<T> command) {
    Jedis jedis = getJedisPool().getResource();
    try {
      return command.doExecute(jedis);
    } catch (Exception e) {
      logger.error("", e);
      return null;
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
  }
}
