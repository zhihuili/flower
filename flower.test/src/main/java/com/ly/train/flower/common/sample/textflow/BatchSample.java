package com.ly.train.flower.common.sample.textflow;

import java.util.HashMap;
import java.util.Map;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.service.EnvBuilder;
import com.ly.train.flower.common.service.ServiceFactory;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.util.FileUtil;

import junit.framework.Assert;

public class BatchSample {

  public static void main(String[] args) throws Exception {
    EnvBuilder.buildEnv();

    int count = 0;
    Map<String, Message1> message1Map = new HashMap<>();
    for (int i = 20; i < 1111; ++i) {
      Message2 m2 = new Message2(i, String.valueOf(i));
      Message1 m1 = new Message1();
      m1.setM2(m2);
      message1Map.put(String.valueOf(i), m1);
      count++;
    }

    int resultCount = 0;
    for (String key : message1Map.keySet()) {
      Message1 m1 = message1Map.get(key);
      Object o = ServiceFacade.syncCallService("sample", "service1", m1);
      System.out.println(o);
      Assert.assertEquals(((Message3)o).getM2().getName(), m1.getM2().getName());
      Assert.assertEquals(Integer.parseInt(key), ((Message3) o).getM2().getAge() - 1) ;
      resultCount++;
    }
    Assert.assertEquals(count, resultCount);
    System.out.println("count:" + count + " resultCount:" + resultCount);
    System.out.println("test ok");
    System.exit(0);
  }
  
  public static void buildServiceEnv() throws Exception {

    ServiceFactory.registerService(FileUtil.readService("/sample.services"));
    ServiceFlow.buildFlow("sample", FileUtil.readFlow("/sample.flow"));

  }
}
