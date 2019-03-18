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
package com.ly.train.flower.common.sample.akka;

import java.time.Duration;
import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;

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
