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
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.util.Constant;

/**
 * 流程服务节点配置
 * 
 * @author leeyazhou
 *
 */
public class ServiceConfig implements Serializable {

  private static final long serialVersionUID = 1L;
  private final String flowName;
  private String serviceName;
  private Set<ServiceConfig> nextServiceConfigs;
  private Set<ServiceConfig> previousServiceConfigs;
  private final AtomicInteger jointSourceNumber = new AtomicInteger(0);

  public ServiceConfig(String flowName) {
    this.flowName = flowName;
  }

  public int getJointSourceNumber() {
    return jointSourceNumber.get();
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

  /**
   * @return the nextServiceConfigs
   */
  public Set<ServiceConfig> getNextServiceConfigs() {
    return nextServiceConfigs;
  }

  /**
   * @return the previousServiceConfigs
   */
  public Set<ServiceConfig> getPreviousServiceConfigs() {
    return previousServiceConfigs;
  }

  public ServiceConfig addNextServiceConfig(ServiceConfig nextServiceConfig) {
    if (nextServiceConfigs == null) {
      this.nextServiceConfigs = new HashSet<>();
    }

    nextServiceConfigs.add(nextServiceConfig);
    return this;
  }


  public ServiceConfig addPreviousServiceConfig(ServiceConfig previousServiceConfig) {
    if (previousServiceConfigs == null) {
      this.previousServiceConfigs = new HashSet<>();
    }
    previousServiceConfigs.add(previousServiceConfig);
    return this;
  }

  public String getFlowName() {
    return flowName;
  }

  public boolean hasNextServices() {
    return nextServiceConfigs != null && nextServiceConfigs.size() > 0;
  }

  public boolean hasPreviousServices() {
    return previousServiceConfigs != null && previousServiceConfigs.size() > 0;
  }

  /**
   * 聚合服务
   * 
   * @return
   */
  public boolean isAggregateService() {
    return ServiceFactory.getServiceClassName(getServiceName()).equals(Constant.AGGREGATE_SERVICE_NAME);
  }

  public String getSimpleDesc() {
    StringBuilder sb = new StringBuilder();
    sb.append(getServiceName());
    sb.append("(");
    sb.append(previousServiceConfigs == null ? 0 : previousServiceConfigs.size()).append(":");
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
