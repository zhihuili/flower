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
import java.util.HashMap;
import java.util.Map;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.common.util.URL;

/**
 * @author leeyazhou
 *
 */
public class RegistryConfig implements Serializable {

  private static final long serialVersionUID = 6969058141359967490L;
  private String url;
  private String protocol;
  private String host;
  private Integer port;
  private Map<String, String> params = new HashMap<>();

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

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getUrl() {
    return url;
  }

  public URL toURL() {
    URL ret = new URL(protocol, host, port);
    ret.setParams(params);
    return ret;
  }

  public void setUrl(String url) {
    if (StringUtil.isNotBlank(url)) {
      String[] tm = url.split("\\?");
      String temp = tm[0].replace("/", "");
      String[] t = temp.split(":");
      if (t.length == 3) {
        this.protocol = t[0];
        this.host = t[1];
        this.port = Integer.parseInt(t[2]);
      }

      if (tm.length == 2) {
        String[] params = tm[1].split("&");
        for (String it : params) {
          if (StringUtil.isNotBlank(it)) {
            String[] kv = it.split("=");
            if (kv != null && kv.length == 2) {
              this.params.put(kv[0], kv[1]);
            }
          }
        }
      }

    }

    this.url = url;
  }

  public Map<String, String> getParams() {
    return params;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RegistryConfig [url=");
    builder.append(url);
    builder.append(", protocol=");
    builder.append(protocol);
    builder.append(", host=");
    builder.append(host);
    builder.append(", port=");
    builder.append(port);
    builder.append(", params=");
    builder.append(params);
    builder.append("]");
    return builder.toString();
  }


}
