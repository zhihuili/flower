/**
 * 
 */
package com.ly.train.flower.common.actor.service;

import com.ly.train.flower.common.actor.model.User;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
@FlowerService
public class ServiceB implements Service<User, User> {
  static final Logger logger = LoggerFactory.getLogger(ServiceB.class);

  @Override
  public User process(User message, ServiceContext context) throws Throwable {
    message.setDesc(message.getDesc() + " --> " + getClass().getSimpleName());    message.setAge(message.getAge() + 1);
    logger.info("结束处理消息, message : {}", message);
    return message;
  }

}
