package com.ly.train.flower.springboot.service;

import java.util.List;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.annotation.FlowerType;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.core.service.impl.AbstractService;

/**
 * @author leeyazhou
 */
@FlowerService(type = FlowerType.AGGREGATE)
public class AggregateUserService extends AbstractService<List<Object>, List<Object>>{

  @Override
  public List<Object> doProcess(List<Object> message, ServiceContext context) throws Throwable {
    return message;
  }

}
