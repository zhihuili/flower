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
package com.ly.train.flower.common.akka.actor.wrapper;

import com.ly.train.flower.common.service.container.ServiceContext;
import akka.actor.ActorRef;

/**
 * @author leeyazhou
 * 
 */
public interface ActorWrapper {

  String getServiceName();

  /**
   * 发送消息
   * 
   * @param message 消息
   */
  void tell(ServiceContext message);

  /**
   * 发送消息
   * 
   * @param message 消息
   * @param sender 发送方
   */
  void tell(ServiceContext message, ActorRef sender);
}
