package com.ly.train.flower.common.sample.one;

import com.ly.train.flower.common.service.message.ReturnMessage;

public class Message3 implements ReturnMessage {
  private Message2 m2;

  public Message2 getM2() {
    return m2;
  }

  public void setM2(Message2 m2) {
    this.m2 = m2;
  }

  public String toString() {
    return m2.getName() + " is " + m2.getAge() + " years old.";
  }
}
