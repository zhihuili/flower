package com.ly.train.flower.common.sample.akka;

import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;

import java.time.Duration;

public class Supervisor extends AbstractActor {
  private static SupervisorStrategy strategy =
      new OneForOneStrategy(
          10,
          Duration.ofMinutes(1),
          DeciderBuilder
              .matchAny(o -> {
                return SupervisorStrategy.resume();
              })
              .build());

  @Override
  public SupervisorStrategy supervisorStrategy() {
    return strategy;
  }


  @Override
  public Receive createReceive() {

    return receiveBuilder()
        .match(
            Props.class,
            props -> {
              getSender().tell(getContext().actorOf(props), getSelf());
            })
        .build();
  }
}
