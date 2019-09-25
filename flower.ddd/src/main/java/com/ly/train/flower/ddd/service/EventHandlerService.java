package com.ly.train.flower.ddd.service;

import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.core.service.impl.AbstractService;
import com.ly.train.flower.ddd.config.DDDConfig;
import com.ly.train.flower.ddd.proxy.MethodProxy;

/**
 * @author leeyazhou
 */
public class EventHandlerService extends AbstractService<Object, Object> {

  private DDDConfig dddConfig;

  public EventHandlerService(DDDConfig dddConfig) {
    this.dddConfig = dddConfig;
  }

  @Override
  public Object doProcess(Object message, ServiceContext context) throws Throwable {
    MethodProxy method = dddConfig.getEventHandler(message.getClass());
    return method.invoke(message, context);
  }

}
