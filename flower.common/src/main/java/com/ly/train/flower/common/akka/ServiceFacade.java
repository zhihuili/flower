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
import java.util.concurrent.TimeoutException;
import javax.servlet.AsyncContext;
import com.ly.train.flower.common.akka.router.FlowRouter;
import com.ly.train.flower.common.akka.router.ServiceRouter;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

public class ServiceFacade {
  protected static final Logger logger = LoggerFactory.getLogger(ServiceFacade.class);

  private final ActorFactory actorFactory;

  public ServiceFacade(FlowerFactory flowerFactory) {
    this.actorFactory = flowerFactory.getActorFactory();
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
   * @throws TimeoutException
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

}
