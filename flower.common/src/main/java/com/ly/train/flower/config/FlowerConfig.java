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
package com.ly.train.flower.config;

import java.io.Serializable;
import java.util.Set;
import com.ly.train.flower.registry.config.RegistryConfig;

/**
 * @author leeyazhou
 *
 */
public class FlowerConfig implements Serializable {
  private static final long serialVersionUID = 1L;

  private String name = "SET_YOUR_OWN_APPLICATON_NAME";
  private String host = "127.0.0.1";
  private int port = 25001;
  private Set<RegistryConfig> registry;

  public Set<RegistryConfig> getRegistry() {
    return registry;
  }

  public void setRegistry(Set<RegistryConfig> registry) {
    this.registry = registry;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FlowerConfig [name=");
    builder.append(name);
    builder.append(", registry=");
    builder.append(registry);
    builder.append("]");
    return builder.toString();
  }


}
