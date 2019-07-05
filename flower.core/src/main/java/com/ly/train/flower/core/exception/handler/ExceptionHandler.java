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

import com.ly.train.flower.core.service.container.ServiceContext;

/**
 * @author leeyazhou
 */
public interface ExceptionHandler {

  /**
   * 处理异常
   * 
   * @param context 上下文
   * @param throwable 异常信息
   */
  void handle(ServiceContext context, Throwable throwable);

}
