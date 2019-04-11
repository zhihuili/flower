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
package com.ly.train.flower.common.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lee
 */
public class AtomicPositiveInteger extends Number {

  private static final long serialVersionUID = 4447595464037944629L;
  private final AtomicInteger ai;

  public AtomicPositiveInteger() {
    this.ai = new AtomicInteger();
  }

  public AtomicPositiveInteger(int initialValue) {
    this.ai = new AtomicInteger(initialValue);
  }

  public final int getAndIncrement() {
    for (;;) {
      int current = ai.get();
      int next = (current >= Integer.MAX_VALUE ? 0 : current + 1);
      if (ai.compareAndSet(current, next)) {
        return current;
      }
    }
  }

  public final int getAndDecrement() {
    for (;;) {
      int current = ai.get();
      int next = (current <= 0 ? Integer.MAX_VALUE : current - 1);
      if (ai.compareAndSet(current, next)) {
        return current;
      }
    }
  }

  public final int incrementAndGet() {
    for (;;) {
      int current = ai.get();
      int next = (current >= Integer.MAX_VALUE ? 0 : current + 1);
      if (ai.compareAndSet(current, next)) {
        return next;
      }
    }
  }

  public final int decrementAndGet() {
    for (;;) {
      int current = ai.get();
      int next = (current <= 0 ? Integer.MAX_VALUE : current - 1);
      if (ai.compareAndSet(current, next)) {
        return next;
      }
    }
  }

  public final int get() {
    return ai.get();
  }

  public final void set(int newValue) {
    if (newValue < 0) {
      throw new IllegalArgumentException("new value " + newValue + " < 0");
    }
    ai.set(newValue);
  }

  public final int getAndSet(int newValue) {
    if (newValue < 0) {
      throw new IllegalArgumentException("new value " + newValue + " < 0");
    }
    return ai.getAndSet(newValue);
  }

  public final int getAndAdd(int delta) {
    if (delta < 0) {
      throw new IllegalArgumentException("delta " + delta + " < 0");
    }
    for (;;) {
      int current = ai.get();
      int next = (current >= Integer.MAX_VALUE - delta + 1 ? delta - 1 : current + delta);
      if (ai.compareAndSet(current, next)) {
        return current;
      }
    }
  }

  public final int addAndGet(int delta) {
    if (delta < 0) {
      throw new IllegalArgumentException("delta " + delta + " < 0");
    }
    for (;;) {
      int current = ai.get();
      int next = (current >= Integer.MAX_VALUE - delta + 1 ? delta - 1 : current + delta);
      if (ai.compareAndSet(current, next)) {
        return next;
      }
    }
  }

  public final boolean compareAndSet(int expect, int update) {
    if (update < 0) {
      throw new IllegalArgumentException("update value " + update + " < 0");
    }
    return ai.compareAndSet(expect, update);
  }

  public final boolean weakCompareAndSet(int expect, int update) {
    if (update < 0) {
      throw new IllegalArgumentException("update value " + update + " < 0");
    }
    return ai.weakCompareAndSet(expect, update);
  }

  public byte byteValue() {
    return ai.byteValue();
  }

  public short shortValue() {
    return ai.shortValue();
  }

  public int intValue() {
    return ai.intValue();
  }

  public long longValue() {
    return ai.longValue();
  }

  public float floatValue() {
    return ai.floatValue();
  }

  public double doubleValue() {
    return ai.doubleValue();
  }

  public String toString() {
    return ai.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((ai == null) ? 0 : ai.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AtomicPositiveInteger other = (AtomicPositiveInteger) obj;
    if (ai == null) {
      if (other.ai != null) {
        return false;
      }
    } else if (!ai.equals(other.ai)) {
      return false;
    }
    return true;
  }

}
