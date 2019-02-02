package com.ly.train.flower.common.service;

import com.ly.train.flower.common.service.web.Web;

public interface HttpService<T> extends FlowerService {
  Object process(T message, Web web) throws Exception;
}
