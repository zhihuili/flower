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
package com.ly.train.flower.core.service.container;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.annotation.FlowerServiceUtil;
import com.ly.train.flower.common.core.config.ServiceMeta;
import com.ly.train.flower.common.core.proxy.MethodProxy;
import com.ly.train.flower.common.core.service.FlowerService;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.exception.FlowException;
import com.ly.train.flower.common.exception.ServiceNotFoundException;
import com.ly.train.flower.common.io.resource.Resource;
import com.ly.train.flower.common.io.resource.ResourceLoader;
import com.ly.train.flower.common.lifecyle.AbstractInit;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.common.util.FileUtil;
import com.ly.train.flower.common.util.Pair;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.common.util.TypeParameterUtil;
import com.ly.train.flower.core.service.container.util.ServiceLoaderUtil;

public class ServiceLoader extends AbstractInit {
  private static final Logger logger = LoggerFactory.getLogger(ServiceLoader.class);
  private final ClassLoader classLoader;

  // <serviceName, Flowservice>
  private volatile ConcurrentMap<String, MethodProxy> servicesCache = new ConcurrentHashMap<>();

  private volatile ConcurrentMap<String, ServiceMeta> serviceMetaCache = new ConcurrentHashMap<>();

  // private volatile static ServiceLoader serviceLoader = null;
  private ServiceFactory serviceFactory;

  ServiceLoader(ServiceFactory serviceFactory) {
    this.serviceFactory = serviceFactory;
    this.classLoader = this.getClass().getClassLoader();
  }

  @Override
  protected void doInit() {
    try {
      this.loadServiceAndFlowFromFiles();
    } catch (Exception e) {
      logger.error("", e);
    }
  }

  public boolean registerFlowerService(String serviceName, FlowerService flowerService) {
    Method method = FlowerServiceUtil.getProcessMethod(flowerService.getClass());
    if (method != null) {
      MethodProxy methodProxy = new MethodProxy(flowerService, method);
      methodProxy.setComplete(ServiceLoaderUtil.isComplete(flowerService));
      methodProxy.setFlush(ServiceLoaderUtil.isFlush(flowerService));

      MethodProxy pre = servicesCache.putIfAbsent(serviceName, methodProxy);
      if (pre == null) {
        logger.info("register flowerservice success , serviceName : {}, flowerService : {}", serviceName, methodProxy);
      }
    }
    initFlowerServiceOfClass(flowerService);
    return true;
  }


  public MethodProxy loadService(String serviceName) {
    MethodProxy methodProxy = servicesCache.get(serviceName);
    if (methodProxy == null) {
      synchronized (logger) {
        methodProxy = servicesCache.get(serviceName);;
        if (methodProxy == null) {
          try {
            ServiceMeta serviceMeta = loadServiceMeta(serviceName);
            if (serviceMeta == null) {
              throw new ServiceNotFoundException(serviceName);
            }
            final String serviceClassName = serviceMeta.getServiceClassName();
            Class<?> serviceClass = classLoader.loadClass(serviceClassName);
            String param = loadServiceMeta(serviceName).getConfig(0);
            Object flowerService = null;
            if (param != null) {
              Constructor<?> constructor = serviceClass.getConstructor(String.class);
              flowerService = constructor.newInstance(param);
            } else {
              flowerService = serviceClass.newInstance();
            }
            Method method = FlowerServiceUtil.getProcessMethod(serviceClass);
            if (method != null) {
              MethodProxy proxy = new MethodProxy(flowerService, method);
              proxy.setComplete(ServiceLoaderUtil.isComplete(flowerService));
              proxy.setFlush(ServiceLoaderUtil.isFlush(flowerService));
              logger.info("load flower service --> {} : {}", serviceName, proxy);
              servicesCache.put(serviceName, proxy);
            }
            initFlowerServiceOfClass(flowerService);
            methodProxy = servicesCache.get(serviceName);
          } catch (Exception e) {
            throw new FlowException("fail to load service : " + serviceName, e);
          }
        }
      }
    }
    return methodProxy;
  }

  /**
   * @param flowerService flowerService
   */
  private void initFlowerServiceOfClass(Object flowerService) {
    Method[] methods = flowerService.getClass().getDeclaredMethods();
    if (methods == null) {
      return;
    }
    for (Method method : methods) {
      com.ly.train.flower.common.annotation.FlowerService flowerServiceAnno =
          method.getAnnotation(com.ly.train.flower.common.annotation.FlowerService.class);
      if (flowerServiceAnno == null) {
        continue;
      }
      String flowerServiceName = flowerServiceAnno.value();
      if (StringUtil.isBlank(flowerServiceName)) {
        flowerServiceName = flowerService.getClass().getName() + "." + method.getName();
      }
      MethodProxy methodProxy = new MethodProxy(flowerService, method);
      methodProxy.setFlush(flowerServiceAnno.flush());
      methodProxy.setComplete(flowerServiceAnno.complete());
      MethodProxy temp = servicesCache.putIfAbsent(flowerServiceName, methodProxy);
      if (temp == null) {
        logger.info("register flowerservice success , serviceName : {}, flowerService : {}", flowerServiceName,
            methodProxy);
      }
    }
  }

