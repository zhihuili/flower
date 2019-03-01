package com.ly.train.flower.common.sample.Supervisor;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.containe.ServiceContext;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class Service2 implements Service<Message2> {

  @Override
  public Object process(Message2 message, ServiceContext context) throws Throwable {

    File file = new File("test.file");
    if (!file.exists()) {
      FileUtils.write(file, "111", "UTF-8");
      throw new Exception("Service2");
    }
    return message.getAge() + 1;
  }

}
