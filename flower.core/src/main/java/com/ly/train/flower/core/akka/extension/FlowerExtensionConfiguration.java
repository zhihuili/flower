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

import org.apache.pekko.actor.AbstractExtensionId;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.ExtendedActorSystem;
import org.apache.pekko.actor.Extension;
import org.apache.pekko.actor.ExtensionId;
import org.apache.pekko.actor.ExtensionIdProvider;

import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;

/**
 * @author leeyazhou
 */
public class FlowerExtensionConfiguration extends AbstractExtensionId<FlowerExtension>
    implements ExtensionIdProvider {
  static final Logger logger = LoggerFactory.getLogger(FlowerExtensionConfiguration.class);
  
  private static final FlowerExtensionConfiguration flowerExtensionConfiguration = new FlowerExtensionConfiguration();

  @Override
  public FlowerExtension createExtension(ExtendedActorSystem system) {
    return new FlowerExtension(system);
  }

  @Override
  public ExtensionId<? extends Extension> lookup() {
    return flowerExtensionConfiguration;
  }

  public static FlowerExtension getFlowerExtension(ActorSystem system) {
    return flowerExtensionConfiguration.get(system);
  }

}
