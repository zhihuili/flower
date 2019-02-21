package com.ly.train.flower.common.actor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ServiceActorFactory {
  final static String name = "LocalFlower";
  final static ActorSystem system = ActorSystem.create(name);
  final static int defaultFlowIndex = -1;
  static ActorRef supervisorActorRef = system.actorOf(Props.create(SupervisorActor.class), "supervisor");
  static scala.concurrent.duration.Duration timeout =
      scala.concurrent.duration.Duration.create(5, SECONDS);
  public static Map<String, ActorRef> map = new ConcurrentHashMap<String, ActorRef>();
  static LoggingAdapter log = Logging.getLogger(system, name);

  public static synchronized ActorRef buildServiceActor(String flowName, String serviceName) {
    return buildServiceActor(flowName, serviceName, defaultFlowIndex);
  }

  public static synchronized ActorRef buildServiceActor(String flowName, String serviceName,
      int index) {
    ActorRef actor = map.get(flowName + serviceName + index);
    if (actor != null) {
      return actor;
    }
    try {
      actor =
          (ActorRef) Await.result(Patterns.ask(supervisorActorRef, Props.create(ServiceActor.class, flowName, serviceName,index,system), 5000), timeout);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    map.put(flowName + serviceName + index, actor);
    return actor;
  }

  public static void shutdown() {
    system.terminate();
  }
}
