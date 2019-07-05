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
package com.ly.train.flower.common.sample.web;

import com.ly.train.flower.core.service.Complete;
import com.ly.train.flower.core.service.Service;
import com.ly.train.flower.core.service.container.ServiceContext;
import com.ly.train.flower.core.service.web.Flush;
import com.ly.train.flower.core.service.web.HttpComplete;

public class FlowService implements Service, HttpComplete, Flush, Complete {

  public FlowService() {}

  @Override
  /**
   * trim service
   */
  public Object process(Object message, ServiceContext context) throws Exception {

    context.getWeb().println(" - end:" + System.currentTimeMillis());
    return "";
  }

  public long delay() {
    return 100;
  }

}
