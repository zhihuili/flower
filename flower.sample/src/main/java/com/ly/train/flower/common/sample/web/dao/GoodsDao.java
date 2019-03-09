package com.ly.train.flower.common.sample.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 14:25
 */
public interface GoodsDao {
    List<Integer> findGoodsIdForRecommend(@Param("customerId") int customerId);
}
