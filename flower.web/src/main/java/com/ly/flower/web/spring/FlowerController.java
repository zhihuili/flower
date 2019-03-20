/**
 * 
 */
package com.ly.flower.web.spring;

import org.springframework.beans.factory.InitializingBean;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.annotation.Flower;
import com.ly.train.flower.common.service.ServiceFlow;

/**
 * 
 * @author leeyazhou
 */
public abstract class FlowerController implements InitializingBean {

  protected ServiceRouter serviceRouter;
  private String flowerName;


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
  public abstract ServiceRouter initServiceRouter();

  /**
   * 定义数据处理流
   * 
   * @see ServiceFlow
   */
  public abstract void buildFlower();

  /**
   * 获取流名称
   * 
   * @return
   */
  public String getFlowName() {
    if (flowerName == null) {
      Flower bindController = this.getClass().getAnnotation(Flower.class);
      this.flowerName = bindController.value();
    }
    return flowerName;
  }
}
