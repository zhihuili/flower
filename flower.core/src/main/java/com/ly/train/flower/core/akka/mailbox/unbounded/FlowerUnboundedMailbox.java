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
package com.ly.train.flower.core.akka.mailbox.unbounded;

import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.dispatch.MailboxType;
import org.apache.pekko.dispatch.MessageQueue;
import org.apache.pekko.dispatch.ProducesMessageQueue;

import com.typesafe.config.Config;

import scala.Option;

/**
 * @author leeyazhou
 */
public class FlowerUnboundedMailbox implements MailboxType, ProducesMessageQueue<FlowerUnboundedMessageQueue> {

  public FlowerUnboundedMailbox(ActorSystem.Settings settings, Config config) {}

  @Override
  public MessageQueue create(Option<ActorRef> owner, Option<ActorSystem> system) {
    return new FlowerUnboundedMessageQueue(owner.get(), system.get());
  }

}
