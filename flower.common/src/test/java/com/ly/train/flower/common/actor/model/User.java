/**
 * 
 */
package com.ly.train.flower.common.actor.model;

/**
 * @author leeyazhou
 *
 */
public class User {

  private String name;
  private String desc;
  private int age;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("User [name=");
    builder.append(name);
    builder.append(", desc=");
    builder.append(desc);
    builder.append(", age=");
    builder.append(age);
    builder.append("]");
    return builder.toString();
  }


}
