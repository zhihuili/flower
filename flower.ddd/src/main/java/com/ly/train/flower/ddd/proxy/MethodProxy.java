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
package com.ly.train.flower.ddd.proxy;

import java.lang.reflect.Method;
import org.springframework.transaction.annotation.Transactional;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.ddd.exception.CommandInvokeException;

/**
 * @author leeyazhou
 */
public class MethodProxy {
  private Method method;
  private Object targetInstance;
  private boolean transactional;

  public MethodProxy(Object targetInstance, Method method) {
    this.targetInstance = targetInstance;
    this.method = method;
    this.transactional = method.getAnnotation(Transactional.class) != null;
  }

  public Object invoke(Object param, ServiceContext context) {
    try {
      Object[] args = new Object[method.getParameterCount()];
      args[0] = param;
      for (int i = 1; i < method.getParameterCount(); i++) {
        if (method.getParameterTypes()[i].equals(ServiceContext.class)) {
          args[i] = context;
        }
      }
      return method.invoke(targetInstance, args);
    } catch (Exception e) {
      throw new CommandInvokeException("method : " + method + ", targetInstance : " + targetInstance, e);
    }
  }

  public boolean isTransactional() {
    return transactional;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((method == null) ? 0 : method.hashCode());
    result = prime * result + ((targetInstance == null) ? 0 : targetInstance.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    MethodProxy other = (MethodProxy) obj;
    if (method == null) {
      if (other.method != null) {
        return false;
      }
    } else if (!method.equals(other.method)) {
      return false;
    }
    if (targetInstance == null) {
      if (other.targetInstance != null) {
        return false;
      }
    } else if (!targetInstance.equals(other.targetInstance)) {
      return false;
    }
    return true;
  }


}
