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

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Predicate;
import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.exception.ServiceNotFoundException;
import com.ly.train.flower.common.service.FlowerService;
import com.ly.train.flower.common.service.impl.AggregateService;
import com.ly.train.flower.common.service.impl.ConditionService;
import com.ly.train.flower.common.service.impl.NothingService;
import com.ly.train.flower.common.util.FileUtil;
import com.ly.train.flower.common.util.StringUtil;

public class ServiceLoader {
  private static final Logger logger = LoggerFactory.getLogger(ServiceLoader.class);
  private final ClassLoader classLoader;

  // <serviceName, Flowservice>
  private volatile ConcurrentMap<String, Object> servicesCache = new ConcurrentHashMap<>();

  private volatile ConcurrentMap<String, ServiceMeta> serviceMetaCache = new ConcurrentHashMap<>();

  private volatile static ServiceLoader serviceLoader = new ServiceLoader();
  private volatile AtomicBoolean init = new AtomicBoolean(false);

  private ServiceLoader() {
    this.classLoader = this.getClass().getClassLoader();

  }

  public static ServiceLoader getInstance() {
    if (serviceLoader.init.compareAndSet(false, true)) {
      try {
        serviceLoader.loadInnerFlowService();
        serviceLoader.loadServiceAndFlow();
      } catch (Exception e) {
        logger.error("", e);
      }
    }
    return serviceLoader;
  }

  public boolean registerFlowerService(String serviceName, FlowerService flowerService) {
    Object ret = servicesCache.putIfAbsent(serviceName, flowerService);
    if (ret != null) {
      logger.warn("flowerservice is already exist, so discard it. serviceName : {}, flowerService : {}", serviceName, flowerService);
    } else {
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
            final String serviceClassName = ServiceFactory.getServiceClassName(serviceName);
            if (StringUtil.isBlank(serviceClassName)) {
              throw new ServiceNotFoundException(serviceClassName);
            }
            Class<?> serviceClass = classLoader.loadClass(serviceClassName);
            String param = ServiceFactory.getServiceClassParameter(serviceName);
            if (param != null) {
              Constructor<?> constructor = serviceClass.getConstructor(String.class);
              service = (FlowerService) constructor.newInstance(param);
            } else {
              service = (FlowerService) serviceClass.newInstance();
            }
            logger.info("load flower service --> {} : {}", serviceName, service);
            servicesCache.put(serviceName, service);
          } catch (Exception e) {
            logger.error("fail to load service : " + serviceName, e);
            throw new FlowerException(e);
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
    return registerServiceType(serviceName, serviceClass, serviceClassName);
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
    ServiceMeta serviceMeta = new ServiceMeta(serviceClass);
    serviceMeta.setServiceName(serviceName);
    try {
      Type[] paramTypes = null;
      Type[] types = serviceClass.getGenericInterfaces();
      if (types != null && types.length > 0)
        paramTypes = ((ParameterizedType) types[0]).getActualTypeArguments();
      else
        paramTypes = ((ParameterizedType) serviceClass.getGenericSuperclass()).getActualTypeArguments();

      // logger.info("参数类型 {} : {} : {}", serviceClass, paramTypes[0], paramTypes[1]);
      Class<?> paramType = null;
      if (paramTypes[0] instanceof ParameterizedType) {
        paramType = (Class<?>) ((ParameterizedType) paramTypes[0]).getRawType();
      } else {
        paramType = (Class<?>) paramTypes[0];
      }
      Class<?> returnType = null;
      if (paramTypes[1] instanceof ParameterizedType) {
        returnType = (Class<?>) ((ParameterizedType) paramTypes[1]).getRawType();
      } else {
        returnType = (Class<?>) paramTypes[1];
      }

      serviceMeta.setParamType(paramType);
      serviceMeta.setResultType(returnType);

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
    serviceMetaCache.put(serviceName, serviceMeta);
  }

  private void loadInnerFlowService() {
    registerServiceType(AggregateService.class.getSimpleName(), AggregateService.class);
    registerServiceType(ConditionService.class.getSimpleName(), ConditionService.class);
    registerServiceType(NothingService.class.getSimpleName(), NothingService.class);

  }

  protected void loadServiceAndFlow() throws Exception {

    Predicate<String> filter = new FilterBuilder().include(".*\\.services").include(".*\\.flow");


    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    configurationBuilder.filterInputsBy(filter).setScanners(new ResourcesScanner());
    configurationBuilder.setUrls(ClasspathHelper.forClassLoader());

    Reflections reflections = new Reflections(configurationBuilder);

    Set<String> servicesFiles = reflections.getResources(Pattern.compile(".*\\.services"));
    for (String path : servicesFiles) {
      logger.info("find service, path : {}", path);
      Map<String, String> services = FileUtil.readService("/" + path);
      for (Map.Entry<String, String> entry : services.entrySet()) {
        registerServiceType(entry.getKey().trim(), entry.getValue().trim());
      }
    }
    Set<String> flowFiles = reflections.getResources(Pattern.compile(".*\\.flow"));
    for (String path : flowFiles) {
      logger.info("find flow file, path : {}", path);
      String flowName = path.substring(0, path.lastIndexOf("."));
      ServiceFlow.getOrCreate(flowName).buildFlow(FileUtil.readFlow("/" + path));
    }

  }


}
