package com.ly.train.flower.common.sample.web.async;

import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.common.sample.web.SessionFactory;
import com.ly.train.flower.common.sample.web.User;
import com.ly.train.flower.common.service.HttpService;
import com.ly.train.flower.common.service.web.Complete;
import com.ly.train.flower.common.service.web.Flush;
import com.ly.train.flower.common.service.web.Web;

public class ServiceB implements HttpService<Integer>, Flush, Complete {

  @Override
  public Object process(Integer message, Web web) throws Exception {
    User user = SessionFactory.getSession().selectOne("findUser", message);
    String result = JSONObject.toJSONString(user);
    web.print(result);
    return null;
  }

}
