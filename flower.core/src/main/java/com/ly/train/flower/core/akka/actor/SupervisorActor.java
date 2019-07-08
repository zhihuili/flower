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
package com.ly.train.flower.core.akka.actor;

import java.util.concurrent.TimeUnit;
import com.ly.train.flower.common.core.config.ServiceConfig;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.core.akka.ActorFactory;
import com.ly.train.flower.core.akka.actor.command.ActorCommand;
import com.ly.train.flower.core.akka.actor.command.ActorContextCommand;
import com.ly.train.flower.core.akka.actor.command.MessageType;
import com.ly.train.flower.core.akka.actor.command.PingCommand;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

public class SupervisorActor extends AbstractFlowerActor {
  private static SupervisorStrategy DEFAULT_STRATEGY = new OneForOneStrategy(10, Duration.create(1, TimeUnit.MINUTES),
      DeciderBuilder.match(ArithmeticException.class, e -> SupervisorStrategy.resume())
          .match(NullPointerException.class, e -> SupervisorStrategy.restart())
          .match(IllegalArgumentException.class, e -> SupervisorStrategy.stop())
          .matchAny(o -> SupervisorStrategy.resume()).build());

  public static Props props(ActorFactory actorFactory) {
    return Props.create(SupervisorActor.class, actorFactory);
  }

  private ActorFactory actorFactory;

  public SupervisorActor(ActorFactory actorFactory) {
    this.actorFactory = actorFactory;
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder().match(ActorContextCommand.class, msg -> {
      getSender().tell(getContext(), getSelf());
    }).match(ActorCommand.class, command -> {
      ServiceConfig serviceConfig = new ServiceConfig();
      serviceConfig.setServiceName(command.getServiceName());
      serviceConfig.setLocal(true);
      actorFactory.buildServiceActor(serviceConfig, command.getIndex());
      command.setMessageType(MessageType.RESPONSE);
      command.setData("PONG");

      getSender().tell(command, getSender());
    }).match(PingCommand.class, ping -> {
      ping.setText("PONG");
      ping.setMessageType(MessageType.RESPONSE);
      getSender().tell(ping, getSender());
    }).matchAny(message -> {
      unhandled(message);
    }).build();
  }

  @Override
  public void onServiceContextReceived(ServiceContext context) throws Throwable {
    // keep empty
  }



  @Override
  public SupervisorStrategy supervisorStrategy() {
    return DEFAULT_STRATEGY;
  }

}
