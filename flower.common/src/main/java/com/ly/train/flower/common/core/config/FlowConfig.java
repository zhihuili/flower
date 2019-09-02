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
package com.ly.train.flower.common.core.config;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author leeyazhou
 */
public class FlowConfig implements Serializable {

  private static final long serialVersionUID = 1L;
  private Set<String> filters = new HashSet<>();
  private ServiceConfig serviceConfig;
  private String flowName;
  private Long timeout;
  private String application;

  public FlowConfig() {}

  public FlowConfig(String flowName, ServiceConfig serviceConfig) {
    this.serviceConfig = serviceConfig;
    this.flowName = flowName;
  }

  /**
   * @return the filters
   */
  public Set<String> getFilters() {
    return filters;
  }

  /**
   * @param filters the filters to set
   */
  public FlowConfig setFilters(Set<String> filters) {
    this.filters = filters;
    return this;
  }

  /**
   * @return the serviceConfig
   */
  public ServiceConfig getServiceConfig() {
    return serviceConfig;
  }

  /**
   * @param serviceConfig the serviceConfig to set
   */
  public FlowConfig setServiceConfig(ServiceConfig serviceConfig) {
    this.serviceConfig = serviceConfig;
    return this;
  }

  /**
   * @return the flowName
   */
  public String getFlowName() {
    return flowName;
  }

  /**
   * @param flowName the flowName to set
   */
  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  /**
   * @param timeout the timeout to set
   */
  public void setTimeout(Long timeout) {
    this.timeout = timeout;
  }

  /**
   * @return the timeout
   */
  public Long getTimeout() {
    return timeout;
  }

  /**
   * @return the application
   */
  public String getApplication() {
    return application;
  }

  /**
   * @param application the application to set
   */
  public void setApplication(String application) {
    this.application = application;
  }

  @Override
  public String toString() {
    final String newLine = "\r\n\t";
    StringBuilder builder = new StringBuilder();
    builder.append("FlowConfig [");
    builder.append(newLine).append("flowName = ").append(flowName);
    builder.append(newLine).append("timeout = ").append(this.timeout);

    buildString(serviceConfig, builder);
    builder.append("\n]");
    return builder.toString();
  }


  private void buildString(ServiceConfig header, StringBuilder builder) {
    if (header == null) {
      return;
    }
    Set<ServiceConfig> nexts = header.getNextServiceConfigs();
    if (nexts == null || nexts.isEmpty()) {
      return;
    }
    StringBuilder temp = new StringBuilder();
    temp.append("\r\n\t");
    temp.append(header.getSimpleDesc()).append(" --> ");
    Iterator<ServiceConfig> it = nexts.iterator();
    while (it.hasNext()) {
      ServiceConfig item = it.next();
      temp.append(item.getSimpleDesc());
      if (it.hasNext()) {
        temp.append(", ");
      }

    }

    if (builder.indexOf(temp.toString()) == -1) {
      builder.append(temp);
    }
    for (ServiceConfig item : nexts) {
      if (item.getNextServiceConfigs() != null) {
        buildString(item, builder);
      }
    }

  }
}
