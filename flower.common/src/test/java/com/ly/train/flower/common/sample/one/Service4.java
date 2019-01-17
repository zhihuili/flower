package com.ly.train.flower.common.sample.one;

import java.util.Set;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.message.IntegerMessage;
import com.ly.train.flower.common.service.message.JointMessage;
import com.ly.train.flower.common.service.message.StringMessage;

public class Service4 implements Service<JointMessage> {

  @Override
  public Object process(JointMessage message) {
    Message2 m = new Message2();
    for (Object o : message.getSet()) {
      if (o instanceof IntegerMessage)
        m.setAge(((IntegerMessage) o).getMessage());
      if (o instanceof StringMessage) {
        m.setName(((StringMessage) o).getMessage());
      }

    }
    Message3 m3 = new Message3();
    m3.setM2(m);
    return m3;
  }

}
