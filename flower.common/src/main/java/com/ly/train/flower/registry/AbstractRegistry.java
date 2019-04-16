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
package com.ly.train.flower.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.common.util.concurrent.NamedThreadFactory;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;
import com.ly.train.flower.registry.config.ServiceInfo;

/**
 * @author leeyazhou
 *
 */
public abstract class AbstractRegistry implements Registry {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  protected final ConcurrentMap<String, ServiceInfo> serviceInfoCache = new ConcurrentHashMap<>();
  protected final ConcurrentMap<String, ServiceConfig> serviceConfigCache = new ConcurrentHashMap<>();
  private static final ScheduledExecutorService executorService =
      Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("registry"));
  protected final URL url;

  public AbstractRegistry(URL url) {
    this.url = url;
    executorService.scheduleAtFixedRate(new Runnable() {

      @Override
      public void run() {
        try {
          doRegisterServiceInfos();
          List<ServiceInfo> t = doGetProvider(null);
          if (t != null && !t.isEmpty()) {
            for (ServiceInfo info : t) {
              serviceInfoCache.put(info.getClassName(), info);
            }
          }
          List<ServiceConfig> t2 = doGetServiceConfig(null);
          if (t2 != null && !t2.isEmpty()) {
            for (ServiceConfig info : t2) {
              serviceConfigCache.put(info.getFlowName(), info);
            }
          }
        } catch (Exception e) {
          logger.error("", e);
        }
      }
    }, 5L, 3L, TimeUnit.SECONDS);
  }


  @Override
  public boolean register(ServiceInfo serviceInfo) {
    serviceInfoCache.putIfAbsent(serviceInfo.getClassName(), serviceInfo);
    return doRegister(serviceInfo);
  }

  @Override
  public List<ServiceInfo> getProvider(ServiceInfo serviceInfo) {
    List<ServiceInfo> ret = doGetProvider(serviceInfo);
    if (ret != null) {
      for (ServiceInfo i : ret) {
        serviceInfoCache.put(i.getClassName(), i);
      }
    }
    return new ArrayList<ServiceInfo>(serviceInfoCache.values());
  }

  private void doRegisterServiceInfos() {
    for (Map.Entry<String, ServiceInfo> entry : serviceInfoCache.entrySet()) {
      doRegister(entry.getValue());
    }
  }

  @Override
  public URL getUrl() {
    return url;
  }


  @Override
  public boolean registerServiceConfig(ServiceConfig serviceConfig) {
    serviceConfigCache.put(serviceConfig.getFlowName(), serviceConfig);
    return doRegisterServiceConfig(serviceConfig);
  }

  @Override
  public List<ServiceConfig> getServiceConfig(ServiceConfig serviceConfig) {
    List<ServiceConfig> ret = doGetServiceConfig(serviceConfig);
    if (ret != null) {
      for (ServiceConfig i : ret) {
        serviceConfigCache.put(i.getFlowName(), i);
      }
    }
    return new ArrayList<ServiceConfig>(serviceConfigCache.values());
  }


  public abstract boolean doRegister(ServiceInfo serviceInfo);

  public abstract boolean doRegisterServiceConfig(ServiceConfig serviceConfig);


  public abstract List<ServiceInfo> doGetProvider(ServiceInfo serviceInfo);

  public abstract List<ServiceConfig> doGetServiceConfig(ServiceConfig serviceConfig);
}
