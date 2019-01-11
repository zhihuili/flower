package com.ly.train.flower.common.sample.one;

import com.ly.train.flower.common.service.message.BlockedMessage;

public class Message1 implements BlockedMessage {
  private Message2 m2;

  public Message2 getM2() {
    return m2;
  }

  public void setM2(Message2 m2) {
    this.m2 = m2;
  }

}
