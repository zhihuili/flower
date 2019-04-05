package com.ly.train.flower.common.sample.aggregate.service;

import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.annotation.FlowerType;
import com.ly.train.flower.common.service.Complete;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.web.Flush;

import java.util.Set;

/**
 * @author: fengyu.zhang
 */
@FlowerService(type = FlowerType.AGGREGATE)
public class ServiceReceiveAB implements Service<Object,Void>, Flush, Complete {
    @Override
    public Void process(Object message, ServiceContext context) throws Throwable {
        System.out.println("处理B分叉之后的聚合消息:");
        Set<Integer> set = (Set<Integer>)message;
        for(Integer integer:set){
            System.out.println(integer);
        }
        return null;
    }
}
