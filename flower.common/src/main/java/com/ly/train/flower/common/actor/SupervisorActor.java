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
import java.util.concurrent.atomic.AtomicInteger;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
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

  private AtomicInteger counter = new AtomicInteger(0);

  @Override
  public Receive createReceive() {
    return receiveBuilder().match(Props.class, propses -> {
      try {
        ActorRef child = getContext().actorOf(propses, "serviceactor_" + counter.incrementAndGet());
        getSender().tell(child, getSelf());
        if (logger.isDebugEnabled()) {
          logger.debug("create child actor : {}", child);
        }
      } catch (Exception e) {
        logger.error("fail to create child actor", e);
      }
    }).matchAny(message -> {
      unhandled(message);
    }).build();
  }


  @Override
  public SupervisorStrategy supervisorStrategy() {
    return DEFAULT_STRATEGY;
  }

  public static Props props() {
    return Props.create(SupervisorActor.class);
  }
}
