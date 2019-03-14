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
package com.ly.train.flower.common.sample.web;

import com.ly.train.flower.common.service.container.FlowContext;
import com.ly.train.flower.common.service.web.Web;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class MyActor extends AbstractActor {
  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(
            String.class,
            s -> {
              log.info("Received String message: {}", s);
              Thread.sleep(100);
              Web web = FlowContext.getServiceContext(s).getWeb();
              web.println("Hello，MyActor");
              web.flush();
              web.complete();
              
              // #my-actor
              // #reply
//              getSender().tell(s, getSelf());
              // #reply
              // #my-actor
            })
        .matchAny(o -> log.info("received unknown message"))
        .build();
  }
}