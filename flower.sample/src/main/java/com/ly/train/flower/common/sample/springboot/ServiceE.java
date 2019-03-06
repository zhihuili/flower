package com.ly.train.flower.common.sample.springboot;

import com.ly.flower.web.springboot.InitController;
import com.ly.flower.web.springboot.PostJson;
import com.ly.flower.web.springboot.annotation.BindController;
import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.containe.ServiceContext;
import com.ly.train.flower.common.service.containe.ServiceFactory;
import org.springframework.web.bind.annotation.RequestMethod;

@BindController(path = "/ServiceE", method = RequestMethod.POST)
public class ServiceE implements Service<User>, InitController, PostJson {

    @Override
    public ServiceRouter init() {
        buildServiceEnv();
        return ServiceFacade.buildServiceRouter("async", "serviceE", 400);
    }

    private static void buildServiceEnv() {
        ServiceFactory.registerService("serviceE",
                "com.ly.train.flower.common.sample.springboot.ServiceE");
        ServiceFactory.registerService("serviceB",
                "com.ly.train.flower.common.sample.springboot.ServiceB");

        ServiceFlow.buildFlow("async", "serviceE", "serviceB");
    }

    @Override
    public Object process(User message, ServiceContext context) throws Throwable {
        context.getWeb().println("User:" + message.getName());
        return message.getId();
    }
}
