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
package com.ly.train.flower.sample.multi.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.sample.multi.springboot.service.CreateOrderExtService;
import com.ly.train.flower.sample.multi.springboot.service.EndService;
import com.ly.train.flower.sample.multi.springboot.service.StartService;
import com.ly.train.flower.web.spring.container.SpringFlowerFactory;

/**
 * @author leeyazhou
 * 
 */
@Configuration
public class FlowerConfiguration {

  @Bean
  public FlowerFactory flowerFactory() {
    FlowerFactory flowerFactory = new SpringFlowerFactory("conf/flower_multi_1.yml");
    flowerFactory.getServiceFactory().registerService(StartService.class.getSimpleName(), StartService.class);
    flowerFactory.getServiceFactory().registerService(CreateOrderExtService.class.getSimpleName(),
        CreateOrderExtService.class);
    flowerFactory.getServiceFactory().registerService(EndService.class.getSimpleName(), EndService.class);
    return flowerFactory;
  }

}
