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
package com.ly.train.flower.core.akka;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;

/**
 * @author leeyazhou
 */
public class BlockedThreadChecker {

  public static class Task {
    final long startTime = System.currentTimeMillis();
    final long maxExecTime;

    public Task(long maxExecTime) {
      this.maxExecTime = maxExecTime;
    }
  }

  private static final Logger log = LoggerFactory.getLogger(BlockedThreadChecker.class);

  private final Map<Thread, Task> threads = new WeakHashMap<>();
  private final Timer timer;
  private final long interval;
  private final long warningExceptionTime;

  BlockedThreadChecker(long intervals, long warningExceptionTimes) {
    this.interval = intervals == 0L ? 1000 : intervals;
    this.warningExceptionTime = warningExceptionTimes == 0L ? 5000 : warningExceptionTimes;
    this.timer = new Timer("flower-blocked-thread-checker", true);
    this.timer.schedule(new TimerTask() {
      @Override
      public void run() {
        synchronized (BlockedThreadChecker.this) {
          long now = System.currentTimeMillis();
          for (Map.Entry<Thread, Task> entry : threads.entrySet()) {
            long execStart = entry.getValue().startTime;
            long dur = now - execStart;
            final long timeLimit = entry.getValue().maxExecTime;
            if (execStart != 0 && dur >= timeLimit) {
              final String message =
                  String.format("Thread %s has been blocked for %s ms, time limit is %s ms", entry, dur, timeLimit);
              if (dur <= warningExceptionTime) {
                log.warn(message);
              } else {
                FlowerException stackTrace = new FlowerException("Thread blocked");
                stackTrace.setStackTrace(entry.getKey().getStackTrace());
                log.warn(message, stackTrace);
              }
            }
          }
        }
      }
    }, interval, interval);
  }

  public synchronized void registerThread(Thread thread, Task checked) {
    this.threads.put(thread, checked);
  }

  public synchronized void unregisterThread(Thread thread) {
    this.threads.remove(thread);
  }

  public void close() {
    timer.cancel();
  }

}
