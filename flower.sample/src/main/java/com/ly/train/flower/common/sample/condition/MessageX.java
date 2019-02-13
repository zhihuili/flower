package com.ly.train.flower.common.sample.condition;

import com.ly.train.flower.common.service.message.Condition;

public class MessageX implements Condition {

  private String src;

  public String getSrc() {
    return src;
  }

  public void setSrc(String src) {
    this.src = src;
  }

  @Override
  public String nextSerivceName() {
    return src;
  }

}
