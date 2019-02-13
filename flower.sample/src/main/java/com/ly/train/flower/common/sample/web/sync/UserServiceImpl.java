package com.ly.train.flower.common.sample.web.sync;

import com.ly.train.flower.common.sample.web.User;

public class UserServiceImpl {

  private UserDaoImpl userDao = new UserDaoImpl();

  public User searchUser(int id) {
    return userDao.findUser(id);
  }

}
