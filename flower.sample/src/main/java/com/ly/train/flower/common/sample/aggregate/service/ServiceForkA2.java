package com.ly.train.flower.common.sample.aggregate.service;

import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;

/**
 * @author: fengyu.zhang
 */
@FlowerService
public class ServiceForkA2 implements Service<Integer, Integer> {
    @Override
    public Integer process(Integer message, ServiceContext context) throws Throwable {
        Integer result = message+2;
        System.out.println("ForkA2:已处理,结果:"+result);
        return result;
    }
}
