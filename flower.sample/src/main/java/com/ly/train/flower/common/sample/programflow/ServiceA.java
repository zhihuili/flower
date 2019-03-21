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
package com.ly.train.flower.common.sample.programflow;

import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;

public class ServiceA implements Service<String,String> {

  private ClassA ca;

  public ServiceA() {
    this.ca = new ClassA();
  }

  @Override
  /**
   * trim service
   */
  public String process(String message, ServiceContext context) {
    ca.f();
    if (message != null && message instanceof String) {
      return ((String) message).trim();
    }
    return "";
  }

}
