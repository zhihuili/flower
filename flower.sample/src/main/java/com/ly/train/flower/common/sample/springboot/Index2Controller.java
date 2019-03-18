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
package com.ly.train.flower.common.sample.springboot;


import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.container.ServiceFactory;

@RestController
public class Index2Controller {
  static final long serialVersionUID = 1L;
  ServiceRouter serviceRouter;

  public Index2Controller() {
    buildServiceEnv();
    serviceRouter = ServiceFacade.buildServiceRouter("async", "serviceA", 400);
  }

  @RequestMapping("/index2")
  @ResponseBody
  public void index(User user, HttpServletRequest req) {
    AsyncContext context = req.startAsync();
    asyncExe(context, user);
  }

  private void asyncExe(AsyncContext ctx, User user) {
    try {
      serviceRouter.asyncCallService(user, ctx);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void buildServiceEnv() {
    ServiceFactory.registerService("serviceA", ServiceA.class);
    ServiceFactory.registerService("serviceB", ServiceB.class);

    ServiceFlow.buildFlow("async", "serviceA", "serviceB");

  }
}
