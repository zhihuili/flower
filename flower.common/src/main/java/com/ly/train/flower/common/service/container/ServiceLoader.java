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
package com.ly.train.flower.common.service.container;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.annotation.FlowerServiceUtil;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.exception.ServiceNotFoundException;
import com.ly.train.flower.common.io.resource.Resource;
import com.ly.train.flower.common.io.resource.ResourceLoader;
import com.ly.train.flower.common.service.FlowerService;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.impl.AggregateService;
import com.ly.train.flower.common.service.impl.ConditionService;
import com.ly.train.flower.common.service.impl.NothingService;
import com.ly.train.flower.common.util.ClassUtil;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.common.util.FileUtil;
import com.ly.train.flower.common.util.Pair;
import com.ly.train.flower.common.util.StringUtil;

public class ServiceLoader extends AbstractInit {
  private static final Logger logger = LoggerFactory.getLogger(ServiceLoader.class);
  private final ClassLoader classLoader;

  // <serviceName, Flowservice>
  private volatile ConcurrentMap<String, Object> servicesCache = new ConcurrentHashMap<>();

  private volatile ConcurrentMap<String, ServiceMeta> serviceMetaCache = new ConcurrentHashMap<>();

  // private volatile static ServiceLoader serviceLoader = null;
  private ServiceFactory serviceFactory;

  ServiceLoader(ServiceFactory serviceFactory) {
    this.serviceFactory = serviceFactory;
    this.classLoader = this.getClass().getClassLoader();
  }

  // @SuppressWarnings("unused")
  // private static ServiceLoader getInstance() {
  // if (serviceLoader == null) {
  // synchronized (logger) {
  // if (serviceLoader == null) {
  // serviceLoader = new ServiceLoader();
  // serviceLoader.init();
  // }
  // }
  // }
  // return serviceLoader;
  // }

  @Override
  protected void doInit() {
    // this.loadInnerFlowService();
    try {
      this.loadServiceAndFlow();
    } catch (Exception e) {
      logger.error("", e);
    }
  }

  public boolean registerFlowerService(String serviceName, FlowerService flowerService) {
    Object ret = servicesCache.putIfAbsent(serviceName, flowerService);
    if (ret == null) {
      logger.info("register flowerservice success , serviceName : {}, flowerService : {}", serviceName, flowerService);
    }
    return true;
  }

  public FlowerService loadService(String serviceName) {
    FlowerService service = (FlowerService) servicesCache.get(serviceName);
    if (service == null) {
      synchronized (logger) {
        service = (FlowerService) servicesCache.get(serviceName);;
        if (service == null) {
          try {
            ServiceMeta serviceMeta = loadServiceMeta(serviceName);
            if (serviceMeta == null) {
              throw new ServiceNotFoundException(serviceName);
            }
            final String serviceClassName = serviceMeta.getServiceClassName();
            Class<?> serviceClass = classLoader.loadClass(serviceClassName);
            String param = loadServiceMeta(serviceName).getConfig(0);
            if (param != null) {
              Constructor<?> constructor = serviceClass.getConstructor(String.class);
              service = (FlowerService) constructor.newInstance(param);
            } else {
              service = (FlowerService) serviceClass.newInstance();
            }
            logger.info("load flower service --> {} : {}", serviceName, service);
            servicesCache.put(serviceName, service);
          } catch (Exception e) {
            throw new FlowerException("fail to load service : " + serviceName, e);
          }
        }
      }
    }
    return service;
  }

  /**
   * 获取服务参数类型
   * 
   * @param serviceName
   * @return {@link ServiceMeta}
   */
  public ServiceMeta loadServiceMeta(String serviceName) {
    return serviceMetaCache.get(serviceName);
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
      initServiceMeta(serviceName, serviceClass, config);
      logger.info("register service type -> {} : {}", serviceName, serviceClass);
    }
    return true;
  }

  private void initServiceMeta(String serviceName, Class<?> serviceClass, String config) {
    ServiceMeta serviceMeta = new ServiceMeta();
    serviceMeta.setServiceName(serviceName);
    serviceMeta.setServiceClassName(serviceClass.getName());
    serviceMeta.setAggregateService(FlowerServiceUtil.isAggregateType(serviceClass));
    serviceMeta.setInnerAggregateService(Constant.AGGREGATE_SERVICE_NAME.equals(serviceClass.getName()));
    serviceMeta.setTimeout(FlowerServiceUtil.getTimeout(serviceClass));
    try {
      Pair<Class<?>, Class<?>> params = getServiceClassParam(serviceClass);
      serviceMeta.setParamType(params.getKey().getName());
      serviceMeta.setResultType(params.getValue().getName());

      if (StringUtil.isNotBlank(config)) {
        String[] tt = config.split(";");
        if (tt.length > 1) {
          for (int i = 1; i < tt.length; i++) {
            serviceMeta.addConfig(tt[i]);
          }
        }
      }

    } catch (Exception e) {
      logger.error("init service meta, serviceName : " + serviceName + ", serviceClass : " + serviceClass, e);
    }
    logger.debug("init ServiceMeta. {} : {}", serviceName, serviceMeta);
    serviceMetaCache.put(serviceName, serviceMeta);
  }

  private Pair<Class<?>, Class<?>> getServiceClassParam(Class<?> serviceClass) {
    Type[] paramTypes = null;
    Type[] types = serviceClass.getGenericInterfaces();
    if (types != null) {
      for (Type type : types) {
        if (type instanceof ParameterizedType) {
          Class<?> a = ClassUtil.forName(((ParameterizedType) type).getRawType().getTypeName());
          if (Service.class.isAssignableFrom(a)) {
            paramTypes = ((ParameterizedType) type).getActualTypeArguments();
            break;
          }
        }
      }
    }
    if (paramTypes == null) {
      Type type = serviceClass.getGenericSuperclass();
      if (type instanceof ParameterizedType) {
        paramTypes = ((ParameterizedType) type).getActualTypeArguments();
      }
    }

    Class<?> paramType = Object.class;
    Class<?> returnType = Object.class;
    if (paramTypes != null) {
      if (paramTypes[0] instanceof ParameterizedType) {
        paramType = (Class<?>) ((ParameterizedType) paramTypes[0]).getRawType();
      } else {
        paramType = (Class<?>) paramTypes[0];
      }

      if (paramTypes[1] instanceof ParameterizedType) {
        returnType = (Class<?>) ((ParameterizedType) paramTypes[1]).getRawType();
      } else {
        returnType = (Class<?>) paramTypes[1];
      }
    }
    return new Pair<Class<?>, Class<?>>(paramType, returnType);
  }

  protected void loadInnerFlowService() {
    registerServiceType(AggregateService.class.getSimpleName(), AggregateService.class);
    registerServiceType(ConditionService.class.getSimpleName(), ConditionService.class);
    registerServiceType(NothingService.class.getSimpleName(), NothingService.class);
  }

  protected void loadServiceAndFlow() throws IOException {
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
