package com.ly.train.flower.common.sample.springboot;


import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.containe.ServiceFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;

@RestController
public class Index2Controller {
  static final long serialVersionUID = 1L;
  ServiceRouter serviceRouter;

  public Index2Controller() {
    buildServiceEnv();
    serviceRouter = ServiceFacade.buildServiceRouter("async", "serviceA", 400);
  }

  @RequestMapping("/index2")
  public void index(HttpServletRequest req) {
    AsyncContext context = req.startAsync();
    asyncExe(context);
  }

  private void asyncExe(AsyncContext ctx) {
    try {
      serviceRouter.asyncCallService(null, ctx);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void buildServiceEnv() {
    ServiceFactory.registerService("serviceA",
        "com.ly.train.flower.common.sample.springboot.ServiceA");
    ServiceFactory.registerService("serviceB",
        "com.ly.train.flower.common.sample.springboot.ServiceB");

    ServiceFlow.buildFlow("async", "serviceA", "serviceB");

  }
}