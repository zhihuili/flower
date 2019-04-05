package com.ly.train.flower.common.sample.aggregate.controller;

import com.ly.flower.web.spring.FlowerController;
import com.ly.train.flower.common.annotation.Flower;
import com.ly.train.flower.common.sample.aggregate.service.*;
import com.ly.train.flower.common.service.container.ServiceFlow;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author: fengyu.zhang
 */
@RestController
@Flower(value = "aggregate",serviceName = "ServiceBegin")
public class AggregateController extends FlowerController {
    @RequestMapping(value = "/test/aggregate/{id}")
    public void process(@PathVariable Integer id, HttpServletRequest req) throws IOException {
        doProcess(id, req);
    }
    @Override
    public void buildFlower() {
        // 第一个分叉
        ServiceFlow.getOrCreate(getFlowName()).buildFlow(ServiceBegin.class, ServiceForkA1.class);
        ServiceFlow.getOrCreate(getFlowName()).buildFlow(ServiceBegin.class, ServiceForkA2.class);
        ServiceFlow.getOrCreate(getFlowName()).buildFlow(ServiceBegin.class, ServiceForkA3.class);
        ServiceFlow.getOrCreate(getFlowName()).buildFlow(ServiceForkA1.class, ServiceReceiveA.class);
        ServiceFlow.getOrCreate(getFlowName()).buildFlow(ServiceForkA2.class, ServiceReceiveA.class);
        ServiceFlow.getOrCreate(getFlowName()).buildFlow(ServiceForkA3.class, ServiceReceiveA.class);
        // 第二个分叉
        ServiceFlow.getOrCreate(getFlowName()).buildFlow(ServiceReceiveA.class, ServiceForkB1.class);
        ServiceFlow.getOrCreate(getFlowName()).buildFlow(ServiceReceiveA.class, ServiceForkB2.class);
        ServiceFlow.getOrCreate(getFlowName()).buildFlow(ServiceReceiveA.class, ServiceReceiveAB.class);
        ServiceFlow.getOrCreate(getFlowName()).buildFlow(ServiceForkB1.class, ServiceReceiveAB.class);
        ServiceFlow.getOrCreate(getFlowName()).buildFlow(ServiceForkB2.class, ServiceReceiveAB.class);


    }
}
