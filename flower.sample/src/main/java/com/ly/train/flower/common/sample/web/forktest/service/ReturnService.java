package com.ly.train.flower.common.sample.web.forktest.service;

import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.common.service.containe.ServiceContext;
import com.ly.train.flower.common.service.web.Complete;
import com.ly.train.flower.common.service.web.Flush;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 13:23
 */
public class ReturnService
    implements com.ly.train.flower.common.service.Service<Set>, Flush, Complete {

  @Override
  public Object process(Set message, ServiceContext context) throws Exception {
    context.getWeb().print(JSONObject.toJSONString(message));
    return null;
  }
}
