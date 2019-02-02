package com.ly.train.flower.common.service.message;

public class DefaultMessage {
  private DefaultMessage() {
  }

  private static DefaultMessage i = new DefaultMessage();

  public static DefaultMessage getMessage() {
    return i;
  }

}
