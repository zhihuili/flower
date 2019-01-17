package com.ly.train.flower.common.service;

public interface Service<T> {

  Object process(T message);
}
