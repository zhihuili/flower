package com.ly.train.flower.ddd.factory;

import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.core.service.container.ServiceFactory;
import com.ly.train.flower.ddd.config.DDDConfig;
import com.ly.train.flower.ddd.gateway.CommandGateway;
import com.ly.train.flower.ddd.gateway.QueryGateway;
import com.ly.train.flower.ddd.gateway.impl.DefaultCommandGateway;
import com.ly.train.flower.ddd.gateway.impl.DefaultQueryGateway;
import com.ly.train.flower.ddd.service.CommandHandlerService;
import com.ly.train.flower.ddd.service.DDDStartService;
import com.ly.train.flower.ddd.service.EventHandlerService;

/**
 * @author leeyazhou
 */
public class DDDFactory {

  private DDDConfig dddConfig;
  private CommandGateway commandGateway;
  private QueryGateway queryGateway;
  private FlowerFactory flowerFactory;

  public DDDFactory(FlowerFactory flowerFactory, String basePackage) {
    this.flowerFactory = flowerFactory;
    this.dddConfig = new DDDConfig();
    this.dddConfig.scan(basePackage);
    loadInternalDDDServices();
    this.commandGateway = new DefaultCommandGateway(flowerFactory);
    this.queryGateway = new DefaultQueryGateway(flowerFactory);

  }

  /**
   * load internal ddd services
   */
  private void loadInternalDDDServices() {
    ServiceFactory serviceFactory = flowerFactory.getServiceFactory();
    serviceFactory.registerService(DDDStartService.class.getSimpleName(), DDDStartService.class);
    serviceFactory.registerService(CommandHandlerService.class.getSimpleName(), CommandHandlerService.class);
    serviceFactory.registerService(EventHandlerService.class.getSimpleName(), EventHandlerService.class);

    final CommandHandlerService commandHandlerService = new CommandHandlerService(this.dddConfig);
    final EventHandlerService eventHandlerService = new EventHandlerService(this.dddConfig);
    serviceFactory.registerFlowerService(CommandHandlerService.class.getSimpleName(), commandHandlerService);
    serviceFactory.registerFlowerService(EventHandlerService.class.getSimpleName(), eventHandlerService);
  }

  public CommandGateway getCommandGateway() {
    return commandGateway;
  }

  public QueryGateway getQueryGateway() {
    return queryGateway;
  }

}
