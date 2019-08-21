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
package com.ly.train.flower.core.filter.impl;

import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.filter.AbstractFilter;
import com.ly.train.flower.filter.FilterChain;

/**
 * @author leeyazhou
 */
public class MockTwoFilter extends AbstractFilter {
  private static final Logger logger = LoggerFactory.getLogger(MockTwoFilter.class);

  @Override
  public Object doFilter(Object message, ServiceContext context, FilterChain filterChain) {
    logger.info("Before 过滤：" + message);
    try {
      return filterChain.doFilter(message, context);
    } finally {
      logger.info("After 过滤" + message);
    }
  }

  @Override
  public int getOrder() {
    return 2;
  }
}
