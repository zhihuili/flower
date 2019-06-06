package com.ly.train.flower.common.util;

import java.util.concurrent.CompletableFuture;
import org.junit.Test;
import com.ly.train.flower.base.model.User;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.impl.AbstractService;

/**
 * @author leeyazhou
 */
public class TypeParameterUtilTest {

  @Test
  public void testInterface() {
    Pair<Class<?>, Class<?>> type = TypeParameterUtil.getServiceClassParam(DemoInterface.class);
    org.junit.Assert.assertEquals(type.getKey(), String.class);
    org.junit.Assert.assertEquals(type.getValue(), String.class);
  }

  @Test
  public void testSuperClass() {
    Pair<Class<?>, Class<?>> type = TypeParameterUtil.getServiceClassParam(DemoExtend.class);
    org.junit.Assert.assertEquals(type.getKey(), String.class);
    org.junit.Assert.assertEquals(type.getValue(), User.class);
  }

  @Test
  public void testCompletableFuture() {
    Pair<Class<?>, Class<?>> type = TypeParameterUtil.getServiceClassParam(DemoExtendCompletableFuture.class);
    org.junit.Assert.assertEquals(type.getKey(), String.class);
    org.junit.Assert.assertEquals(type.getValue(), User.class);
  }

  class DemoInterface implements Service<String, String> {
    @Override
    public String process(String message, ServiceContext context) throws Throwable {
      return null;
    }
  }
  class DemoExtend extends AbstractService<String, User> {
    @Override
    public User doProcess(String message, ServiceContext context) throws Throwable {
      return null;
    }
  }
  class DemoExtendCompletableFuture extends AbstractService<String, CompletableFuture<User>> {
    @Override
    public CompletableFuture<User> doProcess(String message, ServiceContext context) throws Throwable {
      return null;
    }
  }
}
