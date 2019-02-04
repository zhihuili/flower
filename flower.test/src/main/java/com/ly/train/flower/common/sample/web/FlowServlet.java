package com.ly.train.flower.common.sample.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.containe.FlowContext;
import com.ly.train.flower.common.service.containe.ServiceContext;
import com.ly.train.flower.common.service.containe.ServiceFactory;
import com.ly.train.flower.common.service.web.Web;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class FlowServlet extends HttpServlet {
  ServiceRouter sr;
  ActorRef actor;

  @Override
  public void init() {
    buildServiceEnv();
    sr = ServiceFacade.buildServiceRouter("flow", "flowService", 400);
    actor = ActorSystem.create("sample").actorOf(Props.create(MyActor.class));
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("text/html;charset=UTF-8");
    PrintWriter out = resp.getWriter();
     out.println("begin：" + System.currentTimeMillis());
     out.flush();

    AsyncContext ctx = req.startAsync();

    // flower
     asyncExe(ctx);

    // thread
//     new Thread(new Executor(ctx)).start();

    //actor
//    ServiceContext serviceContext = new ServiceContext();
//    Web web = new Web(ctx);
//    serviceContext.setWeb(web);
//    String uuid = UUID.randomUUID().toString();
//    FlowContext.putServiceContext(uuid, serviceContext);
//    actor.tell(uuid, null);

    // out.println("结束Servlet的时间：" + new Date() + ".");
    // out.flush();
  }

  private void asyncExe(AsyncContext ctx) {
    long begin = System.currentTimeMillis();
    try {
      sr.asyncCallService(" Hello, Flow World! ", ctx);
      // ServiceFacade.asyncCallService("flow", "flowService", " Hello World! ", ctx);
    } catch (Exception e) {
      e.printStackTrace();
    }
    long end = System.currentTimeMillis();
    System.out.println(end - begin);
  }

  private void buildServiceEnv() {
    ServiceFactory.registerService("flowService",
        "com.ly.train.flower.common.sample.web.FlowService");
//    ServiceFactory.registerService("endService",
//        "com.ly.train.flower.common.service.NothingService");

    ServiceFlow.buildFlow("flower", "flowService", "null");

  }
}
