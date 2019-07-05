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
import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.core.service.container.ServiceMeta;

/**
 * @author leeyazhou
 * 
 */
public class ServiceInfo implements Serializable {

  private static final long serialVersionUID = 1L;
  private String application;
  private URL address;
  private Date createTime;
  private ServiceMeta serviceMeta;

  public ServiceInfo() {}

  public String toParam() {
    return String.format("className=%s&address=%s&createTime=%s", getServiceMeta().getServiceClassName(), address,
        createTime);
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


  public URL getAddress() {
    return address;
  }

  public void setAddress(URL address) {
    this.address = address;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ServiceInfo [application=");
    builder.append(application);
    builder.append(", address=");
    builder.append(address);
    builder.append(", createTime=");
    builder.append(createTime);
    builder.append("]");
    return builder.toString();
  }



}
