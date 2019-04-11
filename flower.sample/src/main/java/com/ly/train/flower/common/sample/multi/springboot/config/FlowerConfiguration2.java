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
package com.ly.train.flower.common.sample.multi.springboot.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import com.ly.train.flower.common.sample.multi.springboot.service.CreateOrderService;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.SpringFlowerFactory;

/**
 * @author leeyazhou
 *
 */
@Service(value = "FlowerConfiguration2")
public class FlowerConfiguration2 implements InitializingBean {

  @Override
  public void afterPropertiesSet() throws Exception {
    FlowerFactory flowerFactory = new SpringFlowerFactory("conf/flower_multi_2.yml");
    flowerFactory.start();
    flowerFactory.getServiceFactory().registerService(CreateOrderService.class.getSimpleName(),
        CreateOrderService.class);
  }

}
