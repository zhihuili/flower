package com.ly.train.flower.common.sample.web.dao;

import com.ly.train.flower.common.sample.web.mode.Order;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 14:25
 */
@MapperScan
public interface OrderDao {
    List<Order> findByCustomerId(@Param("customerId") int customerId);
}
