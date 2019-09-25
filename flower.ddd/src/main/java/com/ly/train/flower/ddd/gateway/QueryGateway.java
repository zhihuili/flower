package com.ly.train.flower.ddd.gateway;

/**
 * @author leeyazhou
 */
public interface QueryGateway {

  /**
   * query
   * 
   * @param <Q> query type
   * @param query query message
   */
  <Q> void query(Q query);
}
