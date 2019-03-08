package com.ly.train.flower.common.sample.springboot;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.containe.ServiceFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class IndexController {
  static final long serialVersionUID = 1L;
  ServiceRouter sr;

  public IndexController() {
    buildServiceEnv();
    sr = ServiceFacade.buildServiceRouter("async", "serviceA", 400);
  }

  @RequestMapping(value = "/", method = POST)
  public void index(HttpServletRequest req) {
    AsyncContext context = req.startAsync();
    asyncExe(context);
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
        "com.ly.train.flower.common.sample.springboot.ServiceA");
    ServiceFactory.registerService("serviceB",
        "com.ly.train.flower.common.sample.springboot.ServiceB");

    ServiceFlow.buildFlow("async", "serviceA", "serviceB");

  }
}