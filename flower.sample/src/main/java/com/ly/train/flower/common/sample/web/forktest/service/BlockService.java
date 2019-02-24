package com.ly.train.flower.common.sample.web.forktest.service;

import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.common.sample.web.dao.GoodsDao;
import com.ly.train.flower.common.sample.web.dao.OrderDao;
import com.ly.train.flower.common.sample.web.dao.UserDao;
import com.ly.train.flower.common.sample.web.mode.Order;
import com.ly.train.flower.common.sample.web.mode.User;
import com.ly.train.flower.common.service.HttpService;
import com.ly.train.flower.common.service.web.Web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 15:47
 */

@Service("BlockService")
public class BlockService implements HttpService<Integer> {
    @Autowired
    private UserDao userDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private OrderDao orderDao;

    @Override
    public Object process(Integer message, Web web) throws Exception {
        User user = userDao.findUser(message);
        List<Integer> goodsList = goodsDao.findGoodsIdForRecommend(message);
        List<Order> orders = orderDao.findByCustomerId(message);
        Set<Object> set = new HashSet<>();
        set.add(user);
        set.add(goodsList);
        set.add(orders);
        return set;
    }
}
