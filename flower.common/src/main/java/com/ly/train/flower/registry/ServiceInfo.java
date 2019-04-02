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
