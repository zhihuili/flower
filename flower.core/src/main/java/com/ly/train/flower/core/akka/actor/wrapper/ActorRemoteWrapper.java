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
package com.ly.train.flower.core.akka.actor.wrapper;

import java.util.concurrent.atomic.AtomicInteger;
import com.ly.train.flower.common.core.message.Message;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import akka.actor.ActorRef;

/**
 * @author leeyazhou
 * 
 */
public class ActorRemoteWrapper implements ActorWrapper {
  private static final Logger logger = LoggerFactory.getLogger(ActorRemoteWrapper.class);
  private AtomicInteger index = new AtomicInteger(0);
  private final ActorRef actorRef;
  private String serviceName;

  public ActorRemoteWrapper(ActorRef actorRef) {
    this.actorRef = actorRef;
  }

  /**
   * @return the actorRef
   */
  public ActorRef getActorRef() {
    return actorRef;
  }

  @Override
  public void tell(Message message) {
    tell(message, ActorRef.noSender());
  }

  @Override
  public void tell(Message message, ActorRef sender) {
    if (logger.isDebugEnabled()) {
      logger.debug("Remote message. serviceName : {}, actor : {}, message : {}, sender : {}", serviceName, actorRef,
          message, sender);
    }
    if (sender == null) {
      sender = ActorRef.noSender();
    }
    actorRef.tell(message, sender);
  }


  @Override
  public String getServiceName() {
    return serviceName;
  }

  public ActorRemoteWrapper setServiceName(String serviceName) {
    this.serviceName = serviceName;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ActorSelectWrapper [actorRef=");
    builder.append(actorRef);
    builder.append(", serviceName=");
    builder.append(serviceName);
    builder.append("]");
    return builder.toString();
  }



}
