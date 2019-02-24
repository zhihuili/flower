package com.ly.train.flower.common.sample.web;

import java.io.IOException;
import java.io.InputStream;

import com.ly.train.flower.common.sample.web.mode.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class SessionFactory {

  private static SqlSession session;
  static {
    try {
      new SessionFactory();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private SessionFactory() throws IOException {
    String resource = "mybatis-config.xml";
    InputStream inputStream = Resources.getResourceAsStream(resource);
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    session = sqlSessionFactory.openSession();
  }

  /**
   * exception when high concurrency
   * @deprecated
   * @return
   */
  public static SqlSession getSession() {
    return session;
  }

  public static void main(String[] args) throws IOException {
    User user = SessionFactory.getSession().selectOne("listUsers", 1);
    System.out.println(user.getName());
  }
}