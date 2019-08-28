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
package com.ly.train.flower.core.akka.mailbox.bounded;

import java.time.Duration;
import com.typesafe.config.Config;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.MailboxType;
import akka.dispatch.MessageQueue;
import akka.dispatch.ProducesMessageQueue;
import akka.dispatch.ProducesPushTimeoutSemanticsMailbox;
import scala.Option;

/**
 * @author leeyazhou
 */
public class FlowerBoundedMailbox
    implements MailboxType, ProducesMessageQueue<FlowerBoundedMessageQueue>, ProducesPushTimeoutSemanticsMailbox {

  private int capacity;
  private Duration pushTimeOut;

  public FlowerBoundedMailbox(ActorSystem.Settings settings, Config config) {
    this(config.getInt("mailbox-capacity"), config.getDuration("mailbox-push-timeout-time"));
  }

  public FlowerBoundedMailbox(int capacity, Duration pushTimeOut) {
    if (capacity < 0)
      throw new IllegalArgumentException("The capacity for BoundedMailbox can not be negative");
    if (pushTimeOut == null)
      throw new IllegalArgumentException("The push time-out for BoundedMailbox can not be null");
    this.capacity = capacity;
    this.pushTimeOut = pushTimeOut;
  }

  @Override
  public MessageQueue create(Option<ActorRef> owner, Option<ActorSystem> system) {
    return new FlowerBoundedMessageQueue(capacity, pushTimeOut);
  }

  @Override
  public scala.concurrent.duration.Duration pushTimeOut() {
    return scala.concurrent.duration.Duration.fromNanos(this.pushTimeOut.toNanos());
  }

}
