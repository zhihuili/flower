package com.ly.train.flower.ddd.gateway.impl;

import com.ly.train.flower.core.akka.router.FlowRouter;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.ddd.gateway.CommandGateway;
import com.ly.train.flower.ddd.service.CommandHandlerService;
import com.ly.train.flower.ddd.service.DDDStartService;
import com.ly.train.flower.ddd.service.EventHandlerService;

/**
 * @author leeyazhou
 */
public class DefaultCommandGateway implements CommandGateway {
  private final String flowName = "dddCommandGatewayFlow";
  private final FlowerFactory flowerFactory;
  private final FlowRouter flowRouter;

  public DefaultCommandGateway(FlowerFactory flowerFactory) {
    this.flowerFactory = flowerFactory;
    this.flowerFactory.getServiceFactory().getOrCreateServiceFlow(flowName)
        .buildFlow(DDDStartService.class, CommandHandlerService.class)
        .buildFlow(CommandHandlerService.class, EventHandlerService.class).build();
    this.flowRouter = this.flowerFactory.getActorFactory().buildFlowRouter(flowName, 0);
  }

  @Override
  public <C> void send(C command) {
    this.flowRouter.asyncCallService(command);
  }

}
