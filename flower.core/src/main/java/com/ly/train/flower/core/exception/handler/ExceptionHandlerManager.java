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
package com.ly.train.flower.core.exception.handler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.ly.train.flower.common.util.Assert;

/**
 * @author leeyazhou
 */
public class ExceptionHandlerManager {
  private static final ExceptionHandlerManager instance = new ExceptionHandlerManager();

  private ExceptionHandlerManager() {}

  private final ConcurrentMap<Class<?>, ExceptionHandler> exceptionHandlers =
      new ConcurrentHashMap<Class<?>, ExceptionHandler>();

  private ExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();

  public void registerHandler(Class<?> exceptionClass, ExceptionHandler exceptionHandler) {
    exceptionHandlers.putIfAbsent(exceptionClass, exceptionHandler);
  }

  public ExceptionHandler getExceptionHandler(Class<?> exceptionClass) {
    return exceptionHandlers.getOrDefault(exceptionClass, defaultExceptionHandler);
  }

  /**
   * 设置默认异常处理器
   * 
   * @param defaultExceptionHandler {@link DefaultExceptionHandler}不能为null
   */
  public void setDefaultExceptionHandler(ExceptionHandler defaultExceptionHandler) {
    Assert.notNull(defaultExceptionHandler, "default exception handler can't be null.");
    this.defaultExceptionHandler = defaultExceptionHandler;
  }

  public static ExceptionHandlerManager getInstance() {
    return instance;
  }

}
