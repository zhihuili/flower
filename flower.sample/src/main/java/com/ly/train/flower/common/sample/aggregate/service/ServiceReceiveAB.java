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
package com.ly.train.flower.common.sample.aggregate.service;

import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.annotation.FlowerType;
import com.ly.train.flower.common.service.Complete;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.web.Flush;
import java.util.List;

/**
 * @author: fengyu.zhang
 */
@FlowerService(type = FlowerType.AGGREGATE)
public class ServiceReceiveAB implements Service<Object, Void>, Flush, Complete {
  @Override
  public Void process(Object message, ServiceContext context) throws Throwable {
    System.out.println("处理B分叉之后的聚合消息:");
    List<Integer> list = (List<Integer>) message;
    System.out.println("收到数量：" + list.size());
    for (Integer integer : list) {
      System.out.println(integer);
    }
    return null;
  }
}
