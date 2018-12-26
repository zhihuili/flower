package com.ly.train.flower.common.service;

public class ServiceLoader {
  private ClassLoader cl;

  private static ServiceLoader sl = new ServiceLoader();

  private ServiceLoader() {
    cl = this.getClass().getClassLoader();
  }

  public static ServiceLoader getInstance() {
    return sl;
  }

  public Service loadService(String serviceClassPath) {
    try {
      Service service = (Service) cl.loadClass(serviceClassPath).newInstance();
      return service;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}