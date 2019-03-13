/**
 * Copyright © ${project.inceptionYear} 同程艺龙 (zhihui.li@ly.com)
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
    } else if (message instanceof ActorRef) {
      getContext().watch((ActorRef)message);
    } else if (message instanceof String){
      if ("getContext".equals(message)) {
        getSender().tell(getContext(), getSelf());
      }
    } else {
      unhandled(message);
    }
  }

  @Override
  public SupervisorStrategy supervisorStrategy() {
    return strategy;
  }
}
