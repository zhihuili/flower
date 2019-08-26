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
package com.ly.train.flower.filter.impl;

import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.filter.AbstractFilter;
import com.ly.train.flower.filter.FilterChain;

/**
 * @author leeyazhou
 * 
 */
public class AccessLogFilter extends AbstractFilter {
  private static final Logger logger = LoggerFactory.getLogger(AccessLogFilter.class);

  @Override
  public Object doFilter(Object message, ServiceContext context, FilterChain chain) {
    long start = System.currentTimeMillis();
    Object result = null;
    try {
      result = chain.doFilter(message, context);
      return result;
    } finally {
      logger.info("flowName : {}, serviceName : {}, invoke time(ms):{}, message : {}, result : {}",
          context.getFlowName(), (System.currentTimeMillis() - start), context.getCurrentServiceName(), message,
          result);
    }
  }

}
