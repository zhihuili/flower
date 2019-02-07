package com.ly.train.flower.common.actor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class ServiceActorFactory {
  final static ActorSystem system = ActorSystem.create("LocalFlower");
  final static int defaultFlowIndex = -1;

  public static Map<String, ActorRef> map = new ConcurrentHashMap<String, ActorRef>();

  public static synchronized ActorRef buildServiceActor(String flowName, String serviceName) {
    return buildServiceActor(flowName, serviceName, defaultFlowIndex);
  }

  public static synchronized ActorRef buildServiceActor(String flowName, String serviceName,
      int index) {
    ActorRef actor = map.get(flowName + serviceName + index);
    if (actor != null) {
      return actor;
    }

    actor = system.actorOf(Props.create(ServiceActor.class, flowName, serviceName,index,system));
    map.put(flowName + serviceName + index, actor);
    return actor;
  }

}
