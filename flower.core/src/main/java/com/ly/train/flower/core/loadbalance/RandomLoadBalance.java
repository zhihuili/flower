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
package com.ly.train.flower.core.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import com.ly.train.flower.core.akka.actor.wrapper.ActorWrapper;
import com.ly.train.flower.core.service.container.ServiceContext;

/**
 * @author leeyazhou
 * 
 */
public class RandomLoadBalance extends AbstractLoadBalance {
  private static final String name = "RandomLoadBalance";
  private ThreadLocalRandom random = ThreadLocalRandom.current();

  @Override
  public ActorWrapper doChooseOne(List<ActorWrapper> actorRefs, ServiceContext serviceContext) {
    int index = random.nextInt(actorRefs.size());
    return actorRefs.get(index);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RandomLoadBalance [name=");
    builder.append(name);
    builder.append("]");
    return builder.toString();
  }

}
