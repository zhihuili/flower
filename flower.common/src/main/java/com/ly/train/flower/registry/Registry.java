/**
 * 
 */
package com.ly.train.flower.registry;

import java.util.List;

/**
 * @author leeyazhou
 *
 */
public interface Registry {

  /**
   * 注册服务
   * 
   * @param serviceInfo {@link ServiceInfo}
   * @return true / false
   */
  boolean register(ServiceInfo serviceInfo);

  /**
   * 查询服务提供者
   * 
   * @param serviceInfo {@link ServiceInfo}
   * @return {@link ServiceInfo}
   */
  List<ServiceInfo> getProvider(ServiceInfo serviceInfo);

}
