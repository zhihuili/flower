package com.ly.train.flower.common.sample.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ly.train.flower.common.sample.web.mode.Order;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 14:25
 */
public interface OrderDao {
    List<Order> findByCustomerId(@Param("customerId") int customerId);
}
