package com.ly.train.flower.common.sample.web.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.common.sample.web.User;
import com.ly.train.flower.common.sample.web.dao.UserDao;
import com.ly.train.flower.common.service.HttpService;
import com.ly.train.flower.common.service.web.Complete;
import com.ly.train.flower.common.service.web.Flush;
import com.ly.train.flower.common.service.web.Web;

@Service("serviceB")
public class ServiceB implements HttpService<Integer>, Flush, Complete {

  @Autowired
  private UserDao userDao;;

  @Override
  public Object process(Integer message, Web web) throws Exception {
    User user = userDao.findUser(message);
    String result = JSONObject.toJSONString(user);
    web.print(result);
    return null;
  }

}
