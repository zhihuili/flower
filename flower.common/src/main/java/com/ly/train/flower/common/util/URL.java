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
package com.ly.train.flower.common.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leeyazhou
 * 
 */
public class URL implements Serializable {

  private static final long serialVersionUID = 1L;

  private String protocol;
  private String host;
  private int port;
  private Map<String, String> params = new HashMap<String, String>();

  /**
   * http://www.baidu.com:8080
   * 
   * @param address address
   * @return {@link URL}
   */
  public static URL valueOf(String address) {
    // flower://127.0.0.1:8096?application=LocalFlower
    if (StringUtil.isNotBlank(address)) {
      String[] t = address.replaceAll("/", "").split(":");
      if (t.length == 3) {
        return new URL(t[0], t[1], Integer.parseInt(t[2]));
      }
    }
    return null;
  }

  public URL() {}

  public URL(String protocol, String host, int port) {
    this.protocol = protocol;
    this.host = host;
    this.port = port;
  }

  public void addParam(String key, String value) {
    this.params.put(key, value);
  }

  public String getParam(String key) {
    return params.get(key);
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public void setParams(Map<String, String> params) {
    this.params = params;
  }

  public Map<String, String> getParams() {
    return params;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((host == null) ? 0 : host.hashCode());
    result = prime * result + port;
    result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
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
    URL other = (URL) obj;
    if (host == null) {
      if (other.host != null)
        return false;
    } else if (!host.equals(other.host))
      return false;
    if (port != other.port)
      return false;
    if (protocol == null) {
      if (other.protocol != null)
        return false;
    } else if (!protocol.equals(other.protocol))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("URL [protocol=");
    builder.append(protocol);
    builder.append(", host=");
    builder.append(host);
    builder.append(", port=");
    builder.append(port);
    builder.append("]");
    return builder.toString();
  }


}
