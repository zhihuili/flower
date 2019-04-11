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
package com.ly.train.flower.registry.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.ly.train.flower.common.akka.ServiceRouter;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.FlowerFactory;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.registry.AbstractRegistry;
import com.ly.train.flower.registry.config.ServiceInfo;

/**
 * @author leeyazhou
 *
 */
public class SimpleRegistry extends AbstractRegistry {
  protected FlowerFactory flowerFactory;

  private ServiceRouter serviceInfoRegisterRouter;
  private ServiceRouter serviceInfoListRouter;
  private ServiceRouter serviceConfigRegisterRouter;
  private ServiceRouter serviceConfigListRouter;

  public SimpleRegistry(URL url) {
    super(url);
  }

  public void setFlowerFactory(FlowerFactory flowerFactory) {
    this.flowerFactory = flowerFactory;

    this.serviceInfoRegisterRouter = getServiceRouter("ServiceInfoRegisterService");
    this.serviceInfoListRouter = getServiceRouter("ServiceInfoListService");
    this.serviceConfigRegisterRouter = getServiceRouter("ServiceConfigRegisterService");
    this.serviceConfigListRouter = getServiceRouter("ServiceConfigListService");
  }

  private ServiceRouter getServiceRouter(String serviceName) {
    ServiceConfig serviceConfig = new ServiceConfig();
    serviceConfig.setServiceName(serviceName);
    serviceConfig.addAddress(getUrl());
    serviceConfig.setLocal(false);
    return flowerFactory.getServiceActorFactory().buildServiceRouter(serviceConfig, 2);
  }

  private ServiceContext makeServiceContext(Object message) {
    ServiceContext context = ServiceContext.context(message);
    return context;
  }

  @Override
  public boolean doRegister(ServiceInfo serviceInfo) {
    // logger.info("register serviceInfo : {}", serviceInfo);
    ServiceContext serviceContext = makeServiceContext(serviceInfo);
    serviceContext.setCurrentServiceName("ServiceInfoRegisterService");
    serviceContext.setSync(false);
    serviceInfoRegisterRouter.asyncCallService(serviceContext);
    return Boolean.TRUE;
  }

  @Override
  public boolean doRegisterServiceConfig(ServiceConfig serviceConfig) {
    ServiceContext serviceContext = makeServiceContext(serviceConfig);
    serviceContext.setCurrentServiceName("ServiceConfigRegisterService");
    serviceContext.setSync(false);
    serviceConfigRegisterRouter.asyncCallService(serviceContext);
    return false;
  }

  @Override
  public List<ServiceInfo> doGetProvider(ServiceInfo serviceInfo) {
    ServiceContext serviceContext = makeServiceContext(null);
    serviceContext.setCurrentServiceName("ServiceInfoListService");
    serviceContext.setSync(false);
    Object o = serviceInfoListRouter.syncCallService(serviceContext);
    Set<ServiceInfo> ret = null;
    if (o != null) {
      ret = (Set<ServiceInfo>) o;
    }
    List<ServiceInfo> ret2 = new ArrayList<ServiceInfo>(ret);
    return ret2;
  }

  @Override
  public List<ServiceConfig> doGetServiceConfig(ServiceConfig serviceConfig) {
    ServiceContext serviceContext = makeServiceContext(null);
    serviceContext.setCurrentServiceName("ServiceConfigListService");
    serviceContext.setSync(false);
    Object o = serviceConfigListRouter.syncCallService(serviceContext);
    Set<ServiceConfig> ret = null;
    if (o != null) {
      ret = (Set<ServiceConfig>) o;
    }
    List<ServiceConfig> ret2 = new ArrayList<ServiceConfig>(ret);
    return ret2;
  }
}
