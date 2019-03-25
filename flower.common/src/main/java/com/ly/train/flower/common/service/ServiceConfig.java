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
package com.ly.train.flower.common.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceConfig {

  private final String flowName;
  private String serviceName;
  private Set<String> nextServiceNames;
  private Set<String> previousServiceNames;
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

  public Set<String> getNextServiceNames() {
    return nextServiceNames;
  }

  public ServiceConfig addNextServiceName(String nextServiceName) {
    if (nextServiceNames == null) {
      this.nextServiceNames = new HashSet<>();
    }

    nextServiceNames.add(nextServiceName);
    return this;
  }

  public Set<String> getPreviousServiceName() {
    return previousServiceNames;
  }

  public ServiceConfig addPreviousServiceName(String previousServiceName) {
    if (previousServiceNames == null) {
      this.previousServiceNames = new HashSet<>();
    }
    previousServiceNames.add(previousServiceName);
    return this;
  }

  public String getFlowName() {
    return flowName;
  }



}
