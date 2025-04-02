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
package com.ly.train.flower.ddd.command;

import java.io.IOException;

import com.alibaba.fastjson2.JSON;
import com.ly.train.flower.common.annotation.Aggregate;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.ddd.annotation.CommandHandler;
import com.ly.train.flower.ddd.annotation.EventHandler;
import com.ly.train.flower.ddd.api.command.CreateOrderCommand;
import com.ly.train.flower.ddd.api.command.SelectOrderCommand;
import com.ly.train.flower.ddd.api.event.CreateOrderEvent;
import com.ly.train.flower.ddd.api.event.SelectOrderEvent;
import com.ly.train.flower.ddd.config.AggregateLifecycle;

/**
 * @author leeyazhou
 */
@Aggregate
public class FoodCart {

  private static final Logger logger = LoggerFactory.getLogger(FoodCart.class);

  @CommandHandler
  public void command(CreateOrderCommand command, ServiceContext context) {
    logger.info("创建订单命令：{}", command);
    AggregateLifecycle.apply(new CreateOrderEvent(command.getId(), command.getName()));
  }

  @CommandHandler
  public void command(SelectOrderCommand command, ServiceContext context) {
    logger.info("选择订单命令：{}", command);
    AggregateLifecycle.apply(new SelectOrderEvent(command.getId()));
  }

  @EventHandler
  public void on(CreateOrderEvent event, ServiceContext context) throws IOException {
    logger.info("处理订单事件：{}", event);
    context.getWeb().printJSON(JSON.toJSONString(event));
    // dao
  }

  @EventHandler
  public void on(SelectOrderEvent event, ServiceContext context) {
    logger.info("选择订单事件：{}", event);
  }
}
