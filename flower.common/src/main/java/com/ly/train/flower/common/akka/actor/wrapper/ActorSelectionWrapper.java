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
package com.ly.train.flower.common.akka.actor.wrapper;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;

/**
 * @author leeyazhou
 *
 */
public class ActorSelectionWrapper implements ActorWrapper {

  private final ActorSelection actorSelection;
  private String serviceName;

  public ActorSelectionWrapper(ActorSelection actorSelection) {
    this.actorSelection = actorSelection;
  }

  public ActorSelection getActorSelection() {
    return actorSelection;
  }

  @Override
  public void tell(Object message) {
    actorSelection.tell(message, ActorRef.noSender());
  }

  @Override
  public void tell(Object message, ActorRef sender) {
    actorSelection.tell(message, sender);
  }

  @Override
  public String getServiceName() {
    return serviceName;
  }

  public ActorSelectionWrapper setServiceName(String serviceName) {
    this.serviceName = serviceName;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ActorSelectWrapper [actorSelection=");
    builder.append(actorSelection);
    builder.append(", serviceName=");
    builder.append(serviceName);
    builder.append("]");
    return builder.toString();
  }



}
