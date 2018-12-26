package com.ly.train.flower.common.actor;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class ServiceActorFactory {
  final static ActorSystem system = ActorSystem.create("LocalFlower");
  private static FiniteDuration duration = Duration.create(3, SECONDS);

  public static Map<String, ActorRef> map = new HashMap<String, ActorRef>();

  public static ActorRef buildServiceActor(String serviceName) throws Exception {
    ActorRef actor = map.get(serviceName);
    if (actor != null) {
      return actor;
    }

    actor = system.actorOf(Props.create(ServiceActor.class, serviceName));
    return actor;
  }

}
