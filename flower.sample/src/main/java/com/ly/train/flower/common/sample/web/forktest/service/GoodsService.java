package com.ly.train.flower.common.sample.web.forktest.service;

import com.ly.train.flower.common.sample.web.dao.GoodsDao;
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
@Service("GoodsService")
public class GoodsService implements HttpService<Integer> {
    @Autowired
    private GoodsDao goodsDao;

    @Override
    public Object process(Integer message, Web web) throws Exception {
        List<Integer> list = goodsDao.findGoodsIdForRecommend(message);
        return list == null? new ArrayList<Integer>():list;
    }
}
