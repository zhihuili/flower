package com.ly.train.flower.common.sample.one;

import com.ly.train.flower.common.service.Service;

import java.util.Set;

public class Service4 implements Service<Set> {

  @Override
  public Object process(Set message) {
    Message2 m = new Message2();
    for (Object o : message) {

      if (o instanceof Integer) {
        m.setAge((Integer) o);
      }
      if (o instanceof String) {
        m.setName(String.valueOf(o));
      }
    }
    Message3 m3 = new Message3();
    m3.setM2(m);
    return m3;
  }

}
