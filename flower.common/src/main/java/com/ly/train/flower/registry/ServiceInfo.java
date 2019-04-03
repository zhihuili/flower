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
package com.ly.train.flower.registry;

import java.util.Date;
import java.util.Set;

/**
 * @author leeyazhou
 *
 */
public class ServiceInfo {

  private String application;
  private String className;
  private Set<String> host;
  private Date createTime;

  public String toParam() {
    return String.format("className=%s&host=%s&createTime=%s", className, host, createTime);
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public Set<String> getHost() {
    return host;
  }

  public void setHost(Set<String> host) {
    this.host = host;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  /**
   * @param application the application to set
   */
  public void setApplication(String application) {
    this.application = application;
  }

  /**
   * @return the application
   */
  public String getApplication() {
    return application;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ServiceInfo [className=");
    builder.append(className);
    builder.append(", host=");
    builder.append(host);
    builder.append(", createTime=");
    builder.append(createTime);
    builder.append("]");
    return builder.toString();
  }



}
