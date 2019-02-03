package com.ly.train.flower.common.actor;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.AsyncContext;

import com.ly.train.flower.common.service.containe.FlowContext;
import com.ly.train.flower.common.service.containe.ServiceContext;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.web.Web;

import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class ServiceFacade {
  // TODO user define
  public static FiniteDuration duration = Duration.create(300, SECONDS);

  public static void asyncCallService(String flowName, String serviceName, Object o,
      AsyncContext ctx) throws IOException {
    FlowMessage flowMessage = buildFlowMessage(o);
    ServiceContext serviceContext = new ServiceContext();
    Web web = new Web(ctx);
    serviceContext.setWeb(web);
    FlowContext.putServiceContext(flowMessage.getTransactionId(), serviceContext);
    ServiceActorFactory.buildServiceActor(flowName, serviceName).tell(flowMessage, null);

  }

  public static void asyncCallService(String flowName, String serviceName, Object o)
      throws IOException {
    asyncCallService(flowName, serviceName, o, null);
  }

  public static Object syncCallService(String flowName, String serviceName, Object o)
      throws Exception {
    FlowMessage flowMessage = buildFlowMessage(o);
    return Await.result(Patterns.ask(ServiceActorFactory.buildServiceActor(flowName, serviceName),
        flowMessage, new Timeout(duration)), duration);
  }

  public static ServiceRouter buildServiceRouter(String flowName, String serviceName,
      int flowNumber) {
    ServiceRouter serviceRouter = new ServiceRouter(flowName, serviceName, flowNumber);

    return serviceRouter;
  }

  private static FlowMessage buildFlowMessage(Object o) {
    FlowMessage flowMessage = new FlowMessage();
    flowMessage.setTransactionId(UUID.randomUUID().toString());
    flowMessage.setMessage(o);
    return flowMessage;
  }
}