package com.ly.train.flower.common.service;

import com.ly.train.flower.common.service.containe.ServiceContext;

public interface Service<T> extends FlowerService {

  Object process(T message, ServiceContext context) throws Throwable;
}
