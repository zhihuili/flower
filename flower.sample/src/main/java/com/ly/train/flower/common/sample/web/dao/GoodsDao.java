package com.ly.train.flower.common.sample.web.dao;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 14:25
 */
@MapperScan
public interface GoodsDao {
    List<Integer> findGoodsIdForRecommend(@Param("customerId") int customerId);
}
