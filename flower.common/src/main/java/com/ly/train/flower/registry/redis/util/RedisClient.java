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

import com.ly.train.flower.registry.redis.util.command.RedisCommand;
import com.ly.train.flower.registry.redis.util.command.string.StringSetexCommand;
import com.ly.train.flower.registry.redis.util.command.string.StringSetnxCommand;

/**
 * @author leeyazhou
 * 
 */
public class RedisClient {

  private RedisManager redisManager;

  public RedisClient(RedisManager redisManager) {
    this.redisManager = redisManager;
  }

  public Long setnx(String key, String value) {
    return redisManager.execute(new StringSetnxCommand(key, value));
  }

  public String setex(String key, int seconds, String value) {
    RedisCommand<String> command = new StringSetexCommand(key, value, seconds);
    return redisManager.execute(command);
  }
}
