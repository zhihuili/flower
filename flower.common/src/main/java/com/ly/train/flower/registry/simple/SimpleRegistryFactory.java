/**
 * 
 */
package com.ly.train.flower.registry.simple;

import java.net.URL;
import com.ly.train.flower.registry.Registry;
import com.ly.train.flower.registry.RegistryFactory;

/**
 * @author leeyazhou
 *
 */
public class SimpleRegistryFactory implements RegistryFactory {

  @Override
  public Registry createRegistry(URL url) {
    return new SimpleRegistry(url);
  }
}
