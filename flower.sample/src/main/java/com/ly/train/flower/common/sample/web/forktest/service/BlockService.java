package com.ly.train.flower.common.sample.web.forktest.service;

import com.ly.train.flower.common.sample.web.dao.GoodsDao;
import com.ly.train.flower.common.sample.web.dao.OrderDao;
import com.ly.train.flower.common.sample.web.dao.UserDao;
import com.ly.train.flower.common.sample.web.mode.Order;
import com.ly.train.flower.common.sample.web.mode.User;
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
public class BlockService{
    @Autowired
    private UserDao userDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private OrderDao orderDao;

    public Set<Object> getInfo(Integer userId) {
        User user = userDao.findUser(userId);
        List<Integer> goodsList = goodsDao.findGoodsIdForRecommend(userId);
        List<Order> orders = orderDao.findByCustomerId(userId);
        Set<Object> set = new HashSet<>();
        set.add(user);
        set.add(goodsList);
        set.add(orders);
        return set;
    }
}
