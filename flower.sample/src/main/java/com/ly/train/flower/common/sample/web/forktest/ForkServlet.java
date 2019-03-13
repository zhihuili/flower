/**
 * Copyright © ${project.inceptionYear} 同程艺龙 (zhihui.li@ly.com)
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
package com.ly.train.flower.common.sample.web.forktest;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.service.FlowerService;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.container.ServiceFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 13:13
 */
public class ForkServlet extends HttpServlet {
    static final long serialVersionUID = 1L;
    ServiceRouter sr;
    ApplicationContext context;

    @Override
    public void init() {
        context = new ClassPathXmlApplicationContext("spring-mybatis.xml");
        buildServiceEnv();
        sr = ServiceFacade.buildServiceRouter("fork", "serviceBegin", 400);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
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
        ServiceFactory.registerService("serviceBegin",
                "com.ly.train.flower.common.sample.web.forktest.service.BeginService");
        ServiceFactory.registerService("GoodsService",
                "com.ly.train.flower.common.sample.web.forktest.service.GoodsService");
        ServiceFactory.registerService("OrderService",
                "com.ly.train.flower.common.sample.web.forktest.service.OrderService");
        ServiceFactory.registerService("UserService",
                "com.ly.train.flower.common.sample.web.forktest.service.UserService");
        ServiceFactory.registerService("AggregateService",
                "com.ly.train.flower.common.service.AggregateService");
        ServiceFactory.registerService("serviceReturn",
                "com.ly.train.flower.common.sample.web.forktest.service.ReturnService");

        // 缺少该语句，会导致spring注入失败
        ServiceFactory.registerFlowerService("GoodsService", (FlowerService) context.getBean("GoodsService"));
        ServiceFactory.registerFlowerService("OrderService", (FlowerService) context.getBean("OrderService"));
        ServiceFactory.registerFlowerService("UserService", (FlowerService) context.getBean("UserService"));

        ServiceFlow.buildFlow("fork", "serviceBegin", "GoodsService");
        ServiceFlow.buildFlow("fork", "serviceBegin", "OrderService");
        ServiceFlow.buildFlow("fork", "serviceBegin", "UserService");
        ServiceFlow.buildFlow("fork", "GoodsService", "AggregateService");
        ServiceFlow.buildFlow("fork", "OrderService", "AggregateService");
        ServiceFlow.buildFlow("fork", "UserService", "AggregateService");
        ServiceFlow.buildFlow("fork", "AggregateService", "serviceReturn");

    }
}
