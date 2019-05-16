package com.ly.train.flower.common.akka;

import com.ly.train.flower.common.akka.actor.wrapper.ActorWrapper;
import com.ly.train.flower.common.akka.router.FlowRouter;
import com.ly.train.flower.common.akka.router.ServiceRouter;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.lifecyle.Lifecycle;

/**
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
