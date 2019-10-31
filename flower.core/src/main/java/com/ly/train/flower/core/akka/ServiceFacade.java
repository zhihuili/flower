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
package com.ly.train.flower.core.akka;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javax.servlet.AsyncContext;
import com.ly.train.flower.common.core.config.ServiceConfig;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.core.akka.router.FlowRouter;
import com.ly.train.flower.core.akka.router.ServiceRouter;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.core.service.container.ServiceFactory;
import com.ly.train.flower.core.service.container.util.ServiceContextUtil;

public class ServiceFacade {
  protected static final Logger logger = LoggerFactory.getLogger(ServiceFacade.class);

  private final ActorFactory actorFactory;
  private final ServiceFactory serviceFactory;
  private static final String buildInFlowName = "build_in_flow_and_do_not_use_it";

  public ServiceFacade(FlowerFactory flowerFactory) {
    this.actorFactory = flowerFactory.getActorFactory();
    this.serviceFactory = flowerFactory.getServiceFactory();
  }


  /**
   * 异步处理服务
   * 
   * @param flowName 流程名称
   * @param message 消息体
   * @param asyncContext 异步处理上下文
   * @throws IOException io exception
   */
  public void asyncCallService(String flowName, Object message, AsyncContext asyncContext) {
    FlowRouter serviceRouter = buildFlowRouter(flowName, -1);
    serviceRouter.asyncCallService(message, asyncContext);
  }


  /**
   * @see ServiceFacade#asyncCallService(String,Object,AsyncContext)
   */
  public void asyncCallService(String flowName, Object message) throws IOException {
    asyncCallService(flowName, message, null);
  }


  /**
   * 同步调用会引起阻塞，因此需要在外面try catch异常TimeoutException
   * 
   * @param flowName flowName
   * @param message message
   * @return object
   * @throws TimeoutException ex
   */
  public Object syncCallService(String flowName, Object message) throws TimeoutException {
    FlowRouter flowRouter = buildFlowRouter(flowName, -1);
    return flowRouter.syncCallService(message);
  }


  /**
   * will be cached by flowName + "_" + serviceName
   * 
   * @param flowName flowName
   * @param actorNumber 数量
   * @return {@link ServiceRouter}
   */
  public FlowRouter buildFlowRouter(String flowName, int actorNumber) {
    return actorFactory.buildFlowRouter(flowName, actorNumber);
  }

  public ServiceRouter buildServiceRouter(ServiceConfig serviceConfig, int actorNumber) {
    return actorFactory.buildServiceRouter(serviceConfig, actorNumber);
  }

  /**
   * 异步调用单个服务
   * 
   * @param serviceName 服务名称
   * @param message 消息体
   */
  public void asyncCall(String serviceName, Object message) {
    ServiceConfig serviceConfig =
        serviceFactory.getOrCreateServiceFlow(buildInFlowName).getOrCreateServiceConfig(serviceName);
    ServiceContext serviceContext = ServiceContextUtil.context(message);
    serviceContext.setCurrentServiceName(serviceName);
    buildServiceRouter(serviceConfig, 0).asyncCallService(serviceContext);
  }

  /**
   * 同步调用单个服务
   * 
   * @param serviceName 服务名称
   * @param message 消息体
   * @return 结果
   * @throws TimeoutException {@link TimeoutException}
   */
  public Object syncCall(String serviceName, Object message) throws TimeoutException {
    ServiceConfig serviceConfig =
        serviceFactory.getOrCreateServiceFlow(buildInFlowName).getOrCreateServiceConfig(serviceName);
    ServiceContext serviceContext = ServiceContextUtil.context(message);
    serviceContext.setCurrentServiceName(serviceName);
    return buildServiceRouter(serviceConfig, 0).syncCallService(serviceContext);
  }

}
