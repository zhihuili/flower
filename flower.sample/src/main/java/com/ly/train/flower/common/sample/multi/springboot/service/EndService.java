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
package com.ly.train.flower.common.sample.multi.springboot.service;

import java.util.List;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.annotation.FlowerType;
import com.ly.train.flower.core.service.Complete;
import com.ly.train.flower.core.service.container.ServiceContext;
import com.ly.train.flower.core.service.impl.AbstractService;
import com.ly.train.flower.core.service.web.Flush;
import com.ly.train.flower.core.service.web.HttpComplete;

/**
 * @author leeyazhou
 * 
 */
@FlowerService(type = FlowerType.AGGREGATE)
public class EndService extends AbstractService<List<Object>, Object> implements Flush, HttpComplete, Complete {
  @Override
  public Object doProcess(List<Object> message, ServiceContext context) throws Throwable {
    context.getWeb().print(message.toString());
    System.out.println("聚合服务收到消息：" + message);
    return message;
  }

  @Override
  public void onError(List<Object> param, ServiceContext context, Throwable throwable) {
    super.onError(param, context, throwable);
  }

}
