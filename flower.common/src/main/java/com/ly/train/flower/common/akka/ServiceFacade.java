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
import com.ly.train.flower.common.service.container.ServiceLoader;
import com.ly.train.flower.common.service.container.simple.SimpleFlowerFactory;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

public class ServiceFacade {
  private static final Logger logger = LoggerFactory.getLogger(ServiceFacade.class);

  static {
    ServiceLoader.getInstance();
  }

  /**
   * @deprecated serviceName 不必须，因为可以从流程中获取到首个服务
   */
  @Deprecated
  public static void asyncCallService(String flowName, String serviceName, Object message, AsyncContext ctx) {
    asyncCallService(flowName, message, ctx);
  }

  /**
   * 异步处理服务
   * 
   * @param flowName 流程名称
   * @param message 消息体
   * @param asyncContext 异步处理上下文
   * @throws IOException io exception
   */
  public static void asyncCallService(String flowName, Object message, AsyncContext asyncContext) {
    FlowRouter serviceRouter = buildFlowRouter(flowName, -1);
    serviceRouter.asyncCallService(message, asyncContext);
  }

  /**
   * 
   * @see ServiceFacade#asyncCallService(String,Object,AsyncContext)
   */
  @Deprecated
  public static void asyncCallService(String flowName, String serviceName, Object message) {
    asyncCallService(flowName, message, null);
  }

  /**
   * @see ServiceFacade#asyncCallService(String,Object,AsyncContext)
   */
  public static void asyncCallService(String flowName, Object message) throws IOException {
    asyncCallService(flowName, message, null);
  }


  /**
   * 同步调用会引起阻塞，因此需要在外面try catch异常TimeoutException
   * 
   * @param flowName flowName
   * @param message message
   * @return object
   * @throws Exception
   */
  public static Object syncCallService(String flowName, Object message) throws Exception {
    FlowRouter serviceRouter = buildFlowRouter(flowName, -1);
    return serviceRouter.syncCallService(message);
  }

  /**
   * @deprecated serviceName 不必须，因为可以从流程中获取到首个服务
   */
  @Deprecated
  public static Object syncCallService(String flowName, String serviceName, Object message) throws Exception {
    return syncCallService(flowName, message);
  }


  /**
   * will be cached by flowName + "_" + serviceName
   * 
   * @param flowName flowName
   * @param flowNumbe 数量
   * @return {@link ServiceRouter}
   */
  public static FlowRouter buildFlowRouter(String flowName, int flowNumbe) {
    return SimpleFlowerFactory.get().getServiceActorFactory().buildFlowRouter(flowName, flowNumbe);
  }

  public static ServiceRouter buildServiceRouter(ServiceConfig serviceConfig, int flowNumbe) {
    return SimpleFlowerFactory.get().getServiceActorFactory().buildServiceRouter(serviceConfig, flowNumbe);
  }

  public static void shutdown() {
    SimpleFlowerFactory.get().getServiceActorFactory().shutdown();
    logger.info("shutdown.");
  }
}
