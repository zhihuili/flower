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
package com.ly.train.flower.ddd.service;

import java.util.Set;
import java.util.stream.Collectors;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.ddd.config.DDDConfig;
import com.ly.train.flower.ddd.exception.QueryHandlerNotFoundException;
import com.ly.train.flower.ddd.proxy.MethodProxy;

/**
 * @author leeyazhou
 */
public class QueryHandlerService extends BaseService {
  static final Logger logger = LoggerFactory.getLogger(QueryHandlerService.class);
  private DDDConfig dddConfig;

  public QueryHandlerService(DDDConfig dddConfig) {
    this.dddConfig = dddConfig;
  }

  @Override
  public Object doProcess(Object message, ServiceContext context) throws Throwable {
    startLifecycle();
    try {
      Set<MethodProxy> methodProxies = dddConfig.getQueryHandler(message.getClass());
      if (methodProxies == null || methodProxies.isEmpty()) {
        throw new QueryHandlerNotFoundException("for " + message.getClass());
      }
      for (MethodProxy method : methodProxies) {
        method.invoke(message, context);
      }
      return getAppliedMessages().stream().filter(em -> em.getMessage() != null).collect(Collectors.toList());
    } finally {
      endLifecycle();
    }
  }

}
