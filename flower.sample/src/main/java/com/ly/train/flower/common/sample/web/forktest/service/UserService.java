package com.ly.train.flower.common.sample.web.forktest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ly.train.flower.common.sample.web.dao.UserDao;
import com.ly.train.flower.common.sample.web.mode.User;
import com.ly.train.flower.common.service.containe.ServiceContext;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 17:15
 */

@Service("UserService")
public class UserService implements com.ly.train.flower.common.service.Service<Integer> {
    @Autowired
    private UserDao userDao;

    @Override
    public Object process(Integer message, ServiceContext context) throws Exception {
        User user = userDao.findUser(message);
        return user == null ? new User():user;
    }
}
