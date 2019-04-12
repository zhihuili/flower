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
package com.ly.train.flower.common.sample.springboot.controller;


import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.ly.train.flower.common.annotation.Flower;
import com.ly.train.flower.common.sample.springboot.model.User;
import com.ly.train.flower.web.spring.FlowerController;

@RestController
@Flower(serviceName = "ServiceA", value = "async")
public class Index2Controller extends FlowerController {

  @RequestMapping("/index2")
  @ResponseBody
  protected void process(User param, HttpServletRequest req) throws IOException {
    doProcess(param, req);
  }


  @Override
  public void buildFlower() {
    getServiceFlow().buildFlow("ServiceA", "ServiceB");

  }

}
