package com.ly.train.flower.common.sample.web.forktest.service;

import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.common.service.HttpService;
import com.ly.train.flower.common.service.web.Complete;
import com.ly.train.flower.common.service.web.Flush;
import com.ly.train.flower.common.service.web.Web;

import java.util.Set;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 13:23
 */
public class ReturnService implements HttpService<Set>, Flush, Complete {

    @Override
    public Object process(Set message, Web web) throws Exception {
        web.print(JSONObject.toJSONString(message));
        return null;
    }
}
