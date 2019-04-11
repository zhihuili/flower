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
/**
 * 
 */
package com.ly.train.flower.common.service.impl;

import com.ly.train.flower.common.exception.ExceptionHandler;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public abstract class AbstractService<P, R> implements Service<P, R> {
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public R process(P message, ServiceContext context) throws Throwable {
    try {
      return doProcess(message, context);
    } catch (Throwable throwable) {
      onError(throwable, message);
    }
    return null;
  }

  /**
   * 请求参数
   * 
   * @param throwable 异常堆栈
   * @param param 请求参数
   */
  public void onError(Throwable throwable, P param) {
    logger.error("fail to invoke doProcess method.", throwable);
    ExceptionHandler.handle(throwable);
  }

  /**
   * 流程处理
   * 
   * @param message 参数
   * @param context 上下文
   * @return 返回结果
   * @throws any exception
   */
  public abstract R doProcess(P message, ServiceContext context) throws Throwable;
}
