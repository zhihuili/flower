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
package com.ly.train.flower.common.akka;

import java.io.IOException;
import javax.servlet.AsyncContext;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.AbstractInit;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.logging.LoggerFactory;
import akka.actor.ActorRef;

/**
 * @author leeyazhou
 *
 */
public class FlowRouter extends AbstractInit {
  static final com.ly.train.flower.logging.Logger logger = LoggerFactory.getLogger(FlowRouter.class);
  private int number = 2 << 6;
  private volatile ServiceRouter serviceRouter;
  private final ServiceConfig headerServiceConfig;
  private final String flowName;
  private ServiceFacade serviceFacade;

  public FlowRouter(String flowName, ServiceConfig headerServiceConfig, int number, FlowerFactory flowerFactory) {
    this.flowName = flowName;
    this.headerServiceConfig = headerServiceConfig;
    this.serviceFacade = flowerFactory.getServiceFacade();
    if (number > 0) {
      this.number = number;
    }
  }

  @Override
  protected void doInit() {
    getServiceRouter();
  }

  public void asyncCallService(Object message) throws IOException {
    asyncCallService(message, null);
  }

  /**
   * 异步调用
   * 
   * @param message
   * @param ctx
   * @throws IOException
   */
  public <T> void asyncCallService(T message, AsyncContext ctx) {
    ServiceContext serviceContext = ServiceContext.context(message, ctx);
    serviceContext.setFlowName(flowName);
    if (StringUtil.isBlank(serviceContext.getCurrentServiceName())) {
      serviceContext.setCurrentServiceName(headerServiceConfig.getServiceName());
    }
    getServiceRouter().asyncCallService(serviceContext, ActorRef.noSender());
  }

  /**
   * 同步调用
   * 
   * @param message message
   * @return obj
   * @throws Exception
   */
  public Object syncCallService(Object message) throws Exception {
    ServiceContext serviceContext = ServiceContext.context(message);
    serviceContext.setFlowName(flowName);
    serviceContext.setCurrentServiceName(headerServiceConfig.getServiceName());
    serviceContext.setSync(true);
    return getServiceRouter().syncCallService(serviceContext);
  }

  public void asyncCallService(ServiceContext serviceContext) {
    getServiceRouter().asyncCallService(serviceContext, ActorRef.noSender());
  }

  private ServiceRouter getServiceRouter() {
    if (serviceRouter == null) {
      synchronized (this) {
        if (serviceRouter == null) {
          this.serviceRouter = serviceFacade.buildServiceRouter(headerServiceConfig, number);
        }
      }
    }
    return serviceRouter;
  }


  public ServiceConfig getServiceConfig() {
    return headerServiceConfig;
  }



}
