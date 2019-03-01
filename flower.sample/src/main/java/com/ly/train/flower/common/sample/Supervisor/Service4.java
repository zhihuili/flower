package com.ly.train.flower.common.sample.Supervisor;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.containe.ServiceContext;

import java.util.Set;

public class Service4 implements Service<Set> {

  @Override
  public Object process(Set message, ServiceContext context) {
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
    // pi();
    // sleep();
    // System.out.println(System.currentTimeMillis());
    return m3;
  }

  /**
   * calculate PI, only waste CPU time
   */
  private void pi() {
    double y = 1.0;
    for (int i = 0; i <= 100; i++) {
      double π = 3 * Math.pow(2, i) * y;
      y = Math.sqrt(2 - Math.sqrt(4 - y * y));
    }
  }

  private void sleep() {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
