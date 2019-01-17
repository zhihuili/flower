package com.ly.train.flower.common.sample.one;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.service.EnvBuilder;
import com.ly.train.flower.common.service.ServiceFactory;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.util.FileUtil;
import junit.framework.Assert;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfSample {

  public static void main(String[] args) throws Exception {
    // buildServiceEnv();
    EnvBuilder.buildEnv();

    Map<String, Message1> message1Map = new HashMap<>();
    for (int i = 0; i < 1000; ++i) {
      Message2 m2 = new Message2(i, String.valueOf(i));
      Message1 m1 = new Message1();
      m1.setM2(m2);
      message1Map.put(String.valueOf(i), m1);
    }

    for (String key : message1Map.keySet()) {
      Message1 m1 = message1Map.get(key);
      Object o = ServiceFacade.syncCallService("sample", "service1", m1);
      Assert.assertEquals(((Message3)o).getM2().getName(), m1.getM2().getName());
      Assert.assertEquals(Integer.parseInt(key), ((Message3) o).getM2().getAge() - 1) ;
    }

    System.out.println("test ok");
  }

  public static void buildServiceEnv() throws Exception {

    ServiceFactory.registerService(FileUtil.readService("/sample.services"));

    ServiceFlow.buildFlow("sample", FileUtil.readFlow("/sample.flow"));

  }

}
