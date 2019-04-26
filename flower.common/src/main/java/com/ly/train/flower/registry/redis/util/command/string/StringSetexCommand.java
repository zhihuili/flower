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
package com.ly.train.flower.registry.redis.util.command.string;

import com.ly.train.flower.registry.redis.util.command.RedisCommand;
import redis.clients.jedis.Jedis;

/**
 * @author leeyazhou
 * 
 */
public class StringSetexCommand implements RedisCommand<String> {
  private String key;
  private String value;
  private int seconds;

  /**
   * @param key
   * @param value
   * @param seconds
   */
  public StringSetexCommand(String key, String value, int seconds) {
    this.key = key;
    this.value = value;
    this.seconds = seconds;
  }

  @Override
  public String doExecute(Jedis jedis) {
    return jedis.setex(key, seconds, value);
  }

}
