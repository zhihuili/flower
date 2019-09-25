package com.ly.train.flower.ddd.service;

import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.core.service.impl.AbstractService;

/**
 * @author leeyazhou
 */
public class DDDStartService extends AbstractService<Object, Object> {

  @Override
  public Object doProcess(Object message, ServiceContext context) throws Throwable {
    return message;
  }

}
