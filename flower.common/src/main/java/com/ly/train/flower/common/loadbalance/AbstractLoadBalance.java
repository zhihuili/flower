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
package com.ly.train.flower.common.loadbalance;

import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import akka.actor.ActorRef;

/**
 * @author leeyazhou
 *
 */
public abstract class AbstractLoadBalance implements LoadBalance {
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public ActorRef choose(ActorRef[] actorRefs, ServiceContext serviceContext) {
    if (actorRefs == null || actorRefs.length == 0) {
      return null;
    }
    if (actorRefs.length == 1) {
      return actorRefs[0];
    }
    return doChooseOne(actorRefs, serviceContext);
  }

  public abstract ActorRef doChooseOne(ActorRef[] actorRefs, ServiceContext serviceContext);

}
