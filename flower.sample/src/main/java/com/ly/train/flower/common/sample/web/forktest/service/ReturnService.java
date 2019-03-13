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

import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.common.service.Complete;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.web.Flush;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 13:23
 */
public class ReturnService
    implements com.ly.train.flower.common.service.Service<Set>, Flush, Complete {

  @Override
  public Object process(Set message, ServiceContext context) throws Exception {
    context.getWeb().print(JSONObject.toJSONString(message));
    return null;
  }
}
