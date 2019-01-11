package com.ly.train.flower.common.actor;

import static java.util.concurrent.TimeUnit.SECONDS;

import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class ServiceFacade {
  private static FiniteDuration duration = Duration.create(3, SECONDS);

  public static void asyncCallService(String serviceName, Object o) throws Exception {
    ServiceActorFactory.buildServiceActor(serviceName).tell(o, null);
  }

  public static Object syncCallService(String serviceName, Object o) throws Exception {
    return Await.result(
        Patterns.ask(ServiceActorFactory.buildServiceActor(serviceName), o, new Timeout(duration)),
        duration);
  }
}
