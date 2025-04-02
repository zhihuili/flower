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
package com.ly.train.flower.core.akka.extension;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.ExtendedActorSystem;
import org.apache.pekko.actor.Extension;

/**
 * @author leeyazhou
 */
public class FlowerExtension implements Extension {
  ActorSystem actorSystem;
  private AtomicInteger counter = new AtomicInteger();

  public FlowerExtension(ExtendedActorSystem system) {
    this.actorSystem = system;
  }

  public void increase() {
    System.err.println(this + "统计邮箱个数：" + counter.incrementAndGet());
  }
}
