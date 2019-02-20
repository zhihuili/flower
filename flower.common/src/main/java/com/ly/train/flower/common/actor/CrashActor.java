package com.ly.train.flower.common.actor;

import akka.actor.Actor;

public class CrashActor {
  Actor actor = null;
  Object message = null;

  public CrashActor(Actor a, Object o) {
    actor = a;
    message = o;
  }
}
