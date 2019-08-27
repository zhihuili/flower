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
package com.ly.train.flower.sample.aggregate.service;

import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.core.service.Service;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.sample.aggregate.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author fengyu.zhang
 */
@FlowerService
public class ServiceForkA1 implements Service<Integer, Integer> {
  @Autowired
  UserDao userDao;

  @Override
  public Integer process(Integer message, ServiceContext context) throws Throwable {
    System.out.println(userDao.findUser(1).getName());
    Integer result = message + 1;
    System.out.println("ForkA1:已处理,结果:" + result);
    return result;
  }

  public String getName() {
    return userDao.findUser(1).getName();
  }
}
