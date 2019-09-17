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

import com.ly.train.flower.core.akka.mailbox.FlowerMessageQueueSemantics;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.AbstractNodeQueue;
import akka.dispatch.Envelope;
import akka.dispatch.MessageQueue;

/**
 * 
 * @author leeyazhou
 */
public class FlowerUnboundedMessageQueue extends AbstractNodeQueue<Envelope>
    implements MessageQueue, FlowerMessageQueueSemantics {
  private static final long serialVersionUID = 1L;
  protected final ActorRef actorRef;
  protected final ActorSystem actorSystem;

  public FlowerUnboundedMessageQueue(ActorRef actorRef, ActorSystem actorSystem) {
    this.actorRef = actorRef;
    this.actorSystem = actorSystem;
  }

  public void enqueue(ActorRef receiver, Envelope handle) {
    add(handle);
  }

  public Envelope dequeue() {
    return poll();
  }

  public int numberOfMessages() {
    return count();
  }

  public boolean hasMessages() {
    return !isEmpty();
  }

  public void cleanUp(ActorRef owner, MessageQueue deadLetters) {
    Envelope envelope = dequeue();
    if (envelope != null) {
      deadLetters.enqueue(owner, envelope);
      cleanUp(owner, deadLetters);
    }
  }
}
