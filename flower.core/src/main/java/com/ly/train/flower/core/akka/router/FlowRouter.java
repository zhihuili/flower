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
package com.ly.train.flower.core.akka.router;

import java.util.concurrent.TimeoutException;
import javax.servlet.AsyncContext;
import com.ly.train.flower.common.core.config.ServiceConfig;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.core.web.Web;
import com.ly.train.flower.common.lifecyle.AbstractInit;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.core.akka.ActorFactory;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.core.service.container.util.ServiceContextUtil;
import akka.actor.ActorRef;

/**
 * @author leeyazhou
 * 
 */
public class FlowRouter extends AbstractInit implements Router {
  static final com.ly.train.flower.common.logging.Logger logger = LoggerFactory.getLogger(FlowRouter.class);
  private int flowerNumber = -1;
  private final ServiceConfig headerServiceConfig;
  private final ActorFactory actorFactory;

  public FlowRouter(ServiceConfig headerServiceConfig, FlowerFactory flowerFactory) {
    this.headerServiceConfig = headerServiceConfig;
    this.actorFactory = flowerFactory.getActorFactory();
  }

  public FlowRouter(ServiceConfig headerServiceConfig, int flowerNumber, FlowerFactory flowerFactory) {
    this(headerServiceConfig, flowerFactory);
    this.flowerNumber = flowerNumber;
  }


  @Override
  protected void doInit() {
    getServiceRouter();
  }

  public <T> void asyncCallService(T message) {
    this.asyncCallService(message, null);
  }

  /**
   * 异步调用
   * 
   * @param message message
   * @param ctx {@link AsyncContext}
   */
  public <T> void asyncCallService(T message, AsyncContext ctx) {
    ServiceContext serviceContext = null;
    if (ctx != null) {
      Web web = new Web(ctx);
      serviceContext = ServiceContextUtil.context(message, web);
      ServiceContextUtil.record(serviceContext);
    } else {
      serviceContext = ServiceContextUtil.context(message);
    }
    serviceContext.setFlowName(headerServiceConfig.getFlowName());
    if (StringUtil.isBlank(serviceContext.getCurrentServiceName())) {
      serviceContext.setCurrentServiceName(headerServiceConfig.getServiceName());
    }
    getServiceRouter().asyncCallService(serviceContext, ActorRef.noSender());
  }

  /**
   * 同步调用
   * 
   * @param <P> 入参
   * @param <R> 出参
   * @param message message
   * @return R
   * @throws TimeoutException
   */
  @SuppressWarnings("unchecked")
  public <P, R> R syncCallService(P message) throws TimeoutException {
    ServiceContext serviceContext = ServiceContextUtil.context(message);
    serviceContext.setFlowName(headerServiceConfig.getFlowName());
    serviceContext.setCurrentServiceName(headerServiceConfig.getServiceName());
    serviceContext.setSync(true);
    return (R) getServiceRouter().syncCallService(serviceContext);
  }

  private ServiceRouter getServiceRouter() {
    return actorFactory.buildServiceRouter(headerServiceConfig, flowerNumber);
  }

  public ServiceConfig getServiceConfig() {
    return headerServiceConfig;
  }

}
