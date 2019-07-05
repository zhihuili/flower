/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package com.ly.train.flower.springboot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.core.service.Service;
import com.ly.train.flower.core.service.container.ServiceContext;
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
