package com.ly.train.flower.common.sample.web.dao;

import org.mybatis.spring.annotation.MapperScan;

import com.ly.train.flower.common.sample.web.mode.User;

@MapperScan
public interface UserDao {
  User findUser(int id);
}
