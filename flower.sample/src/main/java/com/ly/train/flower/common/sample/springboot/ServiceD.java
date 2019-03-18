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

import org.springframework.web.bind.annotation.RequestMethod;
import com.ly.flower.web.springboot.InitController;
import com.ly.flower.web.springboot.annotation.BindController;
import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.container.ServiceFactory;

@BindController(path = "/ServiceD", method = RequestMethod.POST)
public class ServiceD implements Service<User,Integer>, InitController {

    @Override
    public ServiceRouter init() {
        buildServiceEnv();
        return ServiceFacade.buildServiceRouter("async", "serviceD", 400);
    }

    private static void buildServiceEnv() {
        ServiceFactory.registerService("serviceD",
                "com.ly.train.flower.common.sample.springboot.ServiceD");
        ServiceFactory.registerService("serviceB",
                "com.ly.train.flower.common.sample.springboot.ServiceB");

        ServiceFlow.buildFlow("async", "serviceD", "serviceB");
    }

    @Override
    public Integer process(User message, ServiceContext context) throws Throwable {
        context.getWeb().println("User:" + message.getName());
        return message.getId();
    }
}
