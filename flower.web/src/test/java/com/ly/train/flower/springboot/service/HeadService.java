/**
 * 
 */
package com.ly.train.flower.springboot.service;

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
public class HeadService implements Service<User, User> {

  private static final Logger logger = LoggerFactory.getLogger(HeadService.class);

  @Override
  public User process(User message, ServiceContext context) throws Throwable {
    logger.info("处理消息：{} ", message);
    message.setName(message.getName() + "-->" + "HeaderName");
    return message;
  }



}
