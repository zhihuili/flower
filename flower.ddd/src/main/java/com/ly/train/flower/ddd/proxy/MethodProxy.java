package com.ly.train.flower.ddd.proxy;

import java.lang.reflect.Method;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.ddd.exception.CommandInvokeException;

/**
 * @author leeyazhou
 */
public class MethodProxy {
  private Class<?> target;
  private Method method;
  private Object targetInstance;

  public MethodProxy(Class<?> target, Method method) {
    this.target = target;
    this.method = method;
  }

  public Object invoke(Object param, ServiceContext context) {
    try {
      if (targetInstance == null) {
        synchronized (method) {
          if (targetInstance == null) {
            this.targetInstance = target.newInstance();
          }
        }
      }

      Object[] args = new Object[method.getParameterCount()];
      args[0] = param;
      for (int i = 1; i < method.getParameterCount(); i++) {
        if (method.getParameterTypes()[i].equals(ServiceContext.class)) {
          args[i] = context;
        } else {
          args[i] = null;
        }
      }

      return method.invoke(targetInstance, args);
    } catch (Exception e) {
      throw new CommandInvokeException(e);
    }
  }
}
