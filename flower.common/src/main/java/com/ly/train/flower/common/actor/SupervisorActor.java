/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.common.actor;

import java.util.concurrent.TimeUnit;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

public class SupervisorActor extends AbstractActor {
  private SupervisorStrategy strategy =
      new OneForOneStrategy(10, Duration.create(1, TimeUnit.MINUTES),
          DeciderBuilder.matchAny(o -> SupervisorStrategy.resume()).build());


  @Override
  public Receive createReceive() {
    return receiveBuilder().match(Props.class, message -> {
      ActorRef child = getContext().actorOf((Props) message);
      getSender().tell(child, getSelf());

    }).match(ActorRef.class, message -> {
      getContext().watch((ActorRef) message);
    }).match(String.class, message -> {
      if ("getContext".equals(message)) {
        getSender().tell(getContext(), getSelf());
      }
    }).matchAny(message -> {
      unhandled(message);
    }).build();
  }


  @Override
  public SupervisorStrategy supervisorStrategy() {
    return strategy;
  }
}
