package com.ly.flower.center.http;

import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

public class AxisHttpServer {

	ActorSystem system;


	public AxisHttpServer(ActorSystem system) {
		this.system = system;
	}

	public void startHttpServer() throws Exception {
		final Http http = Http.get(system);
		final ActorMaterializer materializer = ActorMaterializer.create(system);
		ActorRef userRegistryActor = system.actorOf(ServiceRegistryActor.props(), "serviceRegistryActor");


		ServiceRoutes serviceRoutes = new ServiceRoutes(system, userRegistryActor);

		final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = serviceRoutes.routes().flow(system, materializer);
		final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
				ConnectHttp.toHost("localhost", 8096), materializer);

		System.out.println("Server online at http://localhost:8096/");
		System.in.read(); // let it run until user presses return
	}

}
