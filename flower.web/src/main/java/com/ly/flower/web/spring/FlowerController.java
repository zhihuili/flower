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
package com.ly.flower.web.spring;

import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.annotation.Flower;
import com.ly.train.flower.common.service.container.ServiceFlow;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * 
 * @author leeyazhou
 */
public abstract class FlowerController implements InitializingBean {
  protected final Logger logger =LoggerFactory.getLogger(getClass());
  private ServiceRouter serviceRouter;
  private String flowerName;
  private String serviceName;


  protected void doProcess(Object param, HttpServletRequest req) throws IOException {
    AsyncContext context = req.startAsync();
    serviceRouter.asyncCallService(param, context);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    getFlowName();
    buildFlower();
    this.serviceRouter = initServiceRouter();
  }

  /**
   * 初始化路由
   * 
   * @see com.ly.train.flower.common.actor.ServiceFacade.buildServiceRouter
   * @return {@code ServiceRouter}
   */
  private ServiceRouter initServiceRouter() {
    return ServiceFacade.buildServiceRouter(getFlowName(), getServiceName(), 2 << 7);
  }

  /**
   * 定义数据处理流
   * 
   * @see ServiceFlow
   */
  public abstract void buildFlower();

  /**
   * 获取流名称
   * 
   * @return flowName
   */
  public String getFlowName() {
    if (flowerName == null) {
      Flower bindController = this.getClass().getAnnotation(Flower.class);
      this.flowerName = bindController.value();
    }
    return flowerName;
  }

  public String getServiceName() {
    if (serviceName == null) {
      Flower bindController = this.getClass().getAnnotation(Flower.class);
      this.serviceName = bindController.serviceName();
    }
    return serviceName;
  }
}
