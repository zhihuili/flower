package com.ly.train.flower.common.sample.one;

public class Message2 {
  private String name;
  private int age;
  private int id;

  public Message2() {
  }

  public Message2(int age, String name) {
    this.age = age;
    this.name = name;
  }

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

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
