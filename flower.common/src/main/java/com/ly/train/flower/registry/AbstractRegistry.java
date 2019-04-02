/**
 * 
 */
package com.ly.train.flower.registry;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public abstract class AbstractRegistry implements Registry {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  protected ConcurrentMap<String, ServiceInfo> serviceInfoCache = new ConcurrentHashMap<>();

  @Override
  public boolean register(ServiceInfo serviceInfo) {
    serviceInfoCache.putIfAbsent(serviceInfo.getClassName(), serviceInfo);
    return doRegister(serviceInfo);
  }

  @Override
  public List<ServiceInfo> getProvider(ServiceInfo serviceInfo) {
    return doGetProvider(serviceInfo);
  }

  public abstract boolean doRegister(ServiceInfo serviceInfo);


  public abstract List<ServiceInfo> doGetProvider(ServiceInfo serviceInfo);
}
