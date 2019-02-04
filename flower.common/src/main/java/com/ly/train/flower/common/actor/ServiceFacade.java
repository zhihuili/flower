package com.ly.train.flower.common.actor;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;

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
    FlowMessage flowMessage = ServiceUtil.buildFlowMessage(o);
    ServiceUtil.makeWebContext(flowMessage, ctx);
    ServiceActorFactory.buildServiceActor(flowName, serviceName).tell(flowMessage, null);

  }

  public static void asyncCallService(String flowName, String serviceName, Object o)
      throws IOException {
    asyncCallService(flowName, serviceName, o, null);
  }

  public static Object syncCallService(String flowName, String serviceName, Object o)
      throws Exception {
    FlowMessage flowMessage = ServiceUtil.buildFlowMessage(o);
    return Await.result(Patterns.ask(ServiceActorFactory.buildServiceActor(flowName, serviceName),
        flowMessage, new Timeout(duration)), duration);
  }

  public static ServiceRouter buildServiceRouter(String flowName, String serviceName,
      int flowNumber) {
    ServiceRouter serviceRouter = new ServiceRouter(flowName, serviceName, flowNumber);

    return serviceRouter;
  }
}