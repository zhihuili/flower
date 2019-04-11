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
package com.ly.train.flower.common.sample.multi.springboot.controller;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ly.train.flower.common.annotation.Flower;
import com.ly.train.flower.common.sample.multi.springboot.model.OrderExt;
import com.ly.train.flower.common.sample.multi.springboot.service.CreateOrderExtService;
import com.ly.train.flower.common.sample.multi.springboot.service.CreateOrderService;
import com.ly.train.flower.common.sample.multi.springboot.service.EndService;
import com.ly.train.flower.common.sample.multi.springboot.service.StartService;
import com.ly.train.flower.web.spring.FlowerController;

/**
 * @author leeyazhou
 *
 */
@RestController
@RequestMapping("/order/")
@Flower(value = "createOrderFlow", serviceName = "createOrder", flowNumber = 32)
public class CreateOrderController extends FlowerController {

  @RequestMapping(value = "createOrder")
  public void createOrder(OrderExt orderDTO, HttpServletRequest req) throws IOException {
    doProcess(orderDTO, req);
  }

  @Override
  public void buildFlower() {
    getServiceFlow().buildFlow(StartService.class,
        Arrays.asList(CreateOrderService.class, CreateOrderExtService.class));
    getServiceFlow().buildFlow(Arrays.asList(CreateOrderService.class, CreateOrderExtService.class), EndService.class);
    getServiceFlow().build();
  }



}
