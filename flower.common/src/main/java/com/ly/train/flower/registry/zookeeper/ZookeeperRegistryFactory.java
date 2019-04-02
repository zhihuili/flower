/**
 * 
 */
package com.ly.train.flower.registry.zookeeper;

import java.net.URL;
import com.ly.train.flower.registry.Registry;
import com.ly.train.flower.registry.RegistryFactory;

/**
 * @author leeyazhou
 *
 */
public class ZookeeperRegistryFactory implements RegistryFactory {


  @Override
  public Registry createRegistry(URL url) {
    return new ZookeeperRegistry(url);
  }

}
