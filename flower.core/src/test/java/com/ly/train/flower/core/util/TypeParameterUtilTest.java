/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.core.util;

import java.util.concurrent.CompletableFuture;
import org.junit.Test;
import com.ly.train.flower.base.model.User;
import com.ly.train.flower.common.util.Pair;
import com.ly.train.flower.core.service.Service;
import com.ly.train.flower.core.service.container.ServiceContext;
import com.ly.train.flower.core.service.impl.AbstractService;
import com.ly.train.flower.core.util.TypeParameterUtil;

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
