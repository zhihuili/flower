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
