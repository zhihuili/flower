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
package com.ly.train.flower.common.akka;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import com.ly.train.flower.common.akka.actor.ServiceActor;
import com.ly.train.flower.common.akka.actor.SupervisorActor;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

public class ServiceActorFactory {
  private static final Logger logger = LoggerFactory.getLogger(ServiceActorFactory.class);
  private static final Long DEFAULT_TIMEOUT = 5000L;
  private static final Duration timeout = Duration.create(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
  private static final ConcurrentMap<String, ActorRef> flowServiceActorCache = new ConcurrentHashMap<String, ActorRef>();
  private static final int defaultFlowIndex = -1;

  private static ActorSystem actorSystem;
  private static ActorRef supervierActor;
  private static ActorContext actorContext;

  public static synchronized ActorRef buildServiceActor(String flowName, String serviceName) {
    return buildServiceActor(flowName, serviceName, defaultFlowIndex);
  }

  public static synchronized ActorRef buildServiceActor(String flowName, String serviceName, int index) {
    final String cacheKey = flowName + "_" + serviceName + "_" + index;
    ActorRef actorRef = flowServiceActorCache.get(cacheKey);
    if (actorRef != null) {
      return actorRef;
    }
    actorRef = getActorContext().actorOf(ServiceActor.props(flowName, serviceName, index, getActorSystem()), cacheKey);
    logger.info("创建服务{}:{}", cacheKey, actorRef);
    flowServiceActorCache.put(cacheKey, actorRef);
    return actorRef;
  }


  protected static ActorContext getActorContext() {
    if (actorContext == null) {
      synchronized (ServiceActorFactory.class) {
        if (actorContext == null) {
          try {
            supervierActor = getActorSystem().actorOf(SupervisorActor.props(), "flower");
            actorContext = (ActorContext) Await
                .result(Patterns.ask(supervierActor, new SupervisorActor.GetActorContext(), DEFAULT_TIMEOUT - 1), timeout);
          } catch (Exception e) {
            logger.error("", e);
          }
        }
      }
    }
    return actorContext;
  }

  private static ActorSystem getActorSystem() {
    if (actorSystem == null) {
      synchronized (ServiceActorFactory.class) {
        if (actorSystem == null) {
          String name = "LocalFlower";
          Config config = ConfigFactory.parseString("").withFallback(ConfigFactory.load());
          ServiceActorFactory.actorSystem = ActorSystem.create(name, config);
        }
      }
    }
    return actorSystem;
  }

  public static void shutdown() {
    logger.info("akka system terminate, system : {}", actorSystem);
    actorSystem.terminate();
  }
}
