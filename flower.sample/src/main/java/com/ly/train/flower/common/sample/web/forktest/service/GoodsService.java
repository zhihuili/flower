package com.ly.train.flower.common.sample.web.forktest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ly.train.flower.common.sample.web.dao.GoodsDao;
import com.ly.train.flower.common.service.containe.ServiceContext;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 17:16
 */
@Service("GoodsService")
public class GoodsService implements com.ly.train.flower.common.service.Service<Integer> {
    @Autowired
    private GoodsDao goodsDao;

    @Override
    public Object process(Integer message, ServiceContext context) throws Exception {
        List<Integer> list = goodsDao.findGoodsIdForRecommend(message);
        return list == null? new ArrayList<Integer>():list;
    }
}
