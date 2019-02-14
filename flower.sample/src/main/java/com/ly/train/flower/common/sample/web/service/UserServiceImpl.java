package com.ly.train.flower.common.sample.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ly.train.flower.common.sample.web.User;
import com.ly.train.flower.common.sample.web.dao.UserDao;

@Service("userService")
public class UserServiceImpl {

  @Autowired
  private UserDao userDao;;

  public User searchUser(int id) {
    return userDao.findUser(id);
  }

}
