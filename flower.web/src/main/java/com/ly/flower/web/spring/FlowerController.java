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

import com.ly.train.flower.common.akka.FlowRouter;
import com.ly.train.flower.common.akka.ServiceFacade;
import com.ly.train.flower.common.annotation.Flower;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceFlow;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 
 * @author leeyazhou
 */
public abstract class FlowerController implements InitializingBean {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  private static final int DEFAULT_FLOW_NUMBER = 2 << 2;
  private FlowRouter serviceRouter;
  private String flowerName;
  private String serviceName;
  private Integer flowNumber;

  @Autowired
  private FlowerFactory flowerFactory;

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
   * @see ServiceFacade#buildFlowRouter(java.lang.String, int)
   * @return {@code ServiceRouter}
   */
  private FlowRouter initServiceRouter() {
    return flowerFactory.getServiceFacade().buildFlowRouter(getFlowName(), getFlowNumber());
  }

  /**
   * 定义数据处理流
   * 
   * @see ServiceFlow
   */
  public abstract void buildFlower();

  public ServiceFlow getServiceFlow() {
    return flowerFactory.getServiceFactory().getOrCreateServiceFlow(getFlowName());
  }

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

  /**
   * 获取流程首个服务名称
   *
   * @return serviceName
   */
  public String getServiceName() {
    if (serviceName == null) {
      Flower bindController = this.getClass().getAnnotation(Flower.class);
      this.serviceName = bindController.serviceName();
    }
    return serviceName;
  }

  /**
   * 获取流程通道数
   *
   * @return serviceName
   */
  public int getFlowNumber() {
    if (this.flowNumber == null) {
      Flower bindController = this.getClass().getAnnotation(Flower.class);
      this.flowNumber = bindController.flowNumber();
      logger.info("set flowNumber : {}",this.flowNumber);
    }
    if(this.flowNumber == 0){
      this.flowNumber = FlowerController.DEFAULT_FLOW_NUMBER;
      logger.warn("find zero flowNumber, using default values: {}",this.flowNumber);
    }
    return flowNumber;
  }
}
