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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.pekko.dispatch.BoundedQueueBasedMessageQueue;
import org.apache.pekko.dispatch.Envelope;

import com.ly.train.flower.core.akka.mailbox.FlowerMessageQueueSemantics;

/**
 * @author leeyazhou
 */
public class FlowerBoundedMessageQueue extends LinkedBlockingQueue<Envelope>
    implements BoundedQueueBasedMessageQueue, FlowerMessageQueueSemantics {
  private static final long serialVersionUID = 1L;
  private Duration pushTimeOut;

  public FlowerBoundedMessageQueue(int capacity, Duration pushTimeOut) {
    super(capacity);
    this.pushTimeOut = pushTimeOut;
  }

  @Override
  public scala.concurrent.duration.Duration pushTimeOut() {
    return scala.concurrent.duration.Duration.fromNanos(this.pushTimeOut.toNanos());
  }

  @Override
  public BlockingQueue<Envelope> queue() {
    return this;
  }


}
