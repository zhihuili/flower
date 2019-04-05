package com.ly.train.flower.common.sample.aggregate.service;

import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;

/**
 * @author: fengyu.zhang
 */
@FlowerService
public class ServiceBegin implements Service<Integer, Integer> {
    @Override
    public Integer process(Integer message, ServiceContext context) throws Throwable {
        System.out.println("分叉处理，消息值："+message.toString());
        return message;
    }
}
