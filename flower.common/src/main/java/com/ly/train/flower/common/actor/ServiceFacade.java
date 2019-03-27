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
package com.ly.train.flower.common.actor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.servlet.AsyncContext;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.container.ServiceFlow;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class ServiceFacade {
  private static final Logger logger = LoggerFactory.getLogger(ServiceFacade.class);
  private static final Map<String, ServiceRouter> mapRouter = new ConcurrentHashMap<String, ServiceRouter>();

  // TODO user define duration
  static FiniteDuration duration = Duration.create(3, TimeUnit.SECONDS);

  public static void asyncCallService(String flowName, String serviceName, Object message, AsyncContext ctx) throws IOException {
    ServiceContext context = ServiceContext.context(message, ctx);
    context.setFlowName(flowName);
    serviceName = ServiceFlow.getOrCreate(flowName).getHeadServiceConfig().getServiceName();
    ServiceActorFactory.buildServiceActor(flowName, serviceName).tell(context, ActorRef.noSender());
  }

  public static void asyncCallService(String flowName, String serviceName, Object message) throws IOException {
    asyncCallService(flowName, serviceName, message, null);
  }


  /**
   * 同步调用会引起阻塞，因此需要在外面try catch异常TimeoutException
   * 
   * @param flowName
   * @param serviceName
   * @param message
   * @return
   * @throws Exception
   */
  public static Object syncCallService(String flowName, String serviceName, Object message) throws Exception {
    ServiceContext context = ServiceContext.context(message);
    context.setSync(true);
    serviceName = ServiceFlow.getOrCreate(flowName).getHeadServiceConfig().getServiceName();
    return Await.result(Patterns.ask(ServiceActorFactory.buildServiceActor(flowName, serviceName), context, new Timeout(duration)),
        duration);
  }

  /**
   * will cache by flowName + "_" + serviceName
   * 
   * @param flowName
   * @param serviceName
   * @param flowNumbe 数量
   * @return
   */
  public static ServiceRouter buildServiceRouter(String flowName, String serviceName, int flowNumbe) {
    serviceName = ServiceFlow.getOrCreate(flowName).getHeadServiceConfig().getServiceName();
    final String routerName = flowName + "_" + serviceName;

    ServiceRouter serviceRouter = mapRouter.get(routerName);
    if (serviceRouter == null) {
      serviceRouter = new ServiceRouter(flowName, serviceName, flowNumbe);
      mapRouter.put(routerName, serviceRouter);
      logger.info("build service Router. flowName : {}, serviceName : {}, flowNumbe : {}", flowName, serviceName, flowNumbe);
    }
    return serviceRouter;
  }

  public static void shutdown() {
    ServiceActorFactory.shutdown();
    logger.info("shutdown.");
  }
}
