package com.ly.train.flower.common.sample.akka;

import akka.actor.AbstractActor;
import scala.Option;

import java.util.Optional;

class Child extends AbstractActor {
  int state = 0;

  @Override
  public Receive createReceive() {

    return receiveBuilder()
        .match(
            Exception.class,
            exception -> {
              throw exception;
            })
        .match(Integer.class, i -> {
          state = i;
          System.out.println(i);
        })
        .matchEquals("get", s -> getSender().tell(state, getSelf()))
        .build();
  }
}