package com.ly.train.flower.ddd.service;

import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.core.service.impl.AbstractService;
import com.ly.train.flower.ddd.config.DDDConfig;
import com.ly.train.flower.ddd.proxy.MethodProxy;

/**
 * @author leeyazhou
 */
public class CommandHandlerService extends AbstractService<Object, Object> {
  private DDDConfig dddConfig;

  public CommandHandlerService(DDDConfig dddConfig) {
    this.dddConfig = dddConfig;
  }
  
  @Override
  public Object doProcess(Object message, ServiceContext context) throws Throwable {
    MethodProxy method = dddConfig.getCommandHandler(message.getClass());
    return method.invoke(message, context);
  }

}
