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
package com.ly.train.flower.registry.config;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.ly.train.flower.common.service.container.ServiceMeta;
import com.ly.train.flower.common.util.URL;

/**
 * @author leeyazhou
 *
 */
public class ServiceInfo implements Serializable {

  private static final long serialVersionUID = 1L;
  private String application;
  private String className;
  private String serviceName;
  private Set<URL> addresses = new HashSet<URL>();
  private Date createTime;
  // <host, number>
  private Map<String, Integer> number;
  private ServiceMeta serviceMeta;

  public ServiceInfo() {
  }

  public Map<String, Integer> getNumber() {
    return number;
  }

  public void setNumber(Map<String, Integer> number) {
    this.number = number;
  }

  public String toParam() {
    return String.format("className=%s&address=%s&createTime=%s", className, addresses, createTime);
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public void addAddress(URL address) {
    this.addresses.add(address);
  }

  public Set<URL> getAddresses() {
    return addresses;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public void setApplication(String application) {
    this.application = application;
  }

  public String getApplication() {
    return application;
  }

  public ServiceMeta getServiceMeta() {
    return serviceMeta;
  }

  public void setServiceMeta(ServiceMeta serviceMeta) {
    this.serviceMeta = serviceMeta;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ServiceInfo [className=");
    builder.append(className);
    builder.append(", addresses=");
    builder.append(addresses);
    builder.append(", createTime=");
    builder.append(createTime);
    builder.append("]");
    return builder.toString();
  }



}
