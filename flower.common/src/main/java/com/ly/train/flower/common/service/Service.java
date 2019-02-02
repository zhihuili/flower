package com.ly.train.flower.common.service;

public interface Service<T> extends FlowerService {

  Object process(T message);
}
