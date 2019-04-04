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
package com.ly.train.flower.common.akka.actor;

import java.util.concurrent.TimeUnit;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

public class SupervisorActor extends AbstractActor {
  protected static final Logger logger = LoggerFactory.getLogger(SupervisorActor.class);
  private static SupervisorStrategy DEFAULT_STRATEGY = new OneForOneStrategy(10, Duration.create(1, TimeUnit.MINUTES),
      DeciderBuilder.match(ArithmeticException.class, e -> SupervisorStrategy.resume())
          .match(NullPointerException.class, e -> SupervisorStrategy.restart())
          .match(IllegalArgumentException.class, e -> SupervisorStrategy.stop()).matchAny(o -> SupervisorStrategy.resume()).build());

  @Override
  public Receive createReceive() {
    return receiveBuilder().match(GetActorContext.class, msg -> {
      getSender().tell(getContext(), getSelf());
    }).matchAny(message -> {
      unhandled(message);
    }).build();
  }



  @Override
  public void unhandled(Object message) {
    super.unhandled(message);
    if (logger.isWarnEnabled()) {
      logger.warn("unhandled message : {}", message);
    }
  }

  @Override
  public SupervisorStrategy supervisorStrategy() {
    return DEFAULT_STRATEGY;
  }

  public static Props props() {
    return Props.create(SupervisorActor.class);
  }

  public static class GetActorContext {
  }
}
