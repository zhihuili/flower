package com.ly.train.flower.common.actor;

import akka.pattern.Patterns;
import akka.util.Timeout;
import com.ly.train.flower.common.service.message.FlowMessage;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ServiceFacade {

  public static Map<String, ServiceRouter> mapRouter = new ConcurrentHashMap<String, ServiceRouter>();

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
    }
    return serviceRouter;
  }

  public static void shutdown() {
    ServiceActorFactory.shutdown();
  }
}