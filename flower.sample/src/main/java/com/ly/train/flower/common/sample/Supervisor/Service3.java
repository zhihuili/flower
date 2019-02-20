package com.ly.train.flower.common.sample.Supervisor;

import com.ly.train.flower.common.service.Service;

public class Service3 implements Service<Message2> {

  @Override
  public Object process(Message2 message) throws Throwable {
//    File file = new File("/Users/yang/tmp/test.file");
//    if (!file.exists()) {
//      FileUtils.write(file, "111", "UTF-8");
//      throw new Exception("Service3");
//    } else {
//      FileUtils.deleteQuietly(file);
//    }
    return message.getName().toUpperCase();
  }

}
