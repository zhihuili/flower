package com.ly.train.flower.ddd.gateway;

/**
 * @author leeyazhou
 */
public interface CommandGateway {

  /**
   * send command message
   * 
   * @param <C> message type
   * @param command message
   */
  <C> void send(C command);
}
