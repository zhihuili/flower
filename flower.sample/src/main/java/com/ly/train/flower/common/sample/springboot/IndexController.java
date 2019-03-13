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
package com.ly.train.flower.common.sample.springboot;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.container.ServiceFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class IndexController {
  static final long serialVersionUID = 1L;
  ServiceRouter sr;

  public IndexController() {
    buildServiceEnv();
    sr = ServiceFacade.buildServiceRouter("async", "serviceA", 400);
  }

  @RequestMapping(value = "/", method = POST)
  public void index(HttpServletRequest req) {
    AsyncContext context = req.startAsync();
    asyncExe(context);
  }

  private void asyncExe(AsyncContext ctx) {
    try {
      sr.asyncCallService(null, ctx);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void buildServiceEnv() {
    ServiceFactory.registerService("serviceA",
        "com.ly.train.flower.common.sample.springboot.ServiceA");
    ServiceFactory.registerService("serviceB",
        "com.ly.train.flower.common.sample.springboot.ServiceB");

    ServiceFlow.buildFlow("async", "serviceA", "serviceB");

  }
}