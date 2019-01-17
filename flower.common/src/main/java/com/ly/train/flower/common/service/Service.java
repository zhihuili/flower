package com.ly.train.flower.common.service;

import com.ly.train.flower.common.service.message.FlowMessage;

public interface Service<T> {

  Object process(T message);
}
