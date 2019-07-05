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
package com.ly.train.flower.service.container;

import org.springframework.beans.factory.InitializingBean;
import com.ly.train.flower.core.service.container.simple.SimpleFlowerFactory;

/**
 * @author leeyazhou
 * 
 */
public class SpringFlowerFactory extends SimpleFlowerFactory implements InitializingBean {

  public SpringFlowerFactory() {
    super();
  }

  public SpringFlowerFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    init();
  }

}
