/**
 * 
 */
package com.ly.train.flower.config.parser;

/**
 * @author leeyazhou
 *
 */
public interface ConfigParser<T> {

  /**
   * 解析配置文件
   * @return {@link T}
   */
  T parse();
}
