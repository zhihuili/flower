package com.ly.train.flower.common.actor;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SupervisorActor extends UntypedActor {

  private HashMap<ActorRef, CrashActor> crashActorHashMap = new HashMap<>();
  private HashMap<ActorRef, Integer> crashCounts = new HashMap<>();

  private SupervisorStrategy strategy =
      new OneForOneStrategy(
          10,
          Duration.create(1, TimeUnit.MINUTES),
          DeciderBuilder
              .matchAny(o -> {
                ActorRef sender = sender();
                if (crashCounts.containsKey(sender)) {
                  Integer integer = crashCounts.get(sender);
                  System.out.println(sender + " crash:" + integer);
                }
                if (crashActorHashMap.containsKey(sender)) {
                  CrashActor crashActor = crashActorHashMap.get(sender);
                  //TODO 这个地方的方案暂时不对发生异常的Actor进行重新发送消息
                  //crashActor.actor.self().tell(crashActor.message, crashActor.actor.self());
                }
                return SupervisorStrategy.resume();
              })
              .build());

  @Override
  public void onReceive(Object message) throws Throwable {
    if (message instanceof Props) {

      ActorRef child = getContext().actorOf((Props)message);
      getSender().tell(child, getSelf());
    } else if (message instanceof CrashActor) {
      CrashActor crashActor = (CrashActor)message;
      ActorRef actorRef = crashActor.actor.self();
      crashActorHashMap.put(crashActor.actor.self(), crashActor);
      Integer integer = 1;
      if (crashCounts.containsKey(actorRef)) {
        integer = crashCounts.get(actorRef);
        integer += 1;
      }
      crashCounts.put(actorRef, integer);
    }
    else {
      unhandled(message);
    }
  }

  @Override
  public SupervisorStrategy supervisorStrategy() {
    return strategy;
  }
}
