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
package com.ly.train.flower.sample.web.forktest;

import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.ly.train.flower.common.core.service.FlowerService;
import com.ly.train.flower.core.akka.router.FlowRouter;
import com.ly.train.flower.core.service.container.ServiceFlow;
import com.ly.train.flower.sample.web.FlowerHttpServlet;

/**
 * @author fengyu.zhang
 * @date 2019/2/24 13:13
 */
public class ForkServlet extends FlowerHttpServlet {
  static final long serialVersionUID = 1L;
  FlowRouter sr;
  ApplicationContext context;

  @Override
  public void init() {
    context = new ClassPathXmlApplicationContext("spring-mybatis.xml");
    buildServiceEnv();
    sr = flowerFactory.getActorFactory().buildFlowRouter("fork", 400);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html;charset=UTF-8");

    AsyncContext ctx = req.startAsync();

    asyncExe(ctx);
  }

  private void asyncExe(AsyncContext ctx) {
    try {
      sr.asyncCallService(null, ctx);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void buildServiceEnv() {
    serviceFactory.registerService("serviceBegin", "com.ly.train.flower.sample.web.forktest.service.BeginService");
    serviceFactory.registerService("GoodsService", "com.ly.train.flower.sample.web.forktest.service.GoodsService");
    serviceFactory.registerService("OrderService", "com.ly.train.flower.sample.web.forktest.service.OrderService");
    serviceFactory.registerService("UserService", "com.ly.train.flower.sample.web.forktest.service.UserService");
    serviceFactory.registerService("AggregateService", "com.ly.train.flower.common.service.AggregateService");
    serviceFactory.registerService("serviceReturn", "com.ly.train.flower.sample.web.forktest.service.ReturnService");

    // 缺少该语句，会导致spring注入失败
    serviceFactory.registerFlowerService("GoodsService", (FlowerService) context.getBean("GoodsService"));
    serviceFactory.registerFlowerService("OrderService", (FlowerService) context.getBean("OrderService"));
    serviceFactory.registerFlowerService("UserService", (FlowerService) context.getBean("UserService"));

    ServiceFlow serviceFlow = getServiceFlow("fork").buildFlow("serviceBegin", "GoodsService");
    serviceFlow.buildFlow("serviceBegin", "OrderService");
    serviceFlow.buildFlow("serviceBegin", "UserService");
    serviceFlow.buildFlow("GoodsService", "AggregateService");
    serviceFlow.buildFlow("OrderService", "AggregateService");
    serviceFlow.buildFlow("UserService", "AggregateService");
    serviceFlow.buildFlow("AggregateService", "serviceReturn");

  }
}
