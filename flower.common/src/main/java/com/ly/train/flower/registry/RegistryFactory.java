/**
 * 
 */
package com.ly.train.flower.registry;

import java.net.URL;

/**
 * @author leeyazhou
 *
 */
public interface RegistryFactory {

  Registry createRegistry(URL url);
}
