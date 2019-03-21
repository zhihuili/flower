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
///**
// * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.ly.train.flower.sample.web.performance
//
//
//import io.gatling.core.scenario.Simulation
//import io.gatling.core.Predef._
//import io.gatling.http.Predef._
//
//import scala.concurrent.duration._
//
//class LoadSimulation extends Simulation {
//
//  // 从系统变量读取 baseUrl、path和模拟的用户数
//  val baseUrl = System.getProperty("base.url")
//  val testPath = System.getProperty("test.path")
//  val sim_users = System.getProperty("sim.users").toInt
//
//  val httpConf = http.baseUrl(baseUrl)
//
//  var requestName = ""
//  var scenarioName = ""
//
//
//  if(testPath.equals("flow")) {
//    requestName = "flow"
//    scenarioName = "flow"
//  } else if (testPath.equals("sync?id=1")) {
//    requestName = "sync"
//    scenarioName = "sync"
//  } else if (testPath.equals("async?id=1")) {
//    requestName = "async"
//    scenarioName = "async"
//  }
//
//  // 定义模拟的请求，重复30次
//  val helloRequest = repeat(30) {
//    // 自定义测试名称
//    exec(http(requestName)
//      // 执行get请求
//      .get(testPath))
//      .pause(1 second, 2 seconds)
//  }
//
//  // 定义模拟的场景
//  val scn = scenario(scenarioName)
//    // 该场景执行上边定义的请求
//    .exec(helloRequest)
//
//  // 配置并发用户的数量在30秒内均匀提高至sim_users指定的数量
//  setUp(scn.inject(rampUsers(sim_users).during(30 seconds)).protocols(httpConf))
//}
//
