/**
 * 
 */
package com.ly.train.flower.registry.zookeeper;

import java.net.URL;
import java.util.List;
import com.ly.train.flower.registry.Registry;
import com.ly.train.flower.registry.ServiceInfo;

/**
 * @author leeyazhou
 *
 */
public class ZookeeperRegistry implements Registry {

  private final URL url;

  public ZookeeperRegistry(URL url) {
    this.url = url;
  }

  @Override
  public boolean register(ServiceInfo serviceInfo) {
    return false;
  }

  @Override
  public List<ServiceInfo> getProvider(ServiceInfo serviceInfo) {
    return null;
  }

}
