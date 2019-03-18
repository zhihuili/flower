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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorContext;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ServiceActorFactory {
  final static String name = "LocalFlower";
  final static ActorSystem system = ActorSystem.create(name);
  final static ActorRef _supervisorActorRef =
      system.actorOf(Props.create(SupervisorActor.class), "supervisor");
  static UntypedActorContext supervisorActorContext = null;
  final static int defaultFlowIndex = -1;
  static Duration timeout = Duration.create(5, SECONDS);
  public static Map<String, ActorRef> map = new ConcurrentHashMap<String, ActorRef>();
  static LoggingAdapter log = Logging.getLogger(system, name);
  static {
    try {
      supervisorActorContext = (UntypedActorContext) Await
          .result(Patterns.ask(_supervisorActorRef, "getContext", 5000), timeout);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  public static synchronized ActorRef buildServiceActor(String flowName, String serviceName) {
    return buildServiceActor(flowName, serviceName, defaultFlowIndex);
  }

  public static synchronized ActorRef buildServiceActor(String flowName, String serviceName,
      int index) {
    ActorRef actor = map.get(flowName + serviceName + index);
    if (actor != null) {
      return actor;
    }
    Props props = Props.create(ServiceActor.class, flowName, serviceName, index, system);
    actor = supervisorActorContext.actorOf(props);
    map.put(flowName + serviceName + index, actor);
    return actor;
  }

  public static void shutdown() {
    log.info("akka system terminate");
    system.terminate();
  }
}
