package com.ly.train.flower.common.sample.web.forktest.service;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.containe.ServiceContext;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 14:33
 */
public class BeginService implements Service<String> {

    @Override
    public Object process(String message, ServiceContext context) throws Exception {
        Integer result = Integer.valueOf(context.getWeb().getParameter("id"));
        return result;
    }
}
