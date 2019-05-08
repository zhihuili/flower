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
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author lee
 */
public class AbstractBenchmarkClient {
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private int threadNum = 32;
  protected long runtime;
  private long start;

  protected List<BenchmarkRunnable> benchmarkRunnables;

  public AbstractBenchmarkClient() {
  }

  public void setRuntime(long runtime) {
    this.runtime = runtime;
  }

  public void setBenchmarkRunnables(List<BenchmarkRunnable> benchmarkRunnables) {
    this.benchmarkRunnables = benchmarkRunnables;
  }

  public void setThreadNum(int threadNum) {
    this.threadNum = threadNum;
  }

  public int getThreadNum() {
    return threadNum;
  }

  public void doBenchmark() throws Exception {}

  public void markStart() {
    this.start = System.currentTimeMillis();
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        printResult();
      }
    }, 3000, 5000);
  }

  /**
   * 打印结果
   */
  protected void printResult() {
    long allRequestSum = 0;

    long allErrorRequestSum = 0;

    // < 0
    long below0sum = 0;

    // (0,1]
    long above0sum = 0;

    // (1,5]
    long above1sum = 0;

    // (5,10]
    long above5sum = 0;

    // (10,50]
    long above10sum = 0;

    // (50,100]
    long above50sum = 0;

    // (100,500]
    long above100sum = 0;

    // (500,1000]
    long above500sum = 0;

    // > 1000
    long above1000sum = 0;

    long currentRuntime = (System.currentTimeMillis() - start) / 1000;
    for (BenchmarkRunnable benchmarkRunnable : benchmarkRunnables) {
      long[] responseSpreads = benchmarkRunnable.getResult().get(0);
      allRequestSum += benchmarkRunnable.getResult().get(1)[0];
      allErrorRequestSum += benchmarkRunnable.getResult().get(2)[0];
      below0sum += responseSpreads[0];
      above0sum += responseSpreads[1];
      above1sum += responseSpreads[2];
      above5sum += responseSpreads[3];
      above10sum += responseSpreads[4];
      above50sum += responseSpreads[5];
      above100sum += responseSpreads[6];
      above500sum += responseSpreads[7];
      above1000sum += responseSpreads[8];
    }

    System.out.println(" RT <= 0 : " + below0sum * 100 / allRequestSum + "% " + below0sum + "/" + allRequestSum);
    System.out.println(" RT (0,1] : " + above0sum * 100 / allRequestSum + "% " + above0sum + "/" + allRequestSum);
    System.out.println(" RT (1,5] : " + above1sum * 100 / allRequestSum + "% " + above1sum + "/" + allRequestSum);
    System.out.println(" RT (5,10] : " + above5sum * 100 / allRequestSum + "% " + above5sum + "/" + allRequestSum);
    System.out.println(" RT (10,50] : " + above10sum * 100 / allRequestSum + "% " + above10sum + "/" + allRequestSum);
    System.out.println(" RT (50,100] : " + above50sum * 100 / allRequestSum + "% " + above50sum + "/" + allRequestSum);
    System.out.println(" RT (100,500] : " + above100sum * 100 / allRequestSum + "% " + above100sum + "/"
        + allRequestSum);
    System.out.println(" RT (500,1000] : " + above500sum * 100 / allRequestSum + "% " + above500sum + "/"
        + allRequestSum);
    System.out
        .println(" RT > 1000 : " + above1000sum * 100 / allRequestSum + "% " + above1000sum + "/" + allRequestSum);

    System.out.println("allRequestSum \t: " + allRequestSum);
    System.out.println("allErrorRequestSum : " + allErrorRequestSum);
    System.out.println("runtime(second) : " + currentRuntime);
    if (currentRuntime == 0) {
      currentRuntime = 1;
    }
    System.out.println("Average/sec \t: " + allRequestSum / currentRuntime);
    System.out.println("currentTime : " + dateFormat.format(new Date()));
    System.out.println("**********************************************************************\n");
  }
}
