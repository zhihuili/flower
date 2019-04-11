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
package com.ly.train.flower.center.http;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import com.ly.train.flower.registry.config.ServiceInfo;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;

public class ServiceRoutes extends AllDirectives {
  static final Logger logger = LoggerFactory.getLogger(ServiceRoutes.class);
  final ActorSystem system;
  final ActorRef serviceRegistryActor;

  public ServiceRoutes(ActorSystem system, ActorRef serviceRegistryActor) {
    this.system = system;
    this.serviceRegistryActor = serviceRegistryActor;
  }

  private static Duration duration = Duration.ofSeconds(5);

  public Route routes() {
    return route(pathPrefix("services", () -> route(getServices())));
  }

  private Route getServices() {
    return pathEnd(() -> route(get(() -> {
      CompletionStage<ServiceInfo> futureServices =
          Patterns.ask(serviceRegistryActor, new ServiceRegistryMessages.ShowServices(), duration)
              .thenApply(obj -> (ServiceInfo) obj);
      return onSuccess(() -> futureServices, services -> complete(StatusCodes.OK, services, Jackson.marshaller()));
    })));
  }
}
