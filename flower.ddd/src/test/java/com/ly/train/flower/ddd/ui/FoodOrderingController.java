package com.ly.train.flower.ddd.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ly.train.flower.ddd.api.command.CreateOrderCommand;
import com.ly.train.flower.ddd.gateway.CommandGateway;
import com.ly.train.flower.ddd.gateway.QueryGateway;

/**
 * @author leeyazhou
 */
@RequestMapping("/foodcart")
@RestController
public class FoodOrderingController {

  @Autowired
  CommandGateway commandGateway;
  @Autowired
  QueryGateway queryGateway;

  @RequestMapping("/create")
  public void createFoodCart() {
    commandGateway.send(new CreateOrderCommand(201L, "foodcart"));
  }

}
