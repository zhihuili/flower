package com.ly.train.flower.common.sample.web.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.common.sample.web.dao.UserDao;
import com.ly.train.flower.common.sample.web.mode.User;
import com.ly.train.flower.common.service.Complete;
import com.ly.train.flower.common.service.containe.ServiceContext;
import com.ly.train.flower.common.service.web.Flush;

@Service("serviceB")
public class ServiceB
    implements com.ly.train.flower.common.service.Service<Integer>, Flush, Complete {

  @Autowired
  private UserDao userDao;

  @Override
  public Object process(Integer message, ServiceContext context) throws Exception {
    User user = userDao.findUser(message);
    if (user == null) {
      context.getWeb().complete();
      throw new NullPointerException("user:" + message + " is not found.");
    }
    String result = JSONObject.toJSONString(user);
    context.getWeb().print(result);
    return null;
  }

}
