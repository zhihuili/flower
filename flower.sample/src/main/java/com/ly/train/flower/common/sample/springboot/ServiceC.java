package com.ly.train.flower.common.sample.springboot;

import com.ly.flower.web.springboot.InitController;
import com.ly.flower.web.springboot.annotation.BindController;
import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.containe.ServiceContext;
import com.ly.train.flower.common.service.containe.ServiceFactory;
import org.springframework.web.bind.annotation.RequestMethod;

@BindController(path = "/ServiceC", method= RequestMethod.POST)
public class ServiceC implements Service<String>, InitController {
  @Override
  public Object process(String message, ServiceContext context) throws Exception {
    context.getWeb().println("CCCCCCCCCCCCCCCCCCCCCCCCCCc");
    return 333333;
  }

  @Override
  public Object init() {
    buildServiceEnv();
    return ServiceFacade.buildServiceRouter("async", "serviceC", 400);
  }

  private static void buildServiceEnv() {
    ServiceFactory.registerService("serviceC",
        "com.ly.train.flower.common.sample.springboot.ServiceC");
    ServiceFactory.registerService("serviceB",
        "com.ly.train.flower.common.sample.springboot.ServiceB");

    ServiceFlow.buildFlow("async", "serviceC", "serviceB");
  }
}
