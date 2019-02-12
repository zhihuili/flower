package com.ly.train.flower.common.sample.web.sync;

import com.ly.train.flower.common.sample.web.SessionFactory;
import com.ly.train.flower.common.sample.web.User;

public class UserDaoImpl {

  public User findUser(int id) {
    return SessionFactory.getSession().selectOne("findUser", id);
  }
}
