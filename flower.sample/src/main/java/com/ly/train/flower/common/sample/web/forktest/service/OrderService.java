package com.ly.train.flower.common.sample.web.forktest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ly.train.flower.common.sample.web.dao.OrderDao;
import com.ly.train.flower.common.sample.web.mode.Order;
import com.ly.train.flower.common.service.containe.ServiceContext;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 17:16
 */
@Service("OrderService")
public class OrderService implements com.ly.train.flower.common.service.Service<Integer> {
    @Autowired
    private OrderDao orderDao;

    @Override
    public Object process(Integer message, ServiceContext context) throws Exception {
        List<Order> orders = orderDao.findByCustomerId(message);
        return orders == null ? new ArrayList<Order>() : orders;
    }
}
