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

  public FlowerService loadService(String serviceClassPath) {
    try {
      FlowerService service = (FlowerService) cl.loadClass(serviceClassPath).newInstance();
      return service;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}