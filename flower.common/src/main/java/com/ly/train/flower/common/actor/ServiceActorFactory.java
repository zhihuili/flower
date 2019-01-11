package com.ly.train.flower.common.actor;

import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class ServiceActorFactory {
  final static ActorSystem system = ActorSystem.create("LocalFlower");

  public static Map<String, ActorRef> map = new HashMap<String, ActorRef>();

  public static ActorRef buildServiceActor(String serviceName) throws Exception {
    ActorRef actor = map.get(serviceName);
    if (actor != null) {
      return actor;
    }

    actor = system.actorOf(Props.create(ServiceActor.class, serviceName));
    map.put(serviceName, actor);
    System.out.println(actor);
    return actor;
  }

}
