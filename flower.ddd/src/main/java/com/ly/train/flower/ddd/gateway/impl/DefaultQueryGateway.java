package com.ly.train.flower.ddd.gateway.impl;

import com.ly.train.flower.core.akka.router.FlowRouter;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.ddd.gateway.QueryGateway;
import com.ly.train.flower.ddd.service.CommandHandlerService;
import com.ly.train.flower.ddd.service.DDDStartService;
import com.ly.train.flower.ddd.service.EventHandlerService;

/**
 * @author leeyazhou
 */
public class DefaultQueryGateway implements QueryGateway {
  private final String flowName = "dddQueryGatewayFlow";
  private final FlowerFactory flowerFactory;
  private final FlowRouter flowRouter;

  public DefaultQueryGateway(FlowerFactory flowerFactory) {
    this.flowerFactory = flowerFactory;
    this.flowerFactory.getServiceFactory().getOrCreateServiceFlow(flowName)
        .buildFlow(DDDStartService.class, CommandHandlerService.class)
        .buildFlow(CommandHandlerService.class, EventHandlerService.class).build();
    this.flowRouter = this.flowerFactory.getActorFactory().buildFlowRouter(flowName, 0);
  }

  @Override
  public <Q> void query(Q query) {
    this.flowRouter.asyncCallService(query);
  }

}
