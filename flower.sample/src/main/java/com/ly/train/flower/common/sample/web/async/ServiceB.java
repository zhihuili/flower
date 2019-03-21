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
package com.ly.train.flower.common.sample.web.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.common.sample.web.dao.UserDao;
import com.ly.train.flower.common.sample.web.mode.User;
import com.ly.train.flower.common.service.Complete;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.web.Flush;

@Service("serviceB")
public class ServiceB
    implements com.ly.train.flower.common.service.Service<Integer,Void>, Flush, Complete {

  @Autowired
  private UserDao userDao;

  @Override
  public Void process(Integer message, ServiceContext context) throws Exception {
    User user = userDao.findUser(message);
    if (user == null) {
      context.getWeb().complete();
      throw new NullPointerException("user:" + message + " is not found.");
    }
    String result = JSONObject.toJSONString(user);
    context.getWeb().print(result);
    return null;
  }

}
