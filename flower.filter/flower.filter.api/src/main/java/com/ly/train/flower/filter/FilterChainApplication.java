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
package com.ly.train.flower.filter;

import java.util.Arrays;
import com.ly.train.flower.common.core.proxy.MethodProxy;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.exception.ServiceException;
import com.ly.train.flower.common.util.Assert;

/**
 * @author leeyazhou
 */
public class FilterChainApplication implements FilterChain {

  private MethodProxy methodProxy;
  private Filter[] filters;
  private int pos;

  public FilterChainApplication(Filter[] filters, MethodProxy methodProxy) {
    Assert.notNull(methodProxy, "service");
    this.methodProxy = methodProxy;
    this.filters = filters;
    if (filters == null) {
      this.filters = new Filter[0];
    } else {
      Arrays.sort(filters, (o1, o2) -> {
        if (o1.getOrder() > o2.getOrder()) {
          return 1;
        }
        return -1;
      });
    }
  }


  @Override
  public Object doFilter(Object message, ServiceContext context) {
    if (pos < filters.length) {
      return filters[pos++].doFilter(message, context, this);
    }
    try {
      return methodProxy.process(message, context);
    } catch (Throwable e) {
      throw new ServiceException("fail to invoke service : " + methodProxy + ", message : " + message, e);
    }

  }
}
