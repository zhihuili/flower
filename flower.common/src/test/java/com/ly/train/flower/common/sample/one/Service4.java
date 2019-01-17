package com.ly.train.flower.common.sample.one;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.message.FlowMessage;

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
      if (o instanceof Set) {
        for(Object o1 : (Set)o) {
          if (o1 instanceof Integer) {
            m.setAge((Integer) o1);
          }
          if (o1 instanceof String) {
            m.setName(String.valueOf(o1));
          }
          if (o1 instanceof Set) {
            for(Object o2 : (Set)o1) {
              if (o2 instanceof Integer) {
                m.setAge((Integer) o2);
              }
              if (o2 instanceof String) {
                m.setName(String.valueOf(o2));
              }
            }
          }
        }
      }
    }
    Message3 m3 = new Message3();
    m3.setM2(m);
    return m3;
  }

}
