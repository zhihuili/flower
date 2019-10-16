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
package com.ly.train.flower.ddd.config;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;

/**
 * @author leeyazhou
 */
public class AggregateLifecycle {
  static final Logger logger = LoggerFactory.getLogger(AggregateLifecycle.class);
  private static final ThreadLocal<AggregateLifecycle> THREAD_LOCAL = ThreadLocal.withInitial(AggregateLifecycle::new);

  private final List<EventMessage> appliedMessages = new CopyOnWriteArrayList<>();

  private ApplyMore doApply(Object message) {
    this.appliedMessages.add(EventMessage.asEventMessage(message));
    return new ApplyMore() {

      @Override
      public ApplyMore andThenApply(Supplier<?> payloadOrMessageSupplier) {
        appliedMessages.add(EventMessage.asEventMessage(payloadOrMessageSupplier.get()));
        return this;
      }

      @Override
      public ApplyMore andThen(Runnable runnable) {
        runnable.run();
        return this;
      }
    };
  }

  public void startLifecycle() {
    THREAD_LOCAL.set(this);
  }

  public void endLifecycle() {
    this.appliedMessages.clear();
    THREAD_LOCAL.remove();
  }

  public static ApplyMore apply(Object message) {
    return THREAD_LOCAL.get().doApply(message);
  }

  /**
   * @return the appliedMessages
   */
  public List<EventMessage> getAppliedMessages() {
    return appliedMessages;
  }

}
