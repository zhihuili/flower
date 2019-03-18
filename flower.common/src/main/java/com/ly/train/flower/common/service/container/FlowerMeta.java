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
package com.ly.train.flower.common.service.container;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 
 * @author leeyazhou
 *
 */
public class FlowerMeta implements Serializable {

  private static final long serialVersionUID = 7142991778425415404L;

  public FlowerMeta() {}


  private Method method;
  private Class<?> resultType;
  private List<Class<?>> paramType;

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public Class<?> getResultType() {
    return resultType;
  }

  public void setResultType(Class<?> resultType) {
    this.resultType = resultType;
  }

  public List<Class<?>> getParamType() {
    return paramType;
  }

  public void setParamType(List<Class<?>> paramType) {
    this.paramType = paramType;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FlowerMeta [method=");
    builder.append(method);
    builder.append(", resultType=");
    builder.append(resultType);
    builder.append(", paramType=");
    builder.append(paramType);
    builder.append("]");
    return builder.toString();
  }



}
