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
package com.ly.train.flower.common.core.service;

/**
 * 
 * @author leeyazhou
 * 
 * @param <P> 方法参数
 * @param <R> 方法处理结果
 */
public interface Service<P, R> extends FlowerService {

  /**
   * 服务处理
   * 
   * @param message 信息
   * @param context 服务上下文
   * @return 结果
   * @throws Throwable exception
   */
  R process(P message, ServiceContext context) throws Throwable;
}
