package com.ly.train.flower.common.service;

import com.ly.train.flower.common.service.containe.ServiceContext;
import com.ly.train.flower.common.service.message.Condition;

public class ConditionService implements Service<Condition> {

  private String[] condition;

  public ConditionService(String config) {
    condition = config.split(",");
  }

  @Override
  public Object process(Condition message, ServiceContext context) {
    Object o = message.getCondition();
    if (o instanceof Boolean) {
      if ((Boolean) o == true) {
        message.setCondition(condition[0]);
      } else {
        message.setCondition(condition[1]);
      }
    }
    if (o instanceof Integer && (Integer) o < condition.length) {
      message.setCondition(condition[(Integer) o]);
    }
    return message;
  }

}
