package com.ly.train.flower.common.sample.web.sync;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.common.sample.web.User;
import com.ly.train.flower.common.sample.web.service.UserServiceImpl;

public class SyncServlet extends HttpServlet {
  ApplicationContext context;

  @Override
  public void init() {
    context = new ClassPathXmlApplicationContext( "spring-mybatis.xml");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("text/html;charset=UTF-8");
    int id = Integer.valueOf(req.getParameter("id").toString());
    
    UserServiceImpl userService = (UserServiceImpl) context.getBean("userService");
    User user = userService.searchUser(id);
    
    String result = JSONObject.toJSONString(user);
    PrintWriter out = resp.getWriter();
    out.println(result);
    out.flush();
  }
}
