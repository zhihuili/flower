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
package com.ly.train.flower.common.sample.web.forktest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ly.train.flower.common.sample.web.dao.UserDao;
import com.ly.train.flower.common.sample.web.mode.User;
import com.ly.train.flower.common.service.container.ServiceContext;

/**
 * @author fengyu.zhang
 * @date 2019/2/24 17:15
 */

@Service("UserService")
public class UserService implements com.ly.train.flower.common.service.Service<Integer, User> {
  @Autowired
  private UserDao userDao;

  @Override
  public User process(Integer message, ServiceContext context) throws Exception {
    User user = userDao.findUser(message);
    return user == null ? new User() : user;
  }
}
