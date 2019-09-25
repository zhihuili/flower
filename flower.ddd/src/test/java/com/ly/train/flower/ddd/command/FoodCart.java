package com.ly.train.flower.ddd.command;

import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.ddd.annotation.AggregateIdentifier;
import com.ly.train.flower.ddd.annotation.CommandHandler;
import com.ly.train.flower.ddd.annotation.EventHandler;
import com.ly.train.flower.ddd.api.command.CreateOrderCommand;
import com.ly.train.flower.ddd.api.command.SelectOrderCommand;
import com.ly.train.flower.ddd.api.event.CreateOrderEvent;
import com.ly.train.flower.ddd.api.event.SelectOrderEvent;

/**
 * @author leeyazhou
 */
public class FoodCart {

  private static final Logger logger = LoggerFactory.getLogger(FoodCart.class);
  @AggregateIdentifier
  private Long id;
  private String name;

  @CommandHandler
  public CreateOrderEvent command(CreateOrderCommand command, ServiceContext context) {
    logger.info("创建订单命令：{}", command);
    return new CreateOrderEvent(command.getId(), command.getName());
  }

  @CommandHandler
  public SelectOrderEvent command(SelectOrderCommand command, ServiceContext context) {
    logger.info("选择订单命令：{}", command);
    return new SelectOrderEvent(command.getId());
  }

  @EventHandler
  public void command(CreateOrderEvent event, ServiceContext context) {
    this.id = event.getId();
    this.name = event.getName();
    logger.info("处理订单事件：{}", event);
  }

  @EventHandler
  public void command(SelectOrderEvent event, ServiceContext context) {
    logger.info("选择订单事件：{}", event);
  }
}
