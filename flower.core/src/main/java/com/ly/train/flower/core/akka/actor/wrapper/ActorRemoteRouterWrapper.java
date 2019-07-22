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

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.ly.train.flower.common.core.message.Message;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.exception.FlowException;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.common.util.URL;
import akka.actor.ActorRef;

/**
 * @author leeyazhou
 * 
 */
public class ActorRemoteRouterWrapper implements ActorWrapper {
  static final Logger logger = LoggerFactory.getLogger(ActorRemoteRouterWrapper.class);
  private volatile AtomicInteger index = null;
  private final ConcurrentMap<String, ActorRemoteWrapper> actorRemoteWrappers = new ConcurrentHashMap<>();
  private String serviceName;

  public ActorRemoteRouterWrapper(ActorRemoteWrapper actorRef) {
    addActorRemoteWrapper(actorRef);

  }

  public ActorRemoteRouterWrapper(List<ActorWrapper> actorRemoteWrappers) {
    for (ActorWrapper item : actorRemoteWrappers) {
      addActorRemoteWrapper((ActorRemoteWrapper) item);
    }
  }

  private void addActorRemoteWrapper(ActorRemoteWrapper actorRef) {
    final String cacheKey = actorRef.getUrl().getHost() + ":" + actorRef.getUrl().getPort();
    actorRemoteWrappers.putIfAbsent(cacheKey, actorRef);
  }


  public ConcurrentMap<String, ActorRemoteWrapper> getActorRemoteWrappers() {
    return actorRemoteWrappers;
  }

  /**
   * WARN: DO NOT CALL THIS
   * 
   * @see #chooseOne(ServiceContext)
   */
  @Override
  public void tell(Message message) {
    throw new FlowException("not support");
    // tell(message, ActorRef.noSender());
  }

  /**
   * WARN: DO NOT CALL THIS
   * 
   * @see #chooseOne(ServiceContext)
   */
  @Override
  public void tell(Message message, ActorRef sender) {
    throw new FlowException("not support");
    // chooseOne().tell(message, sender);
  }

  public ActorWrapper chooseOne(ServiceContext serviceContext) {
    int size = actorRemoteWrappers.size();
    if (size == 1) {
      return actorRemoteWrappers.values().iterator().next();
    }

    Object urlObj = serviceContext.getAttachment(Constant.ServiceContextOriginURL);
    URL url = null;
    if (urlObj != null && urlObj instanceof URL) {
      url = (URL) urlObj;
    }
    ActorWrapper actorWrapper = null;
    if (url != null) {
      actorWrapper = actorRemoteWrappers.get(url.getHost() + ":" + url.getPort());
      if (actorWrapper != null) {
        return actorWrapper;
      }
    }


    if (index == null) {
      int randomIndex = new Random().nextInt(100);
      index = new AtomicInteger(randomIndex);
    }
    return (ActorWrapper) actorRemoteWrappers.values().toArray()[index.incrementAndGet() / size];
  }

  @Override
  public String getServiceName() {
    return serviceName;
  }

  public ActorRemoteRouterWrapper setServiceName(String serviceName) {
    this.serviceName = serviceName;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ActorRemoteRouterWrapper [serviceName=");
    builder.append(serviceName);
    builder.append(", actorRemoteWrappers=");
    builder.append(actorRemoteWrappers);
    builder.append("]");
    return builder.toString();
  }



}
