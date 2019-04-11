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
package com.ly.train.flower.common.sample.programflow.service;

import com.ly.train.flower.common.sample.programflow.model.MessageA;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;

public class ServiceB implements Service<String, MessageA> {

  int i = 0;

  /**
   * upper case service
   */
  @Override
  public MessageA process(String message, ServiceContext context) {
    if (message != null && message instanceof String) {
      MessageA ma = new MessageA();
      ma.setI(i++);
      ma.setS(((String) message).toUpperCase());
      return ma;
    }
    return null;
  }
}
