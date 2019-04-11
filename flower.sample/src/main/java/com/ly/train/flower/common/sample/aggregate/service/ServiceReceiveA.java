package com.ly.train.flower.common.sample.aggregate.service;

import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.annotation.FlowerType;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;

import java.util.Iterator;
import java.util.Set;

/**
 * 本Service为分叉集合之后的节点，用于处理服务分叉聚合之后的消息，需注解 @FlowerService(type = FlowerType.AGGREGATE)
 * @author: fengyu.zhang
 */
@FlowerService(type = FlowerType.AGGREGATE)
public class ServiceReceiveA implements Service<Set,Integer> {
    /**
     * 用于处理服务分叉聚合之后的消息
     * @param message 需要处理的消息，这里使用Set接收，也可以使用Object接收
     * @param context 服务上下文
     */
    @Override
    public Integer process(Set message, ServiceContext context) throws Throwable {
        System.out.println("处理A分叉之后的聚合消息:");
        Iterator it = message.iterator();
        Integer sum = 0;
        while (it.hasNext()){
            Integer item = (Integer) it.next();
            sum += item;
            System.out.println(item.toString());
        }
        System.out.println("求和："+sum+"开始B分叉处理");
        return sum;
    }
}
