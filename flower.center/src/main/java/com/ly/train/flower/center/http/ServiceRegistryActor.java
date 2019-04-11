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

import com.ly.train.flower.center.http.ServiceRegistryMessages.ShowServices;
import com.ly.train.flower.registry.config.ServiceInfo;
import akka.actor.AbstractActor;
import akka.actor.Props;

public class ServiceRegistryActor extends AbstractActor {

  static Props props() {
    return Props.create(ServiceRegistryActor.class);
  }


  @Override
  public Receive createReceive() {
    return receiveBuilder().match(ShowServices.class, ss -> {
      getSender().tell(new ServiceInfo(), getSelf());
    }).build();
  }
}
