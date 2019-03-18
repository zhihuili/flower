/**
 * 
 */
package com.ly.train.flower.logging;

import com.ly.train.flower.logging.slf4j.Slf4jImpl;

/**
 * @author leeyazhou
 *
 */
public class LoggerFactory {

  public static Logger getLogger(Class<?> clazz) {
    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(clazz);
    return new Slf4jImpl(log);
  }
}
