package com.ly.train.flower.common.sample.aggregate.service;

import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.annotation.FlowerType;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;

import java.util.List;

/**
 * 本Service为分叉集合之后的节点，用于处理服务分叉聚合之后的消息，需注解 @FlowerService(type = FlowerType.AGGREGATE)
 * @author: fengyu.zhang
 */
@FlowerService(type = FlowerType.AGGREGATE)
public class ServiceReceiveA implements Service<List<Object>,Integer> {
    /**
     * 用于处理服务分叉聚合之后的消息
     * @param message 需要处理的消息，这里使用List接收,
     *                默认返回的是List<Object> {@link com.ly.train.flower.common.service.impl.AggregateService#process(Object, ServiceContext)}
     *                也可以使用Object接收
     * @param context 服务上下文
     */
    @Override
    public Integer process(List<Object> message, ServiceContext context) throws Throwable {
        System.out.println("处理A分叉之后的聚合消息:");
        Integer sum = 0;
        if(null != message && message.size()>0) {
            for (Object obj : message) {
                if (obj instanceof Integer) {
                    sum += (Integer) obj;
                }
            }
            System.out.println("求和："+sum+"开始B分叉处理");
            return sum;
        }else {
            System.out.println("空聚合消息");
            return null;
        }

    }
}
