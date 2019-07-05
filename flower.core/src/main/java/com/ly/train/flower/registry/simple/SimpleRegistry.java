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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.core.akka.router.ServiceRouter;
import com.ly.train.flower.core.service.config.ServiceConfig;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.core.service.container.ServiceContext;
import com.ly.train.flower.core.service.container.ServiceMeta;
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
    this.serviceInfoRegisterRouter =
        getServiceRouter("ServiceInfoRegisterService", ServiceInfo.class.getName(), Boolean.class.getName());
    this.serviceInfoListRouter =
        getServiceRouter("ServiceInfoListService", ServiceInfo.class.getName(), HashSet.class.getName());
    this.serviceConfigRegisterRouter =
        getServiceRouter("ServiceConfigRegisterService", ServiceConfig.class.getName(), Boolean.class.getName());
    this.serviceConfigListRouter =
        getServiceRouter("ServiceConfigListService", ServiceConfig.class.getName(), HashSet.class.getName());
  }

  private ServiceRouter getServiceRouter(String serviceName, String paramType, String resultType) {
    ServiceConfig serviceConfig = new ServiceConfig();
    serviceConfig.setServiceName(serviceName);
    serviceConfig.addAddress(getUrl());
    serviceConfig.setLocal(false);
    serviceConfig.setApplication(url.getParam(Constant.applicationName));
    ServiceMeta serviceMeta = new ServiceMeta("no class name, register service name");
    serviceConfig.setServiceMeta(serviceMeta);

    serviceMeta.setServiceName(serviceName);
    serviceMeta.setParamType(paramType);
    serviceMeta.setResultType(resultType);
    return flowerFactory.getActorFactory().buildServiceRouter(serviceConfig, 2);
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
    ServiceContext serviceContext = makeServiceContext(serviceInfo);
    serviceContext.setCurrentServiceName("ServiceInfoListService");
    serviceContext.setSync(false);
    Object o = null;
    try {
      o = serviceInfoListRouter.syncCallService(serviceContext);
    } catch (TimeoutException e) {
      e.printStackTrace();
    }
    List<ServiceInfo> ret2 = new ArrayList<ServiceInfo>();
    if (o != null) {
      Set<ServiceInfo> ret = (Set<ServiceInfo>) o;
      if (ret != null && !ret.isEmpty()) {
        ret2.addAll(ret);
      }
    }
    return ret2;
  }

  @Override
  public List<ServiceConfig> doGetServiceConfig(ServiceConfig serviceConfig) {
    ServiceContext serviceContext = makeServiceContext(serviceConfig);
    serviceContext.setCurrentServiceName("ServiceConfigListService");
    serviceContext.setSync(false);
    Object o = null;
    try {
      o = serviceConfigListRouter.syncCallService(serviceContext);
    } catch (TimeoutException e) {
      e.printStackTrace();
    }
    List<ServiceConfig> ret2 = new ArrayList<ServiceConfig>();
    if (o != null) {
      ServiceConfig ret = (ServiceConfig) o;
      if (ret != null) {
        ret2.add(ret);
      }
    }
    return ret2;
  }
}
