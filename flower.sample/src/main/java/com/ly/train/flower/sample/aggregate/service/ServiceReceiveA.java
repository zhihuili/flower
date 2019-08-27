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
import com.ly.train.flower.common.annotation.FlowerType;
import com.ly.train.flower.common.core.service.Service;
import com.ly.train.flower.common.core.service.ServiceContext;
import java.util.List;

/**
 * 本Service为分叉集合之后的节点，用于处理服务分叉聚合之后的消息，需注解 @FlowerService(type =
 * FlowerType.AGGREGATE)
 * 
 * @author fengyu.zhang
 */
@FlowerService(type = FlowerType.AGGREGATE)
public class ServiceReceiveA implements Service<List<Object>, Integer> {
  /**
   * 用于处理服务分叉聚合之后的消息
   * 
   * @param message 需要处理的消息，这里使用List接收, 默认返回的是List<Object>
   *        {@link com.ly.train.flower.core.service.impl.AggregateService#process(Object, ServiceContext)}
   *        也可以使用Object接收
   * @param context 服务上下文
   */
  @Override
  public Integer process(List<Object> message, ServiceContext context) throws Throwable {
    System.out.println("处理A分叉之后的聚合消息:");
    Integer sum = 0;
    if (null != message && message.size() > 0) {
      System.out.println("收到数量：" + message.size());
      for (Object obj : message) {
        if (obj instanceof Integer) {
          sum += (Integer) obj;
        }
      }
      System.out.println("求和：" + sum + "开始B分叉处理");
      return sum;
    } else {
      System.out.println("空聚合消息");
      return null;
    }

  }
}
