/**
 * 
 */
package com.ly.train.flower.springboot.service;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.springboot.model.User;

/**
 * @author leeyazhou
 *
 */
@FlowerService
public class EndService implements Service<Set<User>, Integer> {

  private static final Logger logger = LoggerFactory.getLogger(EndService.class);

  @Override
  public Integer process(Set<User> message, ServiceContext context) throws Throwable {
    logger.info("处理消息：{} ", message);
    int age = 0;
    for (User user : message) {
      age += user.getAge();
    }
    logger.info("年龄：{}", age);
    return age;
  }



}
