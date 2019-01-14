package com.ly.train.flower.common.sample.one;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.service.ServiceFactory;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.util.FileUtil;

public class ConfSample {

  public static void main(String[] args) throws Exception {
    buildServiceEnv();

    Message2 m2 = new Message2(10, "Zhihui");
    Message1 m1 = new Message1();
    m1.setM2(m2);

    System.out.println(ServiceFacade.syncCallService("sample", "service1", m1));

    Thread.sleep(2000);
    System.exit(0);
  }

  public static void buildServiceEnv() throws Exception {

    ServiceFactory.registerService(FileUtil.readService("/services.properties"));

    ServiceFlow.buildFlow("sample", FileUtil.readFlow("/sample.flow"));

  }

}
