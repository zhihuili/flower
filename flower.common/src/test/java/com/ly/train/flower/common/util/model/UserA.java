/**
 * 
 */
package com.ly.train.flower.common.util.model;

/**
 * @author leeyazhou
 *
 */
public class UserA {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UserA [name=");
    builder.append(name);
    builder.append("]");
    return builder.toString();
  }


}
