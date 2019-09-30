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
package com.ly.train.flower.common.core.proxy;

import java.lang.reflect.Method;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.exception.ServiceInvokeException;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;

/**
 * @author leeyazhou
 */
public class MethodProxy {
  private static final Logger logger = LoggerFactory.getLogger(MethodProxy.class);
  private Method method;
  private Object target;
  private boolean flush;
  private boolean complete;

  public MethodProxy(Object target, Method method) {
    this.target = target;
    this.method = method;
  }

  public Object process(Object args, ServiceContext context) {
    try {
      method.setAccessible(true);
      return method.invoke(target, args, context);
    } catch (Exception e) {
      logger.error("", e);
      throw new ServiceInvokeException("method name : " + method.getName(), e);
    }
  }

  public Object getFlowerService() {
    return target;
  }

  /**
   * @return the flush
   */
  public boolean isFlush() {
    return flush;
  }

  /**
   * @return the completed
   */
  public boolean isCompleted() {
    return complete;
  }

  /**
   * @param flush the flush to set
   */
  public void setFlush(boolean flush) {
    this.flush = flush;
  }

  /**
   * @param completed the completed to set
   */
  public void setComplete(boolean complete) {
    this.complete = complete;
  }


}
