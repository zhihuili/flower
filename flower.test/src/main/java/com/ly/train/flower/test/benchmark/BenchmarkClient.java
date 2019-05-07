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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import com.ly.train.flower.common.akka.FlowRouter;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.simple.SimpleFlowerFactory;
import com.ly.train.flower.test.service.EndService;
import com.ly.train.flower.test.service.StartService;

/**
 * @author lee
 */
public class BenchmarkClient extends AbstractBenchmarkClient {

  private static final String flowName = "benchmarkClient";

  public static void main(String[] args) throws InterruptedException {
    BenchmarkClient client = new BenchmarkClient();
    long runtime = 60;
    int threadNum = 128;
    if (args != null && args.length > 0) {
      runtime = Long.parseLong(args[0]);
      if (args.length > 1) {
        threadNum = Integer.parseInt(args[1]);
      }
    }
    client.setThreadNum(threadNum);
    client.setRuntime(runtime);

    client.doBenchmark();
    System.exit(0);
  }

  private FlowerFactory prepareFlower() {
    FlowerFactory flowerFactory = new SimpleFlowerFactory();
    flowerFactory.getServiceFactory().registerService(StartService.class.getSimpleName(), StartService.class);
    flowerFactory.getServiceFactory().registerService(EndService.class.getSimpleName(), EndService.class);
    flowerFactory.getServiceFactory().getOrCreateServiceFlow(flowName).buildFlow(StartService.class, EndService.class);
    return flowerFactory;
  }

  @Override
  public void doBenchmark() throws InterruptedException {
    CountDownLatch countDownlatch = new CountDownLatch(getThreadNum());
    FlowerFactory flowerFactory = prepareFlower();
    FlowRouter flowRouter = flowerFactory.getServiceActorFactory().buildFlowRouter(flowName, getThreadNum());
    check(flowRouter);

    long endTime = System.currentTimeMillis() + runtime * 1000;
    System.out.println("ready to start client benchmark, benchmark will end at:"
        + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(endTime)));

    List<BenchmarkRunnable> benchmarkRunnables = new ArrayList<BenchmarkRunnable>(getThreadNum());
    markStart();
    for (int i = 0; i < getThreadNum(); i++) {
      BenchmarkRunnableImpl benchmarkRunnable = new BenchmarkRunnableImpl(countDownlatch, flowRouter, endTime, i);
      benchmarkRunnables.add(benchmarkRunnable);
      benchmarkRunnable.setMessage("Flower is Good.");
      benchmarkRunnable.start();
    }
    this.setBenchmarkRunnables(benchmarkRunnables);
    countDownlatch.await();
    this.printResult();
  }


  private static void check(FlowRouter flowRouter) {
    int i = 0;
    while (i++ < 100) {
      flowRouter.asyncCallService("-" + i);
    }
  }

}
