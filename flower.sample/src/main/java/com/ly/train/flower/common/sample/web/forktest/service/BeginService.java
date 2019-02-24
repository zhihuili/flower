package com.ly.train.flower.common.sample.web.forktest.service;

import com.ly.train.flower.common.service.HttpService;
import com.ly.train.flower.common.service.web.Web;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 14:33
 */
public class BeginService implements HttpService<String> {

    @Override
    public Object process(String message, Web web) throws Exception {
        Integer result = Integer.valueOf(web.getParameter("id"));
        return result;
    }
}
