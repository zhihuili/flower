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
package com.ly.train.flower.web.spring;

import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import com.ly.train.flower.common.akka.router.FlowRouter;
import com.ly.train.flower.common.annotation.Flower;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceFlow;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * 
 * @author leeyazhou
 */
public abstract class FlowerController implements InitializingBean {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  private FlowRouter flowRouter;
  private String flowerName;
  private String serviceName;
  private int flowerNumber;


  @Autowired
  private FlowerFactory flowerFactory;

  protected void doProcess(Object param, HttpServletRequest req) throws IOException {
    AsyncContext context = null;
    if (req != null) {
      context = req.startAsync();
    }
    flowRouter.asyncCallService(param, context);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    getFlowName();
    buildFlower();
    this.flowRouter = initFlowRouter();
  }

  /**
   * 初始化路由
   * 
   * @see com.ly.train.flower.common.actor.ServiceFacade#buildFlowRouter
   * @return {@code ServiceRouter}
   */
  private FlowRouter initFlowRouter() {
    return flowerFactory.getServiceFacade().buildFlowRouter(getFlowName(), getFlowerNumber());
  }

  /**
   * 定义数据处理流程
   * 
   * @see ServiceFlow
   */
  public abstract void buildFlower();

  public ServiceFlow getServiceFlow() {
    return flowerFactory.getServiceFactory().getOrCreateServiceFlow(getFlowName());
  }

  /**
   * 获取流程名称
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

  public int getFlowerNumber() {
    if (flowerNumber == 0) {
      Flower bindController = this.getClass().getAnnotation(Flower.class);
      this.flowerNumber = bindController.flowNumber();
      if (this.flowerNumber <= 0) {
        this.flowerNumber = 2 << 6;
      }
    }
    return flowerNumber;
  }

  public String getServiceName() {
    if (serviceName == null) {
      Flower bindController = this.getClass().getAnnotation(Flower.class);
      this.serviceName = bindController.serviceName();
    }
    return serviceName;
  }
}
