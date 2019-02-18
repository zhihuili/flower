package com.ly.train.flower.common.sample.web.dao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Service;

import com.ly.train.flower.common.sample.web.User;

@MapperScan
public interface UserDao {
  User findUser(int id);
}
