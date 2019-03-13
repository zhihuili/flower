/**
 * Copyright © ${project.inceptionYear} 同程艺龙 (zhihui.li@ly.com)
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
package com.ly.train.flower.common.sample.condition;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.service.container.ServiceLoader;

public class Sample {

  public static void main(String[] args) throws Exception {
    buildServiceEnv();

    ServiceFacade.asyncCallService("sample", "serviceA", "c");
  }

  public static void buildServiceEnv() {
    ServiceFactory.registerService("serviceA",
        "com.ly.train.flower.common.sample.condition.ServiceA");
    ServiceFactory.registerService("serviceB",
        "com.ly.train.flower.common.sample.condition.ServiceB");
    ServiceFactory.registerService("serviceC",
        "com.ly.train.flower.common.sample.condition.ServiceC");
    ServiceFactory.registerService("serviceE",
        "com.ly.train.flower.common.sample.condition.ServiceE");
    ServiceFactory.registerService("serviceD",
        "com.ly.train.flower.common.sample.condition.ServiceD");
    ServiceFactory.registerService("serviceF",
        "com.ly.train.flower.common.sample.condition.ServiceF");
    ServiceFactory.registerService("serviceG",
        "com.ly.train.flower.common.sample.condition.ServiceG");
    ServiceFactory.registerService("serviceCondition",
        "com.ly.train.flower.common.service.ConditionService;serviceF,serviceG");

    ServiceFlow.buildFlow("sample", "serviceA", "serviceB");
    ServiceFlow.buildFlow("sample", "serviceA", "serviceC");
    ServiceFlow.buildFlow("sample", "serviceC", "serviceD");
    ServiceFlow.buildFlow("sample", "serviceC", "serviceE");
    ServiceFlow.buildFlow("sample", "serviceE", "serviceCondition");
    ServiceFlow.buildFlow("sample", "serviceCondition", "serviceF");
    ServiceFlow.buildFlow("sample", "serviceCondition", "serviceG");

  }

}
