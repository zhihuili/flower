package com.ly.train.flower.common.sample.web.forktest.service;

import com.ly.train.flower.common.sample.web.dao.UserDao;
import com.ly.train.flower.common.sample.web.mode.User;
import com.ly.train.flower.common.service.HttpService;
import com.ly.train.flower.common.service.web.Web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 17:15
 */

@Service("UserService")
public class UserService implements HttpService<Integer> {
    @Autowired
    private UserDao userDao;

    @Override
    public Object process(Integer message, Web web) throws Exception {
        User user = userDao.findUser(message);
        return user == null ? new User():user;
    }
}
