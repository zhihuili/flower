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
package com.ly.flower.center.http;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.PatternsCS;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;

public class ServiceRoutes extends AllDirectives {
	final private ActorSystem system;
	final private ActorRef serviceRegistryActor;
	final private LoggingAdapter log;

	public ServiceRoutes(ActorSystem system, ActorRef serviceRegistryActor) {
		this.system = system;
		this.serviceRegistryActor = serviceRegistryActor;
		log = Logging.getLogger(system, this);
	}

	Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));

	public Route routes() {
		return route(pathPrefix("services", () -> route(getServices())));
	}

	private Route getServices() {
		return pathEnd(() -> route(get(() -> {
			CompletionStage<ServiceRegistryActor.ServiceInfo> futureServices = PatternsCS
					.ask(serviceRegistryActor, new ServiceRegistryMessages.ShowServices(), timeout)
					.thenApply(obj -> (ServiceRegistryActor.ServiceInfo) obj);
			return onSuccess(() -> futureServices, services -> complete(StatusCodes.OK, services, Jackson.marshaller()));
		})));
	}
}
