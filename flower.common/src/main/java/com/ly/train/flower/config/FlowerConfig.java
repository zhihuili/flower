/**
 * 
 */
package com.ly.train.flower.config;

import java.io.Serializable;
import com.ly.train.flower.registry.config.RegistryConfig;

/**
 * @author leeyazhou
 *
 */
public class FlowerConfig implements Serializable {
  private static final long serialVersionUID = 1L;

  private String name;
  private RegistryConfig registry;

  public RegistryConfig getRegistry() {
    return registry;
  }

  public void setRegistry(RegistryConfig registry) {
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
