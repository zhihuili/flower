package com.ly.train.flower.common.sample.web.async;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.service.FlowerService;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.containe.ServiceFactory;

public class AsyncServlet extends HttpServlet {
  static final long serialVersionUID = 1L;
  ServiceRouter sr;
  ApplicationContext context;

  @Override
  public void init() {
    context = new ClassPathXmlApplicationContext("spring-mybatis.xml");
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

    ServiceFactory.registerFlowerService("serviceB", (FlowerService) context.getBean("serviceB"));

    ServiceFlow.buildFlow("async", "serviceA", "serviceB");

  }
}
