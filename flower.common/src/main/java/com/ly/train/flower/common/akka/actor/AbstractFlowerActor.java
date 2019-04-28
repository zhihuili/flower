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
package com.ly.train.flower.common.akka.actor;

import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import akka.actor.AbstractActor;

/**
 * @author leeyazhou
 * 
 */
public abstract class AbstractFlowerActor extends AbstractActor {
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public Receive createReceive() {
    return receiveBuilder().match(ServiceContext.class, context -> {
      try {
        onServiceContextReceived(context);
      } catch (Throwable e) {
        onException(e);
      }
    }).matchAny(message -> {
      unhandled(message);
    }).build();
  }

  @Override
  public void postStop() throws Exception {
    super.postStop();
  }

  @Override
  public void preStart() throws Exception {
    super.preStart();
  }

  public abstract void onServiceContextReceived(ServiceContext context) throws Throwable;

  public void onException(Throwable throwable) {
    logger.error("", throwable);
  }

  @Override
  public void unhandled(Object message) {
    super.unhandled(message);
    logger.warn("received unhandled message : {}", message);
  }
}
