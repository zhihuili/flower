package com.ly.train.flower.common.sample.aggregate.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ly.train.flower.common.annotation.Flower;
import com.ly.train.flower.common.sample.aggregate.service.ServiceBegin;
import com.ly.train.flower.common.sample.aggregate.service.ServiceForkA1;
import com.ly.train.flower.common.sample.aggregate.service.ServiceForkA2;
import com.ly.train.flower.common.sample.aggregate.service.ServiceForkA3;
import com.ly.train.flower.common.sample.aggregate.service.ServiceForkB1;
import com.ly.train.flower.common.sample.aggregate.service.ServiceForkB2;
import com.ly.train.flower.common.sample.aggregate.service.ServiceReceiveA;
import com.ly.train.flower.common.sample.aggregate.service.ServiceReceiveAB;
import com.ly.train.flower.web.spring.FlowerController;

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
        getServiceFlow().buildFlow(ServiceBegin.class, ServiceForkA1.class);
        getServiceFlow().buildFlow(ServiceBegin.class, ServiceForkA2.class);
        getServiceFlow().buildFlow(ServiceBegin.class, ServiceForkA3.class);
        getServiceFlow().buildFlow(ServiceForkA1.class, ServiceReceiveA.class);
        getServiceFlow().buildFlow(ServiceForkA2.class, ServiceReceiveA.class);
        getServiceFlow().buildFlow(ServiceForkA3.class, ServiceReceiveA.class);
        // 第二个分叉
        getServiceFlow().buildFlow(ServiceReceiveA.class, ServiceForkB1.class);
        getServiceFlow().buildFlow(ServiceReceiveA.class, ServiceForkB2.class);
        getServiceFlow().buildFlow(ServiceReceiveA.class, ServiceReceiveAB.class);
        getServiceFlow().buildFlow(ServiceForkB1.class, ServiceReceiveAB.class);
        getServiceFlow().buildFlow(ServiceForkB2.class, ServiceReceiveAB.class);


    }
}
