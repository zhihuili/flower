package com.ly.train.flower.common.sample.web.async;

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

public class AsyncServlet extends HttpServlet {
  static final long serialVersionUID = 1L;
  ServiceRouter sr;

  @Override
  public void init() {
    buildServiceEnv();
    sr = ServiceFacade.buildServiceRouter("async", "serviceA", 40);
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
    ServiceFactory.registerService("serviceA",
        "com.ly.train.flower.common.sample.web.async.ServiceA");
    ServiceFactory.registerService("serviceB",
        "com.ly.train.flower.common.sample.web.async.ServiceB");

    ServiceFlow.buildFlow("async", "serviceA", "serviceB");

  }
}
