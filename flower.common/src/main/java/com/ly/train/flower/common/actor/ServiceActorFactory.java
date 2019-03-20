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
import java.util.concurrent.TimeUnit;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import akka.actor.AbstractActor;
import akka.actor.AbstractActor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

public class ServiceActorFactory {
  final static String name = "LocalFlower";
  final static Config config = ConfigFactory.parseString("").withFallback(ConfigFactory.load());
  final static ActorSystem system = ActorSystem.create(name, config);
  final static ActorRef _supervisorActorRef = system.actorOf(Props.create(SupervisorActor.class), "supervisor");

  static AbstractActor.ActorContext supervisorActorContext = null;
  final static int defaultFlowIndex = -1;
  static Duration timeout = Duration.create(5, TimeUnit.SECONDS);
  public static Map<String, ActorRef> map = new ConcurrentHashMap<String, ActorRef>();
  static LoggingAdapter log = Logging.getLogger(system, name);
  static {
    try {
      supervisorActorContext =
          (ActorContext) Await.result(Patterns.ask(_supervisorActorRef, "getContext", 5000), timeout);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  public static synchronized ActorRef buildServiceActor(String flowName, String serviceName) {
    return buildServiceActor(flowName, serviceName, defaultFlowIndex);
  }

  public static synchronized ActorRef buildServiceActor(String flowName, String serviceName, int index) {
    final String cacheKey = flowName + serviceName + index;
    ActorRef actor = map.get(cacheKey);
    if (actor != null) {
      return actor;
    }
    Props props = Props.create(ServiceActor.class, flowName, serviceName, index, system);
    actor = supervisorActorContext.actorOf(props);
    map.put(cacheKey, actor);
    return actor;
  }

  public static void shutdown() {
    log.info("akka system terminate, system : {}", system);
    system.terminate();
  }
}
