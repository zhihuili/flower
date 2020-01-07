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
package com.ly.train.flower.ddd.ui;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ly.train.flower.ddd.api.command.CreateOrderCommand;
import com.ly.train.flower.ddd.api.command.SelectOrderCommand;
import com.ly.train.flower.ddd.command.FoodCart;
import com.ly.train.flower.ddd.gateway.CommandGateway;
import com.ly.train.flower.ddd.gateway.QueryGateway;

/**
 * @author leeyazhou
 */
@RequestMapping("/foodcart")
@RestController
public class FoodOrderingController {
  private AtomicInteger index = new AtomicInteger();
  @Autowired
  CommandGateway commandGateway;
  @Autowired
  QueryGateway queryGateway;

  @RequestMapping("/create")
  public void createFoodCart() {
    commandGateway.send(new CreateOrderCommand(index.incrementAndGet(), "foodcart"));
  }

  @RequestMapping("{orderId}")
  public void queryFoodcart(@PathVariable Long orderId) {
	  queryGateway.query(new SelectOrderCommand(orderId));
  }
}
