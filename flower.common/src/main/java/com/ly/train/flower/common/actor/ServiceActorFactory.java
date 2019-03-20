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
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class ServiceActorFactory {
  private static final Logger logger = LoggerFactory.getLogger(ServiceActorFactory.class);
  private static ActorSystem actorSystem;
  private final static int defaultFlowIndex = -1;
  private static Map<String, ActorRef> actorRefCache = new ConcurrentHashMap<String, ActorRef>();

  public static synchronized ActorRef buildServiceActor(String flowName, String serviceName) {
    return buildServiceActor(flowName, serviceName, defaultFlowIndex);
  }

  public static synchronized ActorRef buildServiceActor(String flowName, String serviceName, int index) {
    final String cacheKey = flowName + "_" + serviceName + "_" + index;
    ActorRef actorRef = actorRefCache.get(cacheKey);
    if (actorRef != null) {
      return actorRef;
    }
    actorRef = getActorSystem().actorOf(ServiceActor.props(flowName, serviceName, index, getActorSystem()));
    actorRefCache.put(cacheKey, actorRef);
    return actorRef;
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
