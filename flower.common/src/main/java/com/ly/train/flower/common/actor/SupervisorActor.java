package com.ly.train.flower.common.actor;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class SupervisorActor extends UntypedActor {
  private SupervisorStrategy strategy =
      new OneForOneStrategy(
          10,
          Duration.create(1, TimeUnit.MINUTES),
          DeciderBuilder
              .matchAny(o -> SupervisorStrategy.resume())
              .build());

  @Override
  public void onReceive(Object message) throws Throwable {
    if (message instanceof Props) {

      ActorRef child = getContext().actorOf((Props)message);
      getSender().tell(child, getSelf());
    } else {
      unhandled(message);
    }
  }

  @Override
  public SupervisorStrategy supervisorStrategy() {
    return strategy;
  }
}
