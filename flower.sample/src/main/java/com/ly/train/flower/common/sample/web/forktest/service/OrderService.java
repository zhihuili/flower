package com.ly.train.flower.common.sample.web.forktest.service;

import com.ly.train.flower.common.sample.web.dao.OrderDao;
import com.ly.train.flower.common.sample.web.mode.Order;
import com.ly.train.flower.common.service.HttpService;
import com.ly.train.flower.common.service.web.Web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 17:16
 */
@Service("OrderService")
public class OrderService implements HttpService<Integer> {
    @Autowired
    private OrderDao orderDao;

    @Override
    public Object process(Integer message, Web web) throws Exception {
        List<Order> orders = orderDao.findByCustomerId(message);
        return orders == null ? new ArrayList<Order>() : orders;
    }
}
