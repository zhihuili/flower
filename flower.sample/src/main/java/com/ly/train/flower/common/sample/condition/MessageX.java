package com.ly.train.flower.common.sample.condition;

import com.ly.train.flower.common.service.message.Condition;

public class MessageX implements Condition {

  private Object condition;

  public void setCondition(Object src) {
    this.condition = src;
  }

  @Override
  public Object getCondition() {
    return condition;
  }

}
