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
package com.ly.train.flower.test.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import com.ly.train.flower.common.akka.router.FlowRouter;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import com.ly.train.flower.test.util.FlowerUtil;

/**
 * @author leeyazhou
 */
public class FlowerSyncClient extends AbstractJavaSamplerClient {
  private static final Logger logger = LoggerFactory.getLogger(FlowerSyncClient.class);
  private String flowName = "benchmarkClient";
  private FlowRouter flowRouter;

  @Override
  public void setupTest(JavaSamplerContext context) {
    this.flowRouter = FlowerUtil.buildFlowRouter(flowName, -1);
  }

  @Override
  public Arguments getDefaultParameters() {
    return super.getDefaultParameters();
  }

  private static final String message = "Flower is Good.";

  @Override
  public SampleResult runTest(JavaSamplerContext arg0) {
    SampleResult result = new SampleResult();
    result.sampleStart();
    try {
      long begin = System.currentTimeMillis();
      String ret = (String) flowRouter.syncCallService(message);
      long cost = (System.currentTimeMillis() - begin);
      logger.info("ret : {}, cost:[{}ms]", ret, cost);
      if (message.equals(ret)) {
        result.setSuccessful(true);
      }
    } catch (Exception e) {
      result.setSuccessful(false);
      logger.error("", e);

    } finally {
      result.sampleEnd();
    }
    return result;
  }

  @Override
  public void teardownTest(JavaSamplerContext context) {
    FlowerUtil.stop();
  }

}
