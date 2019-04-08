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
package com.ly.train.flower.common.sample.web;

import javax.servlet.http.HttpServlet;
import com.ly.train.flower.common.akka.ServiceFacade;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.service.container.ServiceFlow;
import com.ly.train.flower.common.service.container.simple.SimpleFlowerFactory;

/**
 * @author leeyazhou
 *
 */
public class FlowerHttpServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  protected static FlowerFactory flowerFactory = null;
  protected static ServiceFactory serviceFactory;
  protected static ServiceFacade serviceFacade;

  public FlowerHttpServlet() {
    if (flowerFactory == null) {
      synchronized (FlowerHttpServlet.class) {
        if (flowerFactory == null) {
          flowerFactory = new SimpleFlowerFactory();
          serviceFactory = flowerFactory.getServiceFactory();
          serviceFacade = flowerFactory.getServiceFacade();
        }
      }
    }
  }

  public ServiceFacade getServiceFacade() {
    return flowerFactory.getServiceFacade();
  }
  
  public ServiceFlow getServiceFlow(String flowName) {
    return serviceFactory.getOrCreateServiceFlow(flowName);
  }
}
