package com.ly.train.flower.common.sample.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.service.ServiceFactory;
import com.ly.train.flower.common.service.ServiceFlow;

public class FlowServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("text/html;charset=UTF-8");
    PrintWriter out = resp.getWriter();
    out.println("进入Servlet的时间：" + new Date() + ".");
    out.flush();

    AsyncContext ctx = req.startAsync();

    //flower
     asyncExe(ctx);
     
     //thread
//    new Thread(new Executor(ctx)).start();

    out.println("结束Servlet的时间：" + new Date() + ".");
    out.flush();
  }

  private void asyncExe(AsyncContext ctx) {
    buildServiceEnv();
    try {
      ServiceFacade.asyncCallService("flow", "flowService", " Hello World! ");
    } catch (Exception e) {
      e.printStackTrace();
    }
    ctx.complete();
  }

  private void buildServiceEnv() {
    ServiceFactory.registerService("flowService",
        "com.ly.train.flower.common.sample.web.FlowService");
    ServiceFactory.registerService("endService",
        "com.ly.train.flower.common.service.NothingService");

    // serviceA -> serviceB -> serviceC
    ServiceFlow.buildFlow("flower", "flowService", "endService");

  }
}
