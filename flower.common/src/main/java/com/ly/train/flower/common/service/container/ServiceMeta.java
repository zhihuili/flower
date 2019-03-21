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
package com.ly.train.flower.common.service.container;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author leeyazhou
 *
 */
public class ServiceMeta implements Serializable {
  private static final long serialVersionUID = 1L;
  private String serviceName;
  private Class<?> paramType;
  private Class<?> resultType;
  private Class<?> serviceClass;
  private List<String> configs = new ArrayList<>();

  public ServiceMeta(Class<?> serviceClass) {
    this.serviceClass = serviceClass;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  /**
   * 请求参数类型
   * 
   * @return
   */
  public Class<?> getParamType() {
    return paramType;
  }

  public void setParamType(Class<?> paramType) {
    this.paramType = paramType;
  }

  /**
   * 返回结果类型
   * 
   * @return
   */
  public Class<?> getResultType() {
    return resultType;
  }

  public void setResultType(Class<?> resultType) {
    this.resultType = resultType;
  }

  public Class<?> getServiceClass() {
    return serviceClass;
  }

  public void setServiceClass(Class<?> serviceClass) {
    this.serviceClass = serviceClass;
  }

  public List<String> getConfigs() {
    return configs;
  }

  public void setConfigs(List<String> configs) {
    this.configs = configs;
  }

  public boolean addConfig(String config) {
    configs.add(config);
    return true;
  }

  public String getConfig(int index) {
    if (configs == null || configs.size() <= index) {
      return null;
    }
    return configs.get(index);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ServiceMeta [serviceName=");
    builder.append(serviceName);
    builder.append(", paramType=");
    builder.append(paramType);
    builder.append(", resultType=");
    builder.append(resultType);
    builder.append(", serviceClass=");
    builder.append(serviceClass);
    builder.append("]");
    return builder.toString();
  }



}
