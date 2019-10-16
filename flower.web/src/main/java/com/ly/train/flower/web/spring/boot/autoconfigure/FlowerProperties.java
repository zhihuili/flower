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
package com.ly.train.flower.web.spring.boot.autoconfigure;

import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import com.ly.train.flower.config.FlowerConfig;

/**
 * @author leeyazhou
 */
@ConfigurationProperties(prefix = "flower", ignoreUnknownFields = true)
public class FlowerProperties {

  /**
   * flower configuration
   */
  @NestedConfigurationProperty
  private FlowerConfig config = new FlowerConfig();

  /**
   * flower registry configuration
   */
  @NestedConfigurationProperty
  private Set<String> registry;

  public FlowerConfig getConfig() {
    return config;
  }

  public void setConfig(FlowerConfig config) {
    this.config = config;
  }

  public Set<String> getRegistry() {
    return registry;
  }

  public void setRegistry(Set<String> registry) {
    this.registry = registry;
  }

}
