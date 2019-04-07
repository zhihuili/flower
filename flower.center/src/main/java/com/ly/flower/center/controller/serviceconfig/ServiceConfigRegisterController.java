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
/**
 * 
 */
package com.ly.flower.center.controller.serviceconfig;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.ly.flower.center.controller.BaseController;
import com.ly.flower.center.service.ReturnService;
import com.ly.flower.center.service.ServiceConfigRegisterService;
import com.ly.train.flower.common.annotation.Flower;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.ServiceFlow;

/**
 * @author leeyazhou
 *
 */
@RestController
@RequestMapping("/serviceconfig/")
@Flower(serviceName = "ServiceConfigRegisterService", value = "registerServiceConfig")
public class ServiceConfigRegisterController extends BaseController {

  @RequestMapping("register")
  protected void process(String data, HttpServletRequest req) throws IOException {
    ServiceConfig serviceConfig = JSONObject.parseObject(data, ServiceConfig.class);
    logger.info("请求信息：{}", data);
    logger.info("请求信息：{}", serviceConfig);
    doProcess(serviceConfig, req);
  }



  @Override
  public void buildFlower() {
    ServiceFlow.getOrCreate(getFlowName()).buildFlow(ServiceConfigRegisterService.class, ReturnService.class);
  }
}
