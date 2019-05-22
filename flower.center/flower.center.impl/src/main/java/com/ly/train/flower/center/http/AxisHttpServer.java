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

  private ActorSystem system;


  public AxisHttpServer(ActorSystem system) {
    this.system = system;
  }

  public void startHttpServer() throws Exception {
    final Http http = Http.get(system);
    final ActorMaterializer materializer = ActorMaterializer.create(system);
    ActorRef userRegistryActor = system.actorOf(ServiceRegistryActor.props(), "serviceRegistryActor");


    ServiceRoutes serviceRoutes = new ServiceRoutes(system, userRegistryActor);

    final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = serviceRoutes.routes().flow(system, materializer);
    final CompletionStage<ServerBinding> binding =
        http.bindAndHandle(routeFlow, ConnectHttp.toHost("localhost", 8096), materializer);
    System.out.println(binding);
    System.out.println("Server online at http://localhost:8096/");
    System.in.read(); // let it run until user presses return
  }

}
