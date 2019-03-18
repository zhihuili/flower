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

import static java.util.concurrent.TimeUnit.SECONDS;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.AsyncContext;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class ServiceFacade {
  private static final Logger logger = LoggerFactory.getLogger(ServiceFacade.class);
  public static Map<String, ServiceRouter> mapRouter =
      new ConcurrentHashMap<String, ServiceRouter>();

  // TODO user define duration
  public static FiniteDuration duration = Duration.create(3, SECONDS);

  public static void asyncCallService(String flowName, String serviceName, Object o,
      AsyncContext ctx) throws IOException {
    FlowMessage flowMessage = ServiceUtil.buildFlowMessage(o);
    ServiceUtil.makeWebContext(flowMessage, ctx);
    ServiceActorFactory.buildServiceActor(flowName, serviceName).tell(flowMessage, null);
  }

  public static void asyncCallService(String flowName, String serviceName, Object o)
      throws IOException {
    asyncCallService(flowName, serviceName, o, null);
  }

  /*
   * syncCallService 同步调用会引起阻塞，因此需要在外面try catch异常TimeoutException
   */
  public static Object syncCallService(String flowName, String serviceName, Object o)
      throws Exception {
    FlowMessage flowMessage = ServiceUtil.buildFlowMessage(o);
    ServiceUtil.makeWebContext(flowMessage, null);
    return Await.result(Patterns.ask(ServiceActorFactory.buildServiceActor(flowName, serviceName),
        flowMessage, new Timeout(duration)), duration);
  }

  public static ServiceRouter buildServiceRouter(String flowName, String serviceName,
      int flowNumber) {
    String routerName = flowName + serviceName;
    ServiceRouter serviceRouter = mapRouter.get(routerName);
    if (serviceRouter == null) {
      serviceRouter = new ServiceRouter(flowName, serviceName, flowNumber);
      mapRouter.put(routerName, serviceRouter);
      logger.info("build service Router. flowName : {}, serviceName : {}, flowNumber : {}",
          flowName, serviceName, flowNumber);
    }
    return serviceRouter;
  }

  public static void shutdown() {
    ServiceActorFactory.shutdown();
    logger.info("shutdown.");
  }
}
