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
package com.ly.train.flower.common.sample.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ly.train.flower.core.akka.router.FlowRouter;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class FlowServlet extends FlowerHttpServlet {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  FlowRouter sr;
  ActorRef actor;

  @Override
  public void init() {
    buildServiceEnv();
    sr = flowerFactory.getServiceFacade().buildFlowRouter("flow", 400);
    actor = ActorSystem.create("sample").actorOf(Props.create(RouterActor.class));
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html;charset=UTF-8");
    PrintWriter out = resp.getWriter();
    out.println("begin：" + System.currentTimeMillis());
    out.flush();

    AsyncContext ctx = req.startAsync();

    // flower
    asyncExe(ctx);

    // thread
    // new Thread(new Executor(ctx)).start();

    // actor
    // ServiceContext serviceContext = new ServiceContext();
    // Web web = new Web(ctx);
    // serviceContext.setWeb(web);
    // String uuid = UUID.randomUUID().toString();
    // FlowContext.putServiceContext(uuid, serviceContext);
    // actor.tell(uuid, null);

    // out.println("结束Servlet的时间：" + new Date() + ".");
    // out.flush();
  }

  private void asyncExe(AsyncContext ctx) {
    try {
      sr.asyncCallService(" Hello, Flow World! ", ctx);
      // ctx.getResponse().getWriter().println("结束Servlet的时间：" + new Date() +
      // ".");
      // ctx.complete();
      // ServiceFacade.asyncCallService("flow", "flowService", " Hello World! ",
      // ctx);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void buildServiceEnv() {
    flowerFactory.getServiceFactory().registerService("flowService",
        "com.ly.train.flower.common.sample.web.FlowService");
    // ServiceFactory.registerService("endService",
    // "com.ly.train.flower.common.service.NothingService");

    getServiceFlow("flower").buildFlow("flowService", "null");

  }
}
