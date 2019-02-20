package com.ly.train.flower.common.sample.Supervisor;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.containe.ServiceFactory;
import com.ly.train.flower.common.util.EnvBuilder;
import com.ly.train.flower.common.util.FileUtil;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class BatchSample {

  public static void main(String[] args) throws Exception {
    EnvBuilder.buildEnv(BatchSample.class);
    File file = new File("test.file");
    if (file.exists()) {
      System.out.println(file.getAbsolutePath());
      FileUtils.deleteQuietly(file);
    }
    int count = 0;
    Map<String, Message1> message1Map = new HashMap<>();
    for (int i = 10; i < 20; ++i) {
      Message2 m2 = new Message2(i, String.valueOf(i));
      Message1 m1 = new Message1();
      m1.setM2(m2);
      message1Map.put(String.valueOf(i), m1);
      count++;
    }

    int resultCount = 0;
    for (String key : message1Map.keySet()) {
      Message1 m1 = message1Map.get(key);
      try {
        //
        Object o = ServiceFacade.syncCallService("sample", "service1", m1);
        System.out.println(o);
      } catch (TimeoutException e) {
        e.printStackTrace();
      }

      resultCount++;
    }
    Assert.assertEquals(count, resultCount);
    System.out.println("count:" + count + " resultCount:" + resultCount);
    System.out.println("test ok");
    ServiceFacade.shutdown();
  }
  
  public static void buildServiceEnv() throws Exception {

    ServiceFactory.registerService(FileUtil.readService("/sample.services"));
    ServiceFlow.buildFlow("sample", FileUtil.readFlow("/sample.flow"));

  }
}
