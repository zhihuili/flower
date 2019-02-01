package com.ly.train.flower.common.actor;

import static java.util.concurrent.TimeUnit.SECONDS;

import akka.pattern.Patterns;
import akka.util.Timeout;
import com.ly.train.flower.common.service.message.FlowMessage;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class ServiceFacade {
  // TODO user define
  public static FiniteDuration duration = Duration.create(300, SECONDS);

  public static void asyncCallService(String flowName, String serviceName, Object o) {
    FlowMessage flowMessage = new FlowMessage();
    flowMessage.setMessage(o);
    ServiceActorFactory.buildServiceActor(flowName, serviceName).tell(flowMessage, null);
  }

  public static Object syncCallService(String flowName, String serviceName, Object o)
      throws Exception {
    FlowMessage flowMessage = new FlowMessage();
    flowMessage.setMessage(o);
    return Await.result(Patterns.ask(ServiceActorFactory.buildServiceActor(flowName, serviceName),
        flowMessage, new Timeout(duration)), duration);
  }

  public static ServiceRouter buildServiceRouter(String flowName, String serviceName,
      int flowNumber) {
    ServiceRouter serviceRouter = new ServiceRouter(flowName, serviceName, flowNumber);

    return serviceRouter;
  }
}