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
import com.ly.train.flower.common.annotation.FlowerType;
import com.ly.train.flower.common.service.impl.AggregateService;

/**
 * 
 * @author leeyazhou
 * 
 */
public class ServiceMeta implements Serializable {
  private static final long serialVersionUID = 1L;
  private String serviceName;
  private String paramType;
  private String resultType;
  private String serviceClassName;
  private boolean local = true;
  private List<String> configs = new ArrayList<>();
  private boolean aggregateService;
  private boolean innerAggregateService;
  private int timeout;


  public ServiceMeta(String serviceClassName) {
    this.serviceClassName = serviceClassName;
  }

  public ServiceMeta() {}

  public String getServiceName() {
    return serviceName;
  }


  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getParamType() {
    return paramType;
  }

  public void setParamType(String paramType) {
    this.paramType = paramType;
  }

  /**
   * 返回结果类型
   * 
   * @return class
   */
  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  /**
   * true: local Service <br/>
   * false : remote Service
   * 
   * @return true / false
   */
  public boolean isLocal() {
    return local;
  }

  /**
   * set local
   * 
   * @param local true: local Service, false : remote Service
   */
  public void setLocal(boolean local) {
    this.local = local;
  }

  public String getServiceClassName() {
    return serviceClassName;
  }

  public void setServiceClassName(String serviceClassName) {
    this.serviceClassName = serviceClassName;
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

  /**
   * 指服务的类型是否是聚合类型
   * 
   * @return
   * @see FlowerType#AGGREGATE
   * @see FlowerType#COMMON return true/false
   */
  public boolean isAggregateService() {
    return aggregateService;
  }

  public void setAggregateService(boolean aggregateService) {
    this.aggregateService = aggregateService;
  }

  /**
   * 指服务是否是聚合类型{@link AggregateService}
   * 
   * @return true / false
   */
  public boolean isInnerAggregateService() {
    return innerAggregateService;
  }

  public void setInnerAggregateService(boolean innerAggregateService) {
    this.innerAggregateService = innerAggregateService;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
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
    builder.append(", serviceClassName=");
    builder.append(serviceClassName);
    builder.append(", local=");
    builder.append(local);
    builder.append(", configs=");
    builder.append(configs);
    builder.append("]");
    return builder.toString();
  }



}
