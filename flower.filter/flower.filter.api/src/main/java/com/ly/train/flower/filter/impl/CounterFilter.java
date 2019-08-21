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
package com.ly.train.flower.filter.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.filter.AbstractFilter;
import com.ly.train.flower.filter.FilterChain;

/**
 * @author leeyazhou
 * 
 */
public class CounterFilter extends AbstractFilter {
  private static final Logger logger = LoggerFactory.getLogger(CounterFilter.class);
  private final ConcurrentMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

  @Override
  public Object doFilter(Object message, ServiceContext context, FilterChain chain) {
    final String cacheKey = context.getFlowName() + "-" + context.getCurrentServiceName();
    AtomicInteger counter = counters.computeIfAbsent(cacheKey, (key) -> {
      return new AtomicInteger();
    });
    int cou = counter.incrementAndGet();
    if (cou % 100 == 0) {
      logger.info("count : {}, flow : {}, service : {}", cou, context.getFlowName(), context.getCurrentServiceName());
    }
    return chain.doFilter(message, context);
  }

}
