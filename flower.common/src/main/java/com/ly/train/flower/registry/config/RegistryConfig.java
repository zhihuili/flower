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
import com.ly.train.flower.common.util.StringUtil;

/**
 * @author leeyazhou
 *
 */
public class RegistryConfig implements Serializable {

  private static final long serialVersionUID = 6969058141359967490L;
  private String url;
  private String type;
  private String host;
  private Integer port;



  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    if (StringUtil.isNotBlank(url)) {
      String temp = url.replace("/", "");
      String[] t = temp.split(":");
      if (t.length == 3) {
        this.type = t[0];
        this.host = t[1];
        this.port = Integer.parseInt(t[2]);
      }
    }

    this.url = url;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RegistryConfig [url=");
    builder.append(url);
    builder.append(", type=");
    builder.append(type);
    builder.append(", host=");
    builder.append(host);
    builder.append(", port=");
    builder.append(port);
    builder.append("]");
    return builder.toString();
  }


}
