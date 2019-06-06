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
package com.ly.train.flower.common.service.config;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import com.ly.train.flower.common.service.container.ServiceMeta;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.common.util.URL;

/**
 * 流程服务节点配置
 * 
 * @author leeyazhou
 * 
 */
public class ServiceConfig implements Serializable {

  private static final long serialVersionUID = 1L;
  private String flowName;
  private String application;
  private String serviceName;
  private ServiceMeta serviceMeta;
  private Set<ServiceConfig> nextServiceConfigs;
  private AtomicInteger jointSourceNumber = new AtomicInteger(0);
  private int index;
  private boolean local = true;
  private Set<URL> addresses;

  public ServiceConfig() {}

  public ServiceConfig(String flowName) {
    this.flowName = flowName;
  }

  public AtomicInteger getJointSourceNumber() {
    return jointSourceNumber;
  }

  public void setJointSourceNumber(AtomicInteger jointSourceNumber) {
    this.jointSourceNumber = jointSourceNumber;
  }


  public int jointSourceNumberPlus() {
    return this.jointSourceNumber.incrementAndGet();
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public void setLocal(boolean local) {
    this.local = local;
  }

  /**
   * true: local Service <br/>
   * false: remote Service
   * 
   * @return true/false
   */
  public boolean isLocal() {
    return local;
  }

  /**
   * @return the nextServiceConfigs
   */
  public Set<ServiceConfig> getNextServiceConfigs() {
    return nextServiceConfigs;
  }


  public ServiceConfig addNextServiceConfig(ServiceConfig nextServiceConfig) {
    if (nextServiceConfigs == null) {
      this.nextServiceConfigs = new HashSet<>();
    }
    nextServiceConfigs.add(nextServiceConfig);
    return this;
  }



  /**
   * 服务在流程中的索引位置
   * 
   * @param index 索引
   * @return {@link ServiceConfig}
   */
  public ServiceConfig setIndex(int index) {
    this.index = index;
    return this;
  }

  /**
   * 服务在流程中的索引位置
   * 
   * @return int
   */
  public int getIndex() {
    return index;
  }

  public String getFlowName() {
    return flowName;
  }



  public Set<URL> getAddresses() {
    return addresses;
  }

  public ServiceConfig setAddresses(Set<URL> addresses) {
    this.addresses = addresses;
    return this;
  }

  public ServiceConfig addAddress(URL address) {
    if (addresses == null) {
      this.addresses = new HashSet<URL>();
    }
    this.addresses.add(address);
    return this;
  }

  public ServiceConfig setFlowName(String flowName) {
    this.flowName = flowName;
    return this;
  }

  public void setNextServiceConfigs(Set<ServiceConfig> nextServiceConfigs) {
    this.nextServiceConfigs = nextServiceConfigs;
  }


  public ServiceMeta getServiceMeta() {
    return serviceMeta;
  }

  public ServiceConfig setServiceMeta(ServiceMeta serviceMeta) {
    this.serviceMeta = serviceMeta;
    return this;
  }

  public boolean hasNextServices() {
    return nextServiceConfigs != null && nextServiceConfigs.size() > 0;
  }

  public int getTimeout() {
    int timeout = -1;
    if (serviceMeta != null) {
      timeout = serviceMeta.getTimeout();
    }
    if (timeout <= 0) {
      timeout = 3000;
    }
    return timeout;
  }

  public void setApplication(String application) {
    this.application = application;
  }

  public String getApplication() {
    return application;
  }

  /**
   * 聚合服务
   * 
   * @return true / false
   */
  public boolean isAggregateService() {
    return getServiceMeta().getServiceClassName().equals(Constant.AGGREGATE_SERVICE_NAME);
  }

  public String getSimpleDesc() {
    StringBuilder sb = new StringBuilder();
    sb.append(getServiceName());
    sb.append("(");
    sb.append(nextServiceConfigs == null ? 0 : nextServiceConfigs.size());
    sb.append(")");
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((flowName == null) ? 0 : flowName.hashCode());
    result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ServiceConfig other = (ServiceConfig) obj;
    if (flowName == null) {
      if (other.flowName != null)
        return false;
    } else if (!flowName.equals(other.flowName))
      return false;
    if (serviceName == null) {
      if (other.serviceName != null)
        return false;
    } else if (!serviceName.equals(other.serviceName))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ServiceConfig [flowName=");
    builder.append(flowName);
    builder.append(", serviceName=");
    builder.append(serviceName);
    builder.append(", jointSourceNumber=");
    builder.append(jointSourceNumber);
    builder.append("]");
    return builder.toString();
  }



}
