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
package com.ly.train.flower.core.service.container;

import java.util.Set;
import com.ly.train.flower.common.core.service.Service;
import com.ly.train.flower.common.exception.handler.ExceptionHandler;
import com.ly.train.flower.common.lifecyle.Lifecycle;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.core.akka.ActorFactory;
import com.ly.train.flower.core.akka.ServiceFacade;
import com.ly.train.flower.core.akka.router.FlowRouter;
import com.ly.train.flower.core.akka.router.ServiceRouter;
import com.ly.train.flower.registry.Registry;

/**
 * @author leeyazhou
 * 
 */
public interface FlowerFactory extends Lifecycle {


  /**
   * 获取Flower容器配置信息
   * 
   * @return {@link FlowerConfig}
   */
  FlowerConfig getFlowerConfig();

  /**
   * 获取注册中心
   * 
   * @return {@link Registry}
   */
  Set<Registry> getRegistry();

  /**
   * 注册异常处理器
   * 
   * @param exceptionClass exception Class
   * @param exceptionHandler {@link ExceptionHandler}
   */
  void registerExceptionHandler(Class<? extends Throwable> exceptionClass, ExceptionHandler exceptionHandler);

  /**
   * 设置默认异常处理器
   * 
   * @param exceptionHandler {@link ExceptionHandler}
   */
  void setDefaultExceptionHandler(ExceptionHandler exceptionHandler);

  /**
   * akka Actor 工厂
   * 
   * @return {@link ActorFactory}
   */
  ActorFactory getActorFactory();

  /**
   * {@link Service}工厂
   * 
   * @return {@link ServiceFactory}
   */
  ServiceFactory getServiceFactory();

  /**
   * service facade
   * 
   * @return {@link ServiceFacade}
   */
  ServiceFacade getServiceFacade();

  /**
   * will be cached by flowName + "_" + serviceName
   * 
   * @param flowName flowName
   * @param flowNumber 流程处理单元数量
   * @return {@link ServiceRouter}
   */
  FlowRouter buildFlowRouter(String flowName, int flowNumber);


}
