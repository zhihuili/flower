package com.ly.train.flower.core.filter.impl;

import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.filter.AbstractFilter;
import com.ly.train.flower.filter.FilterChain;

/**
 * @author leeyazhou
 */
public class MockOneFilter extends AbstractFilter {
  private static final Logger logger = LoggerFactory.getLogger(MockOneFilter.class);

  @Override
  public Object doFilter(Object message, ServiceContext context, FilterChain filterChain) {
    logger.info("Before 过滤：" + message);
    try {
      return filterChain.doFilter(message, context);
    } finally {
      logger.info("After 过滤" + message);
    }
  }

  @Override
  public int getOrder() {
    return 1;
  }
}
