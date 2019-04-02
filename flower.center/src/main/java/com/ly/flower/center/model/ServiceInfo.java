/**
 * 
 */
package com.ly.flower.center.model;

import java.util.Set;

/**
 * @author leeyazhou
 *
 */
public class ServiceInfo {

  private String className;
  private Set<String> host;

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



}
