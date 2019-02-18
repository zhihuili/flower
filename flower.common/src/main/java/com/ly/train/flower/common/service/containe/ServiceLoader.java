package com.ly.train.flower.common.service.containe;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ly.train.flower.common.service.FlowerService;

public class ServiceLoader {
  private ClassLoader cl;
  private Map<String, Class> messageTypeMap = new ConcurrentHashMap<String, Class>();

  private static ServiceLoader sl = new ServiceLoader();

  private ServiceLoader() {
    cl = this.getClass().getClassLoader();
  }

  public static ServiceLoader getInstance() {
    return sl;
  }

  public FlowerService loadService(String serviceName) {
    FlowerService service;
    try {
      Class serviceClass = cl.loadClass(ServiceFactory.getServiceClassName(serviceName));
      String param = ServiceFactory.getServiceClassParameter(serviceName);
      if (param != null) {
        Constructor constructor = serviceClass.getConstructor(String.class);
        service = (FlowerService) constructor.newInstance(param);
        return service;
      }
      service = (FlowerService) serviceClass.newInstance();
      return service;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public Class getServiceMessageType(String serviceName) {
    Class messageType = messageTypeMap.get(serviceName);
    if (messageType == null) {
      synchronized (this) {
        messageType = messageTypeMap.get(serviceName);
        if (messageType == null) {
          try {
            Class serviceClass = cl.loadClass(ServiceFactory.getServiceClassName(serviceName));

            messageType = (Class) ((ParameterizedType) serviceClass.getGenericInterfaces()[0])
                .getActualTypeArguments()[0];
            messageTypeMap.put(serviceName, messageType);
          } catch (Exception e) {
            messageType = Object.class;
          }
        }
      }
    }
    return messageType;
  }
}