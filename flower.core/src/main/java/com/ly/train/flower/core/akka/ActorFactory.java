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
package com.ly.train.flower.core.akka;

import com.ly.train.flower.common.lifecyle.Lifecycle;
import com.ly.train.flower.core.akka.actor.wrapper.ActorWrapper;
import com.ly.train.flower.core.akka.router.FlowRouter;
import com.ly.train.flower.core.akka.router.ServiceRouter;
import com.ly.train.flower.core.service.config.ServiceConfig;

/**
 * Actor 工厂
 * 
 * @author leeyazhou
 */
public interface ActorFactory extends Lifecycle {


  /**
   * 创建actor并缓存
   * 
   * @param serviceConfig 服务配置信息
   * @return {@link ActorWrapper}
   * @see ActorFactory#buildServiceActor(ServiceConfig, int)
   */
  public ActorWrapper buildServiceActor(ServiceConfig serviceConfig);


  /**
   * 创建actor并缓存
   * 
   * @param serviceConfig 服务配置信息
   * @param index 索引
   * @return {@link ActorWrapper}
   */
  public ActorWrapper buildServiceActor(ServiceConfig serviceConfig, int index);

  /**
   * 创建流程路由
   * 
   * @param flowName 流程名称
   * @param flowNumber 数量
   * @return {@link FlowRouter}
   */
  public FlowRouter buildFlowRouter(String flowName, int flowNumber);

  /**
   * 创建服务路由
   * 
   * @param serviceConfig 服务配置信息
   * @param flowNumber 数量
   * @return {@link ServiceRouter}
   */
  public ServiceRouter buildServiceRouter(ServiceConfig serviceConfig, int flowNumber);


}
