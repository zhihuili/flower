package com.ly.train.flower.common.sample.web;

import java.util.ArrayList;
import java.util.List;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

public class RouterActor extends AbstractActor {

  Router router;

  {
    List<Routee> routees = new ArrayList<Routee>();
    for (int i = 0; i < 400; i++) {
      ActorRef r = getContext().actorOf(Props.create(MyActor.class));
      getContext().watch(r);
      routees.add(new ActorRefRoutee(r));
    }
    router = new Router(new RoundRobinRoutingLogic(), routees);
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(
            String.class,
            message -> {
              router.route(message, getSender());
            })
        .match(
            Terminated.class,
            message -> {
              router = router.removeRoutee(message.actor());
              ActorRef r = getContext().actorOf(Props.create(MyActor.class));
              getContext().watch(r);
              router = router.addRoutee(new ActorRefRoutee(r));
            })
        .build();
  }
}