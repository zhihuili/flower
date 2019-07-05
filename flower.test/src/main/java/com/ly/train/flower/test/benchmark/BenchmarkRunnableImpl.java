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
package com.ly.train.flower.test.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.core.akka.router.FlowRouter;

public class BenchmarkRunnableImpl extends BenchmarkRunnable {
  private static final Logger logger = LoggerFactory.getLogger(BenchmarkRunnableImpl.class);
  private CountDownLatch countDownLatch;
  private FlowRouter flowRouter;
  private boolean running = true;
  private long endTime;
  private long acceptRequest;
  private long errorRequest;
  private int index;
  private long[] responseSpreads = new long[9];

  private String message;

  public BenchmarkRunnableImpl(CountDownLatch countDownLatch, FlowRouter flowRouter, long endTime, int index) {
    this.flowRouter = flowRouter;
    this.countDownLatch = countDownLatch;
    this.endTime = endTime;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String getServiceName() {
    return "BeanchmarkThread-" + index;
  }

  @Override
  public void doService() {
    while (running) {
      long start = System.currentTimeMillis();
      if (start > endTime) {
        running = false;
        break;
      }
      try {
        String flag = (String) flowRouter.syncCallService(message);
        if (logger.isInfoEnabled()) {
          logger.info(" result : " + flag);
        }
      } catch (Exception e) {
        logger.error("", e);
        errorRequest++;
      }
      sumResponseTimeSpread(System.currentTimeMillis() - start);
    }
    countDownLatch.countDown();
  }

  private void sumResponseTimeSpread(long responseTime) {
    acceptRequest++;
    if (responseTime <= 0) {
      responseSpreads[0] = responseSpreads[0] + 1;
    } else if (responseTime > 0 && responseTime <= 1) {
      responseSpreads[1] = responseSpreads[1] + 1;
    } else if (responseTime > 1 && responseTime <= 5) {
      responseSpreads[2] = responseSpreads[2] + 1;
    } else if (responseTime > 5 && responseTime <= 10) {
      responseSpreads[3] = responseSpreads[3] + 1;
    } else if (responseTime > 10 && responseTime <= 50) {
      responseSpreads[4] = responseSpreads[4] + 1;
    } else if (responseTime > 50 && responseTime <= 100) {
      responseSpreads[5] = responseSpreads[5] + 1;
    } else if (responseTime > 100 && responseTime <= 500) {
      responseSpreads[6] = responseSpreads[6] + 1;
    } else if (responseTime > 500 && responseTime <= 1000) {
      responseSpreads[7] = responseSpreads[7] + 1;
    } else if (responseTime > 1000) {
      responseSpreads[8] = responseSpreads[8] + 1;
    }
  }

  @Override
  public List<long[]> getResult() {
    List<long[]> result = new ArrayList<long[]>(6);
    result.add(responseSpreads);
    result.add(new long[] {acceptRequest});
    result.add(new long[] {errorRequest});
    return result;
  }
}
