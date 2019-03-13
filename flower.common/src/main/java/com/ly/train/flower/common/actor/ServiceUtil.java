/**
 * Copyright © ${project.inceptionYear} 同程艺龙 (zhihui.li@ly.com)
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
package com.ly.train.flower.common.actor;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.AsyncContext;
import com.ly.train.flower.common.service.container.FlowContext;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.web.Web;

public class ServiceUtil {

  public static FlowMessage buildFlowMessage(Object o) {
    FlowMessage flowMessage = new FlowMessage();
    flowMessage.setTransactionId(UUID.randomUUID().toString());
    flowMessage.setMessage(o);
    return flowMessage;
  }

  public static void makeWebContext(FlowMessage flowMessage, AsyncContext ctx) throws IOException {
    ServiceContext serviceContext = new ServiceContext();
    if (ctx != null) {
      Web web = new Web(ctx);
      serviceContext.setWeb(web);
    }
    FlowContext.putServiceContext(flowMessage.getTransactionId(), serviceContext);
  }

}