  /**
   * 获取服务参数类型
   * 
   * @param serviceName serviceName
   * @return {@link ServiceMeta}
   */
  public ServiceMeta loadServiceMeta(String serviceName) {
    return serviceMetaCache.get(serviceName);
  }

  public boolean registerServiceType(Class<?> serviceClass) {
    Method[] methods = serviceClass.getDeclaredMethods();
    if (methods == null) {
      return false;
    }
    for (Method method : methods) {
      com.ly.train.flower.common.annotation.FlowerService flowerService =
          method.getAnnotation(com.ly.train.flower.common.annotation.FlowerService.class);
      if (flowerService == null || method.getParameterCount() != 2
          || method.getParameterTypes()[1] != ServiceContext.class) {
        continue;
      }
      String flowerServiceName = flowerService.value();
      if (StringUtil.isBlank(flowerServiceName)) {
        flowerServiceName = serviceClass.getName() + "." + method.getName();
      }
      ServiceMeta serviceMeta = new ServiceMeta();
      serviceMeta.setServiceName(flowerServiceName);
      serviceMeta.setServiceClassName(serviceClass.getName());
      serviceMeta.setFlowerType(FlowerServiceUtil.getFlowerType(serviceClass));
      serviceMeta.setTimeout(FlowerServiceUtil.getTimeout(serviceClass));
      serviceMeta.setMethodName(method.getName());

      serviceMeta.setResultType(method.getReturnType().getName());
      serviceMeta.setParamType(method.getParameterTypes()[0].getName());

      serviceMetaCache.put(flowerServiceName, serviceMeta);
      logger.info("register FlowerService type -> {} : {}", flowerServiceName, serviceClass);
    }
    return true;
  }

  public boolean registerServiceType(String serviceName, String serviceClassName) {
    Class<?> serviceClass = null;
    final String tempClassName = serviceClassName;
    try {
      String[] tt = serviceClassName.trim().split(";");
      if (tt.length > 1) {
        serviceClassName = tt[0].trim();
      }
      serviceClass = classLoader.loadClass(serviceClassName);
    } catch (ClassNotFoundException e) {
      final String msg = "serviceName : " + serviceName + ", serviceClassName : " + serviceClassName;
      logger.error(msg, e);
      return false;
    }
    return registerServiceType(serviceName, serviceClass, tempClassName);
  }

  public boolean registerServiceType(String serviceName, Class<?> serviceClass) {
    return registerServiceType(serviceName, serviceClass, null);
  }

  public boolean registerServiceType(String serviceName, Class<?> serviceClass, String config) {
    if (!serviceMetaCache.containsKey(serviceName)) {
      Method processMethod = FlowerServiceUtil.getProcessMethod(serviceClass);
      if (processMethod != null) {
        ServiceMeta serviceMeta = new ServiceMeta();
        serviceMeta.setServiceName(serviceName);
        serviceMeta.setServiceClassName(serviceClass.getName());
        serviceMeta.setFlowerType(FlowerServiceUtil.getFlowerType(serviceClass));
        serviceMeta.setInnerAggregateService(Constant.AGGREGATE_SERVICE_NAME.equals(serviceClass.getName()));
        serviceMeta.setTimeout(FlowerServiceUtil.getTimeout(serviceClass));

        Pair<Class<?>, Class<?>> paramAndResultType = TypeParameterUtil.getServiceClassParam(serviceClass);
        serviceMeta.setParamType(paramAndResultType.getKey().getName());
        serviceMeta.setResultType(paramAndResultType.getValue().getName());
        if (StringUtil.isNotBlank(config)) {
          String[] tt = config.split(";");
          if (tt.length > 1) {
            for (int i = 1; i < tt.length; i++) {
              serviceMeta.addConfig(tt[i]);
            }
          }
        }
        serviceMetaCache.put(serviceName, serviceMeta);

        logger.info("register FlowerService type -> {} : {}", serviceName, serviceClass);
      }
      registerServiceType(serviceClass);
    }
    return true;
  }



  protected void loadServiceAndFlowFromFiles() throws IOException {
    Resource[] resources = new ResourceLoader("", ".services").getResources();
    for (Resource resource : resources) {
      logger.info("find service, path : {}", resource.getURL());
      List<Pair<String, String>> services = FileUtil.readService(resource);
      for (Pair<String, String> pair : services) {
        boolean flag = registerServiceType(pair.getKey(), pair.getValue());
        if (flag == false) {
          logger.warn("register service error, path : {}, service : {}", resource.getURL(), pair);
        }
      }

    }
    resources = new ResourceLoader("", ".flow").getResources();
    for (Resource resource : resources) {
      logger.info("find flow file, path : {}", resource.getPath());
      String flowName = resource.getName();
      flowName = flowName.substring(0, flowName.lastIndexOf("."));
      ServiceFlow.getOrCreate(flowName, serviceFactory).buildFlow(FileUtil.readFlow(resource));
    }

  }


}
