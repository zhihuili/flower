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
package com.ly.train.flower.common.sample.web.async;

import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.ly.train.flower.common.akka.router.FlowRouter;
import com.ly.train.flower.common.sample.web.FlowerHttpServlet;
import com.ly.train.flower.common.service.FlowerService;

public class AsyncServlet extends FlowerHttpServlet {
  static final long serialVersionUID = 1L;
  FlowRouter sr;
  ApplicationContext context;

  @Override
  public void init() {
    context = new ClassPathXmlApplicationContext("spring-mybatis.xml");
    buildServiceEnv();
    sr = serviceFacade.buildFlowRouter("async", 400);
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
    serviceFactory.registerService("serviceA", "com.ly.train.flower.common.sample.web.async.ServiceA");
    serviceFactory.registerService("serviceB", "com.ly.train.flower.common.sample.web.async.ServiceB");

    serviceFactory.registerFlowerService("serviceB", (FlowerService) context.getBean("serviceB"));

    getServiceFlow("async").buildFlow("serviceA", "serviceB");

  }
}
