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
package com.ly.train.flower.ddd.gateway.impl;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.ly.train.flower.core.akka.router.FlowRouter;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.ddd.factory.DDDFactory;
import com.ly.train.flower.ddd.gateway.QueryGateway;
import com.ly.train.flower.ddd.service.DDDEndService;
import com.ly.train.flower.ddd.service.DDDStartService;
import com.ly.train.flower.ddd.service.EventHandlerService;
import com.ly.train.flower.ddd.service.QueryHandlerService;

/**
 * @author leeyazhou
 */
public class DefaultQueryGateway implements QueryGateway, InitializingBean {
  private final String flowName = "dddQueryGatewayFlow";
  private final FlowerFactory flowerFactory;
  private FlowRouter flowRouter;

  public DefaultQueryGateway(FlowerFactory flowerFactory, DDDFactory dddFactory) {
    this.flowerFactory = flowerFactory;
  }

  @Override
  public <Q> void query(Q query) {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    AsyncContext asyncContext = request == null ? null : request.startAsync();
    this.flowRouter.asyncCallService(query, asyncContext);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    initFlowerRouter();
  }

  void initFlowerRouter() {
    if (flowRouter == null) {
      synchronized (flowName) {
        if (flowRouter == null) {
          this.flowerFactory.getServiceFactory().getOrCreateServiceFlow(flowName)
              .buildFlow(DDDStartService.class, QueryHandlerService.class)
              .buildFlow(QueryHandlerService.class, EventHandlerService.class)
              .buildFlow(EventHandlerService.class, DDDEndService.class).build();
          this.flowRouter = this.flowerFactory.getActorFactory().buildFlowRouter(flowName, 0);
        }
      }
    }

  }

}
