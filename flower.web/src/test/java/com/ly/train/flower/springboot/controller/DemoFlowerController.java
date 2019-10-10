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
/**
 * 
 */
package com.ly.train.flower.springboot.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ly.train.flower.common.annotation.Flower;
import com.ly.train.flower.core.service.container.ServiceFlow;
import com.ly.train.flower.springboot.model.User;
import com.ly.train.flower.springboot.service.AggregateUserService;
import com.ly.train.flower.springboot.service.EndService;
import com.ly.train.flower.springboot.service.HeadService;
import com.ly.train.flower.springboot.service.UserService;
import com.ly.train.flower.springboot.service.UserService2;
import com.ly.train.flower.web.spring.FlowerController;

/**
 * @author leeyazhou
 * 
 */
@Flower(value = "flower", serviceName = "HeadService")
@RequestMapping("/flower/")
@RestController
public class DemoFlowerController extends FlowerController {

  @RequestMapping("test")
  public void test(User user, HttpServletRequest req) throws IOException {
    doProcess(user, req);
    logger.info("请求参数：{}", user);
  }

  @Override
  public void buildFlower() {
    ServiceFlow serviceFlow = getServiceFlow();

    serviceFlow.buildFlow(HeadService.class, UserService.class);
    serviceFlow.buildFlow(HeadService.class, UserService2.class);
    serviceFlow.buildFlow(UserService.class, AggregateUserService.class);
    serviceFlow.buildFlow(UserService2.class, AggregateUserService.class);
    serviceFlow.buildFlow(AggregateUserService.class, EndService.class);
    getServiceFlow().build();
  }

}
