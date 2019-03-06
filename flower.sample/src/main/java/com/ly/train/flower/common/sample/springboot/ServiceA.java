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

@BindController(path = "/ServiceA", method= RequestMethod.GET)
public class ServiceA implements Service<User>, InitController {
  @Override
  public Object process(User message, ServiceContext context) throws Exception {
    context.getWeb().println(message.toString());
    return message.getId();
  }

  @Override
  public ServiceRouter init() {
    buildServiceEnv();
    return ServiceFacade.buildServiceRouter("async", "serviceA", 400);
  }

  private static void buildServiceEnv() {
    ServiceFactory.registerService("serviceA",
        "com.ly.train.flower.common.sample.springboot.ServiceA");
    ServiceFactory.registerService("serviceB",
        "com.ly.train.flower.common.sample.springboot.ServiceB");

    ServiceFlow.buildFlow("async", "serviceA", "serviceB");
  }
}
